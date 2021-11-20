package ch.heigvd.api.mailrobot.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationManagerTest {
    private static final String CORRECT_CONFIG = "config.properties";
    private static final String EMPTY_FILE = "empty.txt";
    private static final String INVALID_FILE = "unknown.txt";
    private static final String MESSAGE_FILE = "messages.txt";
    private static final String TARGET_FILE = "targets.txt";
    private static final String ADDRESS = "localhost";

    private static final int PORT = 25;
    private static final int NB_GROUPS = 10;
    private static final String[] WITNESS_ADDRESS = {"witness.address@heig-vd.ch"};

    private static final int MESSAGE_COUNT = 4;
    private static final int TARGET_COUNT = 5;

    @Test
    void itShouldCorrectlyReadTheConfigFile() throws IOException {
        ConfigurationManager manager = new ConfigurationManager(CORRECT_CONFIG, EMPTY_FILE,
                EMPTY_FILE);

        assertEquals(ADDRESS, manager.getServerAddress());
        assertEquals(PORT, manager.getServerPort());
        assertEquals(NB_GROUPS, manager.getNumberOfGroups());
        assertArrayEquals(WITNESS_ADDRESS, manager.getWitnessesAddresses().toArray());
    }

    @Test
    void itShouldThrowOnInvalidFile() {
        assertThrows(IOException.class, () -> new ConfigurationManager(INVALID_FILE, EMPTY_FILE, EMPTY_FILE));
        assertThrows(IOException.class, () -> new ConfigurationManager(CORRECT_CONFIG, INVALID_FILE, EMPTY_FILE));
        assertThrows(IOException.class, () -> new ConfigurationManager(CORRECT_CONFIG, EMPTY_FILE, INVALID_FILE));
    }

    @Test
    void itShouldCorrectlyReadTheMessageAndTargetFile() throws IOException {
        ConfigurationManager manager = new ConfigurationManager(CORRECT_CONFIG, MESSAGE_FILE, TARGET_FILE);

        assertEquals(MESSAGE_COUNT, manager.getMessages().size());
        assertEquals(TARGET_COUNT, manager.getPersons().size());
    }

    //todo tests si données malformatées
}