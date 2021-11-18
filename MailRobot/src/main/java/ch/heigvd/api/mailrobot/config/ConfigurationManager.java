package ch.heigvd.api.mailrobot.config;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurationManager {
    private final static Logger LOG = Logger.getLogger(ConfigurationManager.class.getName());

    @Getter
    private String serverAddress;
    @Getter
    private int serverPort;
    @Getter
    private int numberOfGroups;
    @Getter
    private String witnessAddress;
    private String messageSeparator;

//    private final List<Person> persons;
//    private final List<String> messages;


    public ConfigurationManager(String configFile, String messagesFile, String personFile) throws IOException {

        loadProperties(parseFileName(configFile));
        loadMessages(parseFileName(messagesFile));
    }

    private InputStream getResourceAsStream(String fileName) {
        return ConfigurationManager.class.getResourceAsStream(fileName);
    }

    private String parseFileName(String fileName) {
        return fileName.startsWith("/") ? fileName : "/" + fileName;
    }

    private void loadProperties(String fileName) throws IOException {
        try (InputStream in = getResourceAsStream(fileName)) {
            Properties prop = new Properties();
            prop.load(in);

            serverAddress = prop.getProperty("serverAddress");
            serverPort = Integer.parseInt(prop.getProperty("serverPort"));
            numberOfGroups = Integer.parseInt(prop.getProperty("numberOfGroups"));
            witnessAddress = prop.getProperty("witnessAddress");
            messageSeparator = prop.getProperty("messageSeparator");
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error while parsing configuration file. ", e);
            throw e;
        }
    }

    private void loadMessages(String fileName) {
        Scanner scan = new Scanner(getResourceAsStream(fileName), StandardCharsets.UTF_8);
        scan.useDelimiter(messageSeparator);

        while (scan.hasNext()) {
            System.out.println(scan.next());
        }
        scan.close();
    }
}