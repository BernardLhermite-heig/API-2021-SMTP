package ch.heigvd.api.mailrobot.config;

import ch.heigvd.api.mailrobot.model.mail.Person;
import ch.heigvd.api.mailrobot.util.MandatoryProperties;
import lombok.Getter;
import lombok.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Classe permettant de lire les différents fichiers nécessaires au bon fonctionnement
 * du MailRobot
 *
 * @author Stéphane Marengo
 * @author Loris Marzullo
 */
public class ConfigurationManager {
    private final static Logger LOG = Logger.getLogger(ConfigurationManager.class.getName());
    private static final String DEFAULT_MESSAGE_SEPARATOR = "---";
    private static final String DEFAULT_PERSON_SEPARATOR = ":";
    private static final String DEFAULT_WITNESS_SEPARATOR = ",";

    @Getter
    private String serverAddress;
    @Getter
    private int serverPort;
    @Getter
    private int numberOfGroups;
    private String messageSeparator;
    private String personSeparator;

    private List<String> witnessesAddresses;
    private List<Person> persons;
    private List<String> messages;

    /**
     * Créé une instance et récupère les différentes informations des fichiers spécifiés.
     *
     * @param configFile   le fichier contenant la configuration
     * @param messagesFile le fichier contenant les messages
     * @param personFile   le fichier contenant les victimes
     * @throws IOException                    si une erreur survient lors des traitements I/O
     * @throws MissingFormatArgumentException si une propriété obligatoire est manquante dans la configuration
     * @throws NumberFormatException          si le numéro de port n'est pas un entier valide
     */
    public ConfigurationManager(@NonNull String configFile, @NonNull String messagesFile, @NonNull String personFile)
            throws IOException, MissingFormatArgumentException, NumberFormatException {
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
            throw new IOException("File " + fileName + " not found.");

        return in;
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
     *
     * @param fileName le fichier à lire
     * @throws IOException si le fichier ne peut pas être lu
     */
    private void loadProperties(String fileName) throws IOException {
        try (InputStream in = getResourceAsStream(fileName)) {
            MandatoryProperties prop = new MandatoryProperties();
            prop.load(in);

            serverAddress = prop.getMandatoryProperty("serverAddress");
            serverPort = Integer.parseInt(prop.getMandatoryProperty("serverPort"));
            numberOfGroups = Integer.parseInt(prop.getMandatoryProperty("numberOfGroups"));
            messageSeparator = prop.getProperty("messageSeparator", DEFAULT_MESSAGE_SEPARATOR);
            personSeparator = prop.getProperty("personSeparator", DEFAULT_PERSON_SEPARATOR);
            
            String witnessSeparator = prop.getProperty("witnessSeparator", DEFAULT_WITNESS_SEPARATOR);
            String witnesses = prop.getMandatoryProperty("witnessAddresses");
            witnessesAddresses = new ArrayList<>(Arrays.asList(witnesses.split(witnessSeparator)));
        } catch (MissingFormatArgumentException | NumberFormatException | IOException e) {
            LOG.log(Level.SEVERE, "Error while parsing configuration file " + fileName + ".", e);
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
            LOG.log(Level.SEVERE, "Error while parsing messages file " + fileName + ".", e);
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

        try (BufferedReader br = new BufferedReader(new InputStreamReader(getResourceAsStream(fileName),
                StandardCharsets.UTF_8))) {
            for (String line; br.ready() && (line = br.readLine()) != null; ) {
                String[] person = line.split(personSeparator);
                persons.add(new Person(person));
            }
        } catch (IOException | IllegalArgumentException e) {
            LOG.log(Level.SEVERE, "Error while parsing persons file " + fileName + ".", e);
            throw e;
        }
        return persons;
    }

    /**
     * Retourne une liste non modifiable de personnes.
     *
     * @return la liste des personnes
     */
    public List<Person> getPersons() {
        return Collections.unmodifiableList(persons);
    }

    /**
     * Retourne une liste non modifiable de messages.
     *
     * @return la liste des messages
     */
    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    /**
     * Retourne une liste non modifiable de victimes.
     *
     * @return la liste des messages
     */
    public List<String> getWitnessesAddresses() {
        return Collections.unmodifiableList(witnessesAddresses);
    }
}