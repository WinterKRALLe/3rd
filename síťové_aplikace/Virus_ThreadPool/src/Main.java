import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Reader implements Runnable {
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    Socket socket;

    public Reader(Socket s) {
        this.socket = s;
    }

    public void run() {
        InputStream inputS = null;
        try {
            inputS = socket.getInputStream();
            int total = 0, count;
            byte[] buffer = new byte[10000];

            while ((count = inputS.read(buffer)) != -1)
                total += count;

            System.out.println("[  " + ANSI_GREEN + "OK" + ANSI_RESET + "  ] Reader has finished.");
            socket.close();

        } catch (IOException e) {
            System.out.println("[  " + ANSI_YELLOW + "WARNING" + ANSI_RESET + "  ] Thread's finished via Exception.");
        }
    }
}

class MyTask implements Runnable {
    String hostname;
    int port;
    static ExecutorService executor = Executors.newFixedThreadPool(100);
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    public MyTask(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    int total = 0;

    public void run() {
        try (Socket s = new Socket(hostname, port)) {
            Reader rTask = new Reader(s);
            executor.execute(rTask);

            OutputStream outputS = s.getOutputStream();

            byte[] request = ("GET / HTTP/1.0\r\nConnection: keep-alive\r\nHost: localhost\r\n\r\n").getBytes();

            for (int i = 0; i < 100; i++) {
                outputS.write(request);
                outputS.flush();
                total += request.length;
            }

            System.out.println("[  " + ANSI_GREEN + "OK" + ANSI_RESET + "  ] Thread's finished.");
            System.out.println("Read " + total / 1024 + "kB");

        } catch (IOException e) {
            System.out.println("[  " + ANSI_YELLOW + "WARNING" + ANSI_RESET + "  ] Thread's finished via Exception.");
        }
    }
}

public class Main {
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Hostname & port are required.");
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        ExecutorService executor = Executors.newFixedThreadPool(100);
        MyTask.executor = executor;
        MyTask task = new MyTask(hostname, port);

        System.out.println("[  " + ANSI_GREEN + "OK" + ANSI_RESET + "  ] Connected to " + hostname + ":" + port);

        for (int i = 0; i < 100; i++) {
            executor.execute(task);
            System.out.println("[  " + ANSI_GREEN + "OK" + ANSI_RESET + "  ] Task " + i + " has started.");
        }

        System.out.println("[  " + ANSI_GREEN + "OK" + ANSI_RESET + "  ] All tasks are running.");
    }
}
