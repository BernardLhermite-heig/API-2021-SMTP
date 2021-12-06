package ch.heigvd.api.mailrobot.smtp;

import ch.heigvd.api.mailrobot.model.mail.Message;
import ch.heigvd.api.mailrobot.model.mail.Person;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Stéphane Marengo
 * @author Loris Marzullo
 */
class SmtpClientTest {
    private static final String DOMAIN = "prank";
    private static final String HOST = "localhost";
    private static final int PORT = 25;

    @Test
    public void itShouldThrowOnInvalidPort() {
        assertThrows(IllegalArgumentException.class, () -> new SmtpClient(HOST, -25, DOMAIN));
        assertThrows(IllegalArgumentException.class, () -> new SmtpClient(HOST, 0, DOMAIN));
    }

    @Test
    public void itShouldThrowOnMissingRecipients() {
        Person person = new Person("firstname", "lastname", "email");
        Message m = new Message(person, "Subject", "Body");
        SmtpClient client = new SmtpClient(HOST, PORT, DOMAIN);
        assertThrows(IllegalArgumentException.class, () -> client.send(m));
    }
}