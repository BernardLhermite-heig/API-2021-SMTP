package ch.heigvd.api.mailrobot;

import ch.heigvd.api.mailrobot.config.ConfigurationManager;
import ch.heigvd.api.mailrobot.model.mail.Message;
import ch.heigvd.api.mailrobot.model.prank.Prank;
import ch.heigvd.api.mailrobot.model.prank.PrankGenerator;
import ch.heigvd.api.mailrobot.smtp.SmtpClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppStarter {
    private static final Logger LOG = Logger.getLogger(AppStarter.class.getName());
    private static final String CONFIG_DIR = "./config";
    private static final String CONFIG_FILE = "config.properties";
    private static final String MESSAGE_FILE = "messages.txt";
    private static final String TARGET_FILE = "targets.txt";

    private static final String[] DEFAULT_CONFIG_FILES = {
            "messages.default.txt",
            "targets.default.txt",
            "config.default.properties"
    };

    public static void main(String[] args) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");
        // Only log SEVERE
        Logger.getLogger("").setLevel(Level.SEVERE);

        for (String arg : args) {
            switch (arg) {
                case "-c":
                    try {
                        createDefaultConfig();
                        System.out.println("Default configuration created.");
                    } catch (IOException e) {
                        System.out.println("Could not create default configuration.");
                    }
                    return;
                case "-l":
                    Logger.getLogger("").setLevel(Level.ALL);
                    break;
            }
        }

        try {
            ConfigurationManager config = new ConfigurationManager(CONFIG_DIR, CONFIG_FILE, MESSAGE_FILE, TARGET_FILE);
            PrankGenerator generator = new PrankGenerator(config);
            List<Prank> pranks = generator.generatePranks();

            SmtpClient client = new SmtpClient(config.getServerAddress(), config.getServerPort(), config.getDomain());
            List<String> witnesses = config.getWitnessesEmails();

            for (Prank prank : pranks) {
                Message message = new Message(prank.getExpeditor(), prank.getSubject(), prank.getBody());
                message.addRecipients(prank.getRecipients());
                message.addWitnesses(witnesses);

                System.out.println("Sending prank...");

                if (!client.send(message)) {
                    System.out.println("Something went wrong.");
                }
            }
            System.out.println("Done sending pranks !");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "An error occurred.", e);
        }
    }


    /**
     * Créé des fichiers de configurations par défaut.
     *
     * @throws IOException si une erreur survient durant les traitements I/O
     */
    public static void createDefaultConfig() throws IOException {
        Path configPath = Paths.get(CONFIG_DIR);

        if (!Files.exists(configPath))
            Files.createDirectory(configPath);

        for (String configFile : DEFAULT_CONFIG_FILES) {
            String fileName = configFile.replace("default.", "");
            Path filePath = configPath.resolve(fileName);

            if (Files.exists(filePath)) continue;

            try {
                Files.copy(getResourceAsStream("/" + configFile), filePath);
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Could not create " + fileName + ".", e);
            }
        }
    }

    /**
     * Retourne un InputStream sur le fichier de resource passé en paramètre.
     *
     * @param fileName le nom du fichier de resource
     * @return un InputStream sur le fichier
     * @throws IOException si le fichier ne peut pas être ouvert
     */
    private static InputStream getResourceAsStream(String fileName) throws IOException {
        InputStream in = ConfigurationManager.class.getResourceAsStream(fileName);
        if (in == null)
            throw new IOException("File " + fileName + " not found in resources.");

        return in;
    }
}
