package ch.heigvd.api.mailrobot.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigurationManagerTest {
    private static final Logger LOG = Logger.getLogger(ConfigurationManagerTest.class.getName());
    private static final String CORRECT_CONFIG = "config.properties";
    private static final String EMPTY_FILE = "empty.txt";
    private static final String INVALID_FILE = "unknown.txt";
    private static final String ADDRESS = "localhost";

    private static final int PORT = 25;
    private static final int NB_GROUPS = 10;
    private static final String WITNESS_ADDRESS = "witness.address@heig-vd.ch";
    private static final String MESSAGE_SEPARATOR = "---";

    @Test
    void itShouldCorrectlyReadTheConfigFile() throws IOException {
        ConfigurationManager manager = new ConfigurationManager(CORRECT_CONFIG, EMPTY_FILE,
                EMPTY_FILE);

        assertEquals(manager.getServerAddress(), ADDRESS);
        assertEquals(manager.getServerPort(), PORT);
        assertEquals(manager.getNumberOfGroups(), NB_GROUPS);
        assertEquals(manager.getWitnessAddress(), WITNESS_ADDRESS);
    }
    @Test
    void itShouldThrowOnInvalidFile() {
        Exception exception = assertThrows(IOException.class, () -> {
            new ConfigurationManager(INVALID_FILE, EMPTY_FILE, EMPTY_FILE);
        });
    }
}