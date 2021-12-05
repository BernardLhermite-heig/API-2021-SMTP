package ch.heigvd.api.mailrobot.config;

import ch.heigvd.api.mailrobot.TestWithFiles;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.MissingFormatArgumentException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Stéphane Marengo
 * @author Loris Marzullo
 */
class ConfigurationManagerTest extends TestWithFiles {
    private static final String ADDRESS = "localhost";
    private static final int PORT = 25;
    private static final int NB_GROUPS = 2;
    private static final String DOMAIN = "prank";
    private static final String[] WITNESS_ADDRESSES = {"witness.address@heig-vd.ch", "witness2.address@heig-vd.ch"};
    private static final int MESSAGES_COUNT = 4;
    private static final int TARGETS_COUNT = 9;

    @Test
    void itShouldCorrectlyReadTheFiles() throws IOException {
        ConfigurationManager manager = new ConfigurationManager(CONFIG_DIR, CONFIG_FILE, MESSAGES_FILE, TARGETS_FILE);

        assertEquals(ADDRESS, manager.getServerAddress());
        assertEquals(PORT, manager.getServerPort());
        assertEquals(NB_GROUPS, manager.getNumberOfGroups());
        assertEquals(DOMAIN, manager.getDomain());
        assertArrayEquals(WITNESS_ADDRESSES, manager.getWitnessesEmails().toArray());
        assertEquals(MESSAGES_COUNT, manager.getMessages().size());
        assertEquals(TARGETS_COUNT, manager.getTargets().size());
    }

    @Test
    void itShouldThrowOnInvalidFile() {
        assertThrows(IOException.class, () -> new ConfigurationManager(CONFIG_DIR, UNKNOWN_FILE, EMPTY_FILE, EMPTY_FILE));
        assertThrows(IOException.class, () -> new ConfigurationManager(CONFIG_DIR, CONFIG_FILE, UNKNOWN_FILE, EMPTY_FILE));
        assertThrows(IOException.class, () -> new ConfigurationManager(CONFIG_DIR, CONFIG_FILE, EMPTY_FILE, UNKNOWN_FILE));
    }

    @Test
    void itShouldThrowOnInvalidServerAddress() throws IOException {
        editConfig("serverAddress", "");

        assertThrows(MissingFormatArgumentException.class,
                () -> new ConfigurationManager(CONFIG_DIR, CONFIG_FILE, MESSAGES_FILE, TARGETS_FILE));
    }

    @Test
    void itShouldThrowOnInvalidPort() throws IOException {
        String key = "serverPort";

        editConfig(key, "");
        assertThrows(MissingFormatArgumentException.class,
                () -> new ConfigurationManager(CONFIG_DIR, CONFIG_FILE, MESSAGES_FILE, TARGETS_FILE));

        editConfig(key, "-25");
        assertThrows(MissingFormatArgumentException.class,
                () -> new ConfigurationManager(CONFIG_DIR, CONFIG_FILE, MESSAGES_FILE, TARGETS_FILE));

        editConfig(key, "2s");
        assertThrows(MissingFormatArgumentException.class,
                () -> new ConfigurationManager(CONFIG_DIR, CONFIG_FILE, MESSAGES_FILE, TARGETS_FILE));
    }

    @Test
    void itShouldThrowOnInvalidNumberOfGroups() throws IOException {
        String key = "numberOfGroups";

        editConfig(key, "");
        assertThrows(MissingFormatArgumentException.class,
                () -> new ConfigurationManager(CONFIG_DIR, CONFIG_FILE, MESSAGES_FILE, TARGETS_FILE));

        editConfig(key, "-25");
        assertThrows(MissingFormatArgumentException.class,
                () -> new ConfigurationManager(CONFIG_DIR, CONFIG_FILE, MESSAGES_FILE, TARGETS_FILE));

        editConfig(key, "2s");
        assertThrows(MissingFormatArgumentException.class,
                () -> new ConfigurationManager(CONFIG_DIR, CONFIG_FILE, MESSAGES_FILE, TARGETS_FILE));
    }

    @Test
    void itShouldThrowOnInvalidWitnesses() throws IOException {
        String key = "witnessesEmails";

        editConfig(key, "");
        assertThrows(MissingFormatArgumentException.class,
                () -> new ConfigurationManager(CONFIG_DIR, CONFIG_FILE, MESSAGES_FILE, TARGETS_FILE));

        editConfig(key, "invalid email");
        assertThrows(MissingFormatArgumentException.class,
                () -> new ConfigurationManager(CONFIG_DIR, CONFIG_FILE, MESSAGES_FILE, TARGETS_FILE));

        editConfig(key, "witness.address@heig-vd.ch;witness2.address@heig-vd.ch");
        assertThrows(MissingFormatArgumentException.class,
                () -> new ConfigurationManager(CONFIG_DIR, CONFIG_FILE, MESSAGES_FILE, TARGETS_FILE));
    }

    @Test
    void itShouldThrowOnEmptyMessages() {
        assertThrows(MissingFormatArgumentException.class,
                () -> new ConfigurationManager(CONFIG_DIR, CONFIG_FILE, EMPTY_FILE, TARGETS_FILE));
    }

    @Test
    void itShouldThrowOnInvalidTargets() {
        assertThrows(MissingFormatArgumentException.class,
                () -> new ConfigurationManager(CONFIG_DIR, CONFIG_FILE, MESSAGES_FILE, EMPTY_FILE));

        assertThrows(MissingFormatArgumentException.class,
                () -> new ConfigurationManager(CONFIG_DIR, CONFIG_FILE, MESSAGES_FILE, TARGETS_INVALID_FILE));
    }

}