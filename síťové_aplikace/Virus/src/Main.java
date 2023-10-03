import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class myThread extends Thread {
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";

    Socket s;

    public myThread(String hostname, int port) throws IOException {
        s = new Socket(hostname, port);
    }

    int total = 0;

    public void run() {
        try {
            OutputStream outputS = s.getOutputStream();
            InputStream inputS = s.getInputStream();
            byte[] request = ("GET / HTTP/1.0\r\nConnection: keep-alive\r\nHost: localhost\r\n\r\n").getBytes();
            byte[] buffer = new byte[2048];
            int count;
            for (int i = 0; i < 20; i++) {
                outputS.write(request);
                outputS.flush();
                sleep(5);
                if (inputS.available() > 0) {
                    count = inputS.read(buffer);
                    total += count;
                }
            }
            s.close();
            System.out.println("[  " + ANSI_GREEN + "OK" + ANSI_RESET + "  ] Thread's finished.");
            System.out.println("Read " + total / 1024 + "kB");
        } catch (IOException | InterruptedException e) {
            System.out.println("[  " + ANSI_GREEN + "OK" + ANSI_RESET + "  ] Thread's finished via Exception.");
        }
    }

}


public class Main {
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Hostname & port are required.");
            return;
        }
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            for (int i = 0; i < 10; i++) {
                myThread thread = new myThread(hostname, port);
                thread.start();
            }
            System.out.println("[  " + ANSI_GREEN + "OK" + ANSI_RESET + "  ] Connected to " + hostname + ":" + port);
        } catch (IOException e) {
            System.out.println("[  " + ANSI_RED + "ERROR" + ANSI_RESET + "  ] Closed via Exception.");
        }
    }
}