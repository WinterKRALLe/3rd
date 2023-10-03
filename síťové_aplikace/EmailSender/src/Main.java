import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Sending email...");
        try {
            EmailSender sender = new EmailSender("smtp.utb.cz", 25);
            sender.send("you@utb.cz", "m_bereznaj@utb.cz", "Email from Java", "Funguje to?\nSnad...");
            sender.close();
            System.out.println("Email sent...");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
