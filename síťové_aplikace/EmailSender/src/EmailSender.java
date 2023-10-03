import java.net.*;
import java.io.*;

public class EmailSender {
    /*
     * Constructor opens Socket to host/port. If the Socket throws an exception during opening,
     * the exception is not handled in the constructor.
     */
    Socket s;
    OutputStream os;
    public EmailSender(String host, int port) throws IOException {
        s = new Socket(host, port);
        os = s.getOutputStream();
    }
    /*
     * sends email from an email address to an email address with some subject and text.
     * If the Socket throws an exception during sending, the exception is not handled by this method.
     */
    public void send(String from, String to, String subject, String text) throws IOException {
        os.write(("HELO pcMarek\r\n").getBytes());
        os.write(("MAIL FROM: " + from + "\r\n").getBytes());
        os.write(("RCPT TO: " + to + "\r\n").getBytes());
        os.write("DATA\r\n".getBytes());
        os.write(("Subject: " + subject + "\r\n").getBytes());
        os.write((text + "\r\n.\r\n").getBytes());
        quit();
    }

    private void quit() throws IOException {
        os.write("QUIT\r\n".getBytes());
    }

    /*
     * sends QUIT and closes the socket
     */
    public void close() throws IOException {
        s.close();
    }
}
