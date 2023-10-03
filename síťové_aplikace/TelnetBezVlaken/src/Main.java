import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("hostname & port are required");
            return;
        }
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            Socket s = new Socket(hostname, port);
            System.out.println("Connected to " + hostname + ":" + port);
            InputStream inp = s.getInputStream();
            OutputStream out = s.getOutputStream();
            while (true) {
                int pocet;
                byte[] buffer = new byte[2048];
                if (System.in.available() > 0) {
                    pocet = System.in.read(buffer);
                    if (pocet == -1) break;
                    out.write(buffer, 0, pocet);
                    out.flush();
                }
                if (inp.available() > 0) {
                    pocet = inp.read(buffer);
                    System.out.write(buffer, 0, pocet);
                    out.flush();
                    if (buffer[2] == '1') {
                        System.out.println("Disconnected.");
                        break;
                    }
                }
                Thread.sleep(10);
            }
            s.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}