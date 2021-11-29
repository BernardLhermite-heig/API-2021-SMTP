package ch.heigvd.api.mailrobot.smtp;

import ch.heigvd.api.mailrobot.config.ConfigurationManager;
import ch.heigvd.api.mailrobot.model.mail.Message;
import ch.heigvd.api.mailrobot.model.mail.Person;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Stéphane Marengo
 * @author Loris Marzullo
 */
class SmtpClientTest {
    private static final int INVALID_PORT = -25;
    private static final String CONFIG_FILE = "config.properties";
    private static final String MESSAGE_FILE = "messages.txt";
    private static final String TARGET_FILE = "targets.txt";
    private static final String DOMAIN = "prank";
    private static ConfigurationManager manager;
    private static SmtpClient client;

    @BeforeAll
    public static void init() throws IOException {
        manager = new ConfigurationManager(CONFIG_FILE, MESSAGE_FILE, TARGET_FILE);
        client = new SmtpClient(manager.getServerAddress(), manager.getServerPort());
    }

    @Test
    public void itShouldThrowOnInvalidPort() {
        assertThrows(IllegalArgumentException.class, () -> new SmtpClient(manager.getServerAddress(), INVALID_PORT));
    }

    @Test
    public void itShouldThrowOnMissingRecipients() {
        Message m = new Message(manager.getPersons().get(0), "Subject", "Body");
        assertThrows(IllegalArgumentException.class, () -> client.send(m, DOMAIN));
    }

    @Test
    public void itShouldThrowOnMissingExpeditorAddress() {
        Message m = new Message(new Person(new String[]{"", "", ""}), "Subject", "Body");
        m.addRecipient(manager.getPersons().get(0));
        assertThrows(IllegalArgumentException.class, () -> client.send(m, DOMAIN));
    }
}