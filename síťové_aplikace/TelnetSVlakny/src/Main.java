import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class myThread extends Thread {
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";

    Socket s;

    public myThread(Socket s) {
        this.s = s;
    }

    public void run() {
        try {
            OutputStream outputS = s.getOutputStream();
            byte[] buffer = new byte[2048];
            int pocet;
            while ((pocet = System.in.read(buffer)) != -1) {
                outputS.write(buffer, 0, pocet);
                outputS.flush();
                sleep(100);
                if (s.isClosed()) return;
            }
            s.close();
            System.out.println("[  " + ANSI_GREEN + "OK" + ANSI_RESET + "  ] Thread's finished");
        } catch (IOException e) {
            System.err.println("[  " + ANSI_GREEN + "OK" + ANSI_RESET + "  ] Thread's finished via Exception");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}


public class Main {
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("hostname & port are required");
            return;
        }
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            Socket s = new Socket(hostname, port);
            myThread thread = new myThread(s);
            thread.start();
            System.out.println("[  " + ANSI_GREEN + "OK" + ANSI_RESET + "  ] Connected to " + hostname + ":" + port);
            InputStream inp = s.getInputStream();
            byte[] buffer = new byte[2048];
            int pocet;
            while ((pocet = inp.read(buffer)) != -1) {
                System.out.write(buffer, 0, pocet);
                System.out.flush();
            }
            s.close();
            System.out.println("[  " + ANSI_GREEN + "OK" + ANSI_RESET + "  ] Disconnected");
        } catch (IOException e) {
            System.out.println("[  " + ANSI_RED + "ERROR" + ANSI_RESET + "  ] " + e);
        }
    }
}