package ch.heigvd.api.mailrobot.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.MissingFormatArgumentException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Stéphane Marengo
 * @author Loris Marzullo
 */
class ConfigurationManagerTest {
    private static final String CORRECT_CONFIG = "config.properties";
    private static final String MISSING_PROPERTY_CONFIG = "missingPropertyConfig.properties";
    private static final String INVALID_PROPERTY_CONFIG = "invalidConfig.properties";
    private static final String EMPTY_FILE = "empty.txt";
    private static final String IMAGINARY_FILE = "unknown.txt";
    private static final String MESSAGE_FILE = "messages.txt";
    private static final String TARGET_FILE = "targets.txt";
    private static final String INVALID_TARGET_FILE = "invalidTargets.txt";
    private static final String ADDRESS = "localhost";

    private static final int PORT = 25;
    private static final int NB_GROUPS = 2;
    private static final String[] WITNESS_ADDRESSES = {"witness.address@heig-vd.ch", "witness2.address@heig-vd.ch"};

    private static final int MESSAGE_COUNT = 4;
    private static final int TARGET_COUNT = 9;

    @Test
    void itShouldCorrectlyReadTheConfigFile() throws IOException {
        ConfigurationManager manager = new ConfigurationManager(CORRECT_CONFIG, EMPTY_FILE,
                EMPTY_FILE);

        assertEquals(ADDRESS, manager.getServerAddress());
        assertEquals(PORT, manager.getServerPort());
        assertEquals(NB_GROUPS, manager.getNumberOfGroups());
        assertArrayEquals(WITNESS_ADDRESSES, manager.getWitnessesAddresses().toArray());
    }

    @Test
    void itShouldThrowOnInvalidFile() {
        assertThrows(IOException.class, () -> new ConfigurationManager(IMAGINARY_FILE, EMPTY_FILE, EMPTY_FILE));
        assertThrows(IOException.class, () -> new ConfigurationManager(CORRECT_CONFIG, IMAGINARY_FILE, EMPTY_FILE));
        assertThrows(IOException.class, () -> new ConfigurationManager(CORRECT_CONFIG, EMPTY_FILE, IMAGINARY_FILE));
    }

    @Test
    void itShouldCorrectlyReadTheMessageAndTargetFile() throws IOException {
        ConfigurationManager manager = new ConfigurationManager(CORRECT_CONFIG, MESSAGE_FILE, TARGET_FILE);

        assertEquals(MESSAGE_COUNT, manager.getMessages().size());
        assertEquals(TARGET_COUNT, manager.getPersons().size());
    }

    @Test
    void itShouldThrowOnInvalidPersonFile() {
        assertThrows(IllegalArgumentException.class, () -> new ConfigurationManager(CORRECT_CONFIG, MESSAGE_FILE, INVALID_TARGET_FILE));
    }

    @Test
    void itShouldThrowOnMissingMandatoryProperty() {
        assertThrows(MissingFormatArgumentException.class,
                () -> new ConfigurationManager(MISSING_PROPERTY_CONFIG, MESSAGE_FILE, INVALID_TARGET_FILE));
    }

    @Test
    void itShouldThrowOnInvalidPort() {
        assertThrows(NumberFormatException.class,
                () -> new ConfigurationManager(INVALID_PROPERTY_CONFIG, MESSAGE_FILE, INVALID_TARGET_FILE));
    }
}