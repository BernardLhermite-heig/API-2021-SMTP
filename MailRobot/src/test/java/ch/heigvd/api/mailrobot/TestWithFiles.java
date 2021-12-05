package ch.heigvd.api.mailrobot;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Properties;
import java.util.stream.Stream;


/**
 * Classe abstraite permettant de copier les fichiers de resources entre chaque test.
 * Fournit également une méthode permettant d'éditer le fichier de configuration.
 *
 * @author Stéphane Marengo
 * @author Loris Marzullo
 */
public abstract class TestWithFiles {
    protected static final String CONFIG_DIR = "./testsConfig";
    protected static final Path CONFIG_PATH = Paths.get(CONFIG_DIR);

    protected static final String CONFIG_FILE = "config.properties";
    protected static final String MESSAGES_FILE = "messages.txt";
    protected static final String TARGETS_FILE = "targets.txt";
    protected static final String TARGETS_INVALID_FILE = "targets.invalid.txt";
    protected static final String TARGETS_SMALL_FILE = "targets.small.txt";
    protected static final String EMPTY_FILE = "empty.txt";
    protected static final String UNKNOWN_FILE = "unknown.txt";

    protected static final String[] FILES = {CONFIG_FILE, MESSAGES_FILE, TARGETS_FILE,
            TARGETS_INVALID_FILE, TARGETS_SMALL_FILE, EMPTY_FILE};

    private static void copyFile(String file) throws IOException {
        Path filePath = CONFIG_PATH.resolve(file);

        InputStream in = TestWithFiles.class.getResourceAsStream("/" + file);
        assert in != null;

        Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
    }

    protected static void editConfig(String key, String value) throws IOException {
        File file = CONFIG_PATH.resolve(CONFIG_FILE).toFile();
        Properties prop = new Properties();

        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
            prop.load(reader);
            prop.setProperty(key, value);
        }
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            prop.store(writer, "");
        }
    }

    @BeforeAll
    static void createConfigFiles() throws IOException {
        clearConfig();

        if (!Files.exists(CONFIG_PATH))
            Files.createDirectory(CONFIG_PATH);

        for (String file : FILES) {
            copyFile(file);
        }
    }

    @BeforeEach
    private void copyConfigFile() throws IOException {
        copyFile(CONFIG_FILE);
    }

    @AfterAll
    static void clearConfig() throws IOException {
        if (!Files.exists(CONFIG_PATH)) return;

        try (Stream<Path> walk = Files.walk(CONFIG_PATH)) {
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}
