import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.*;

class Room {
    String name;
    Set<client> users;

    public Room(String name) {
        this.name = name;
        this.users = ConcurrentHashMap.newKeySet(0);
    }

    public void addUser(client user) {
        users.add(user);
    }

    public void removeUser(client user) {
        users.remove(user);
    }

    public void broadcast(String message, client sender) {
        for (client c : users) {
            if (c != sender) {
                boolean status = c.frontaZprav.offer(message);
                if (!status) {
                    System.err.println("Fronta je plná");
                }
            }
        }
    }
}

class serverManager implements Runnable {
    static Set<client> set = ConcurrentHashMap.newKeySet(0);
    static Set<Room> roomSet = ConcurrentHashMap.newKeySet(0);

    public void add(client t) {
        set.add(t);
    }

    public void addClient(client c) {
        set.add(c);
    }

    public void removeClient(client c) {
        set.remove(c);
        // Remove the client from all rooms
        for (Room room : roomSet) {
            room.removeUser(c);
        }
    }

    public static void addRoom(Room room) {
        roomSet.add(room);
    }

    public void removeRoom(Room room) {
        roomSet.remove(room);
    }

    public void run() {
        while (true) {
            try {
                Iterator<client> i = set.iterator();
                long time = System.currentTimeMillis();
                while (i.hasNext()) {
                    client c = i.next();
                    if (time - c.inputTask.timeOfLastActivity > 150000) {
                        System.out.println("Killing inactive task: " + c.s);
                        c.s.close();
                        c.outputTaskFuture.cancel(true);
                        i.remove();
                    }
                }
                Thread.sleep(1000);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class FakeDatabase {
    protected static ConcurrentHashMap<String, UserInfo> userDatabase = new ConcurrentHashMap<>();

    protected static class UserInfo {
        String loginName;
        String salt;
        String hashedPassword;

        public UserInfo(String loginName, String salt, String hashedPassword) {
            this.loginName = loginName;
            this.salt = salt;
            this.hashedPassword = hashedPassword;
        }
    }

    public static String generateSalt() {
        String[] words = {"apple", "banana", "orange", "grape", "kiwi", "pineapple", "mango", "strawberry"};
        Random random = new SecureRandom();
        int index = random.nextInt(words.length);
        return words[index];
    }

    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            String saltedPassword = salt + password;
            byte[] hashedBytes = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static void addUser(String loginName, String password) {
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        userDatabase.put(loginName, new UserInfo(loginName, salt, hashedPassword));
    }

    public static void handleChangePassword(String newPassword, UserInfo userInfo, OutputStream out) throws IOException {
        String newSalt = generateSalt();
        String newHashedPassword = hashPassword(newPassword, newSalt);

        userDatabase.put(userInfo.loginName, new UserInfo(userInfo.loginName, newSalt, newHashedPassword));

        out.write("Password changed\r\n".getBytes());
        out.flush();
    }

    public static void printUserDatabase() {
        System.out.println("User Database:");

        for (Map.Entry<String, UserInfo> entry : userDatabase.entrySet()) {
            UserInfo userInfo = entry.getValue();
            System.out.println("Login Name: " + userInfo.loginName);
            System.out.println("Salt: " + userInfo.salt);
            System.out.println("Hashed Password: " + userInfo.hashedPassword);
            System.out.println("-------------");
        }
    }
}

class client {
    private boolean isAuthenticated = false;
    public Future<?> outputTaskFuture;
    Socket s;

    socketInputTask inputTask;
    socketOutputTask outputTask;

    String name;

    public String getName() {
        return name;
    }

    ArrayBlockingQueue<String> frontaZprav = new ArrayBlockingQueue<String>(1024);

    Thread outputTaskThread;

    public client(Socket s) {
        this.s = s;
        inputTask = new socketInputTask();
        outputTask = new socketOutputTask();
        name = s.toString();
    }

    class socketOutputTask implements Runnable {
        public void run() {
            try {
                var out = s.getOutputStream();
                while (true) {
                    String zprava = frontaZprav.take();
                    out.write(zprava.getBytes());
                    out.flush();
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class socketInputTask implements Runnable {
        long timeOfLastActivity = System.currentTimeMillis();
        private FakeDatabase.UserInfo userInfo;

        public void run() {
            try {
                var in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                var out = s.getOutputStream();
                String line;
                while ((line = in.readLine()) != null) {
                    long curTime = System.currentTimeMillis();
                    double delta = (curTime - timeOfLastActivity) / 1000.0;
                    if (delta > 0 && line.length() / delta < 1) {
                        System.err.println("Zrychli nebo zemři!");
                    }
                    timeOfLastActivity = curTime;

                    if (!isAuthenticated) {
                        if (line.startsWith("#login")) {
                            String[] parts = line.split(" ", 3);
                            if (parts.length == 3) {
                                String username = parts[1];
                                String password = parts[2];
                                handleLogin(username, password, out);
                                name = username;
                            }
                        } else {
                            out.write("Please login first.\r\n".getBytes());
                            out.flush();
                        }
                    } else {
                        if (line.startsWith("#setMyName")) {
                            String[] parts = line.split(" ", 2);
                            if (parts.length >= 2) {
                                name = parts[1];
                            }
                        } else if (line.startsWith("#changepswd")) {
                            String[] parts = line.split(" ", 2);
                            if (parts.length == 2) {
                                String newPassword = parts[1];
                                handleChangePassword(newPassword, out);
                            }
                        } else if (line.startsWith("#pm")) {
                            String[] parts = line.split(" ", 3);
                            if (parts.length == 3) {
                                String targetName = parts[1];
                                String message = parts[2].trim();
                                for (client c : serverManager.set) {
                                    if (c.getName().equals(targetName)) {
                                        String formattedMessage = name + " (private): " + message + "\r\n";
                                        boolean status = c.frontaZprav.offer(formattedMessage);
                                        if (!status) {
                                            System.err.println("Fronta je plná");
                                        }
                                        break;
                                    }
                                }
                            }
                        } else if (line.startsWith("#joinRoom")) {
                            String[] parts = line.split(" ", 2);
                            if (parts.length >= 2) {
                                String roomName = parts[1].trim();
                                Room room = findOrCreateRoom(roomName);
                                room.addUser(client.this);
                                System.out.println(name + " joined room: " + roomName);
                            }
                        } else if (line.startsWith("#leaveRoom")) {
                            String[] parts = line.split(" ", 2);
                            if (parts.length >= 2) {
                                String roomName = parts[1].trim();
                                Room room = findRoom(roomName);
                                if (room != null) {
                                    room.removeUser(client.this);
                                    System.out.println(name + " left room: " + roomName);
                                }
                            }
                        } else if (line.startsWith("#listRooms")) {
                            // List all rooms
                            for (Room room : serverManager.roomSet) {
                                out.write((room.name + "\r\n").getBytes());
                                out.flush();
                            }
                        } else if (line.startsWith("#listUsers")) {
                            // List users in a specific room
                            String[] parts = line.split(" ", 2);
                            if (parts.length >= 2) {
                                String roomName = parts[1].trim();
                                Room room = findRoom(roomName);
                                if (room != null) {
                                    for (client user : room.users) {
                                        out.write((user.getName() + "\r\n").getBytes());
                                        out.flush();
                                    }
                                }
                            } else {
                                System.err.println("Neplatný formát soukromé zprávy.");
                            }
                        } else {
                            // Handle messages differently based on whether the user is in a room
                            Room currentRoom = findUsersRoom();
                            if (currentRoom != null) {
                                String formattedMessage = currentRoom.name + ": " + name + ": " + line.trim() + "\r\n";
                                for (client c : currentRoom.users) {
                                    boolean status = c.frontaZprav.offer(formattedMessage);
                                    if (!status) {
                                        System.err.println("Fronta je plná");
                                    }
                                }
                            } else {
                                // Handle messages for users not in any room differently
                                String formattedMessage = "Global: " + name + ": " + line.trim() + "\r\n";
                                for (client c : serverManager.set) {
                                    if (c != client.this) {
                                        boolean status = c.frontaZprav.offer(formattedMessage);
                                        if (!status) {
                                            System.err.println("Fronta je plná");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                s.close();
                System.out.println("Task končí, klient ukončil spojení");
            } catch (IOException e) {
                System.err.println("Task končí v IOException - asi watchdog.");
            }
        }
        private void handleLogin(String username, String password, OutputStream out) throws IOException {
            FakeDatabase.UserInfo userInfo = FakeDatabase.userDatabase.get(username);
            if (userInfo != null && isValidLogin(userInfo, password)) {
                out.write(("Login successful for user: " + username + "\r\n").getBytes());
                out.flush();
                isAuthenticated = true;
                this.userInfo = userInfo;
            } else {
                out.write(("Login failed for user: " + username + "\r\n").getBytes());
                out.flush();
            }
        }

        private boolean isValidLogin(FakeDatabase.UserInfo userInfo, String password) {
            return userInfo.hashedPassword.equals(FakeDatabase.hashPassword(password, userInfo.salt));
        }
        private void handleChangePassword(String newPassword, OutputStream out) throws IOException {
            if (isAuthenticated) {
                String newSalt = FakeDatabase.generateSalt();
                String newHashedPassword = FakeDatabase.hashPassword(newPassword, newSalt);

                FakeDatabase.UserInfo existingUserInfo = FakeDatabase.userDatabase.get(userInfo.loginName);
                existingUserInfo.salt = newSalt;
                existingUserInfo.hashedPassword = newHashedPassword;

                out.write("Password changed\r\n".getBytes());
                out.flush();
            } else {
                out.write("User not authenticated\r\n".getBytes());
                out.flush();
            }
        }
    }

    private Room findRoom(String roomName) {
        for (Room room : serverManager.roomSet) {
            if (room.name.equals(roomName)) {
                return room;
            }
        }
        return null;
    }

    private Room findOrCreateRoom(String roomName) {
        Room room = findRoom(roomName);
        if (room == null) {
            room = new Room(roomName);
            serverManager.addRoom(room);
        }
        return room;
    }

    private Room findUsersRoom() {
        for (Room room : serverManager.roomSet) {
            if (room.users.contains(client.this)) {
                return room;
            }
        }
        return null;
    }

}

public class IMS {

    public static void main(String[] args) {

        FakeDatabase.addUser("alice", "password123");
        FakeDatabase.addUser("bob", "securepass");

        // FakeDatabase.printUserDatabase();

        try {
            ServerSocket serverSock = new ServerSocket(8888);
            System.out.println("Server spusten na portu 8888");
            var executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
            serverManager watchdog = new serverManager();
            executor.execute(watchdog);
            for (; ; ) {
                Socket s = serverSock.accept();
                System.out.println("Pripojil se klient " + s);
                client c = new client(s);
                watchdog.add(c);
                executor.execute(c.inputTask);
                c.outputTaskFuture = executor.submit(c.outputTask);
                int pocetTasku = executor.getActiveCount();
                System.out.println("Active task count = " + pocetTasku);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
