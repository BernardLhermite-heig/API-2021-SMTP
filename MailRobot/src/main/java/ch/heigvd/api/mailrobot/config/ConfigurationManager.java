package ch.heigvd.api.mailrobot.config;

import ch.heigvd.api.mailrobot.model.mail.Person;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurationManager {
    private final static Logger LOG = Logger.getLogger(ConfigurationManager.class.getName());
    private static final String DEFAULT_MESSAGE_SEPARATOR = "---";
    private static final String DEFAULT_PERSON_SEPARATOR = ":";

    @Getter
    private String serverAddress;
    @Getter
    private int serverPort;
    @Getter
    private int numberOfGroups;
    @Getter
    private List<String> witnessesAddresses;
    private String messageSeparator;
    private String personSeparator;

    private final List<Person> persons;
    private final List<String> messages;


    public ConfigurationManager(String configFile, String messagesFile, String personFile) throws IOException {
        loadProperties(configFile);

        messages = loadMessages(messagesFile);
        persons = loadPersons(personFile);
    }

    /**
     * Retourne un InputStream sur le fichier de resource passé en paramètre.
     *
     * @param fileName le nom du fichier de resource
     * @return un InputStream sur le fichier
     * @throws IOException si le fichier ne peut pas être ouvert
     */
    private InputStream getResourceAsStream(String fileName) throws IOException {
        InputStream in = ConfigurationManager.class.getResourceAsStream(parseFileName(fileName));
        if (in == null)
            throw new IOException("File " + fileName + " not found");

        return in;
    }

    /**
     * Retourne le chemin complet du fichier de resource passé en paramètre.
     *
     * @param fileName le nom du fichier de resource
     * @return le chemin complet du fichier
     * @throws IOException si le fichier ne peut pas être ouvert
     */
    private String getResource(String fileName) throws IOException {
        URL path = ConfigurationManager.class.getResource(parseFileName(fileName));
        if (path == null)
            throw new IOException("File " + fileName + " not found");

        return path.getPath();
    }

    /**
     * Ajoute un '/' au nom de fichier passé en paramètre si ce dernier est manquant.
     *
     * @param fileName le nom du fichier à traiter
     * @return le nom du fichier sous la forme /nomFichier
     */
    private String parseFileName(String fileName) {
        return fileName.startsWith("/") ? fileName : "/" + fileName;
    }

    /**
     * Charge les données du fichier de propriétés passé en paramètre.
     * // TODO throw si donnée manquante?
     *
     * @param fileName le fichier à lire
     * @throws IOException si le fichier ne peut pas être lu
     */
    private void loadProperties(String fileName) throws IOException {
        try (InputStream in = getResourceAsStream(fileName)) {
            Properties prop = new Properties();
            prop.load(in);

            //todo check donnée
            serverAddress = prop.getProperty("serverAddress");
            serverPort = Integer.parseInt(prop.getProperty("serverPort"));
            numberOfGroups = Integer.parseInt(prop.getProperty("numberOfGroups"));
            messageSeparator = prop.getProperty("messageSeparator", DEFAULT_MESSAGE_SEPARATOR);
            personSeparator = prop.getProperty("personSeparator", DEFAULT_PERSON_SEPARATOR);

            String witnesses = prop.getProperty("witnessAddress");
            witnessesAddresses = new ArrayList<>(Arrays.asList(witnesses.split(":")));
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error while parsing configuration file. ", e);
            throw e;
        }
    }

    /**
     * Retourne une liste des messages situés dans le fichier passé en paramètre.
     *
     * @param fileName le fichier à lire
     * @throws IOException si le fichier ne peut pas être lu
     */
    private List<String> loadMessages(String fileName) throws IOException {
        List<String> messages = new LinkedList<>();

        try (Scanner scan = new Scanner(getResourceAsStream(fileName), StandardCharsets.UTF_8)) {
            scan.useDelimiter(messageSeparator);

            while (scan.hasNext()) {
                messages.add(scan.next());
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error while parsing messages file. ", e);
            throw e;
        }
        return messages;
    }

    /**
     * Retourne une liste des adresses situées dans le fichier passé en paramètre.
     *
     * @param fileName le fichier à lire
     * @throws IOException si le fichier ne peut pas être lu
     */
    private List<Person> loadPersons(String fileName) throws IOException {
        List<Person> persons = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(getResource(fileName), StandardCharsets.UTF_8))) {
            for (String line; br.ready() && (line = br.readLine()) != null; ) {
                String[] person = line.split(personSeparator);
                persons.add(new Person(person));
            }
        } catch (IOException | IllegalArgumentException e) {
            LOG.log(Level.SEVERE, "Error while parsing persons file. ", e);
            throw e;
        }
        return persons;
    }

    public List<Person> getPersons() {
        return Collections.unmodifiableList(persons);
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }
}