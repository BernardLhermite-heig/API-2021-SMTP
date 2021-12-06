package ch.heigvd.api.mailrobot.config;

import ch.heigvd.api.mailrobot.model.mail.Person;
import ch.heigvd.api.mailrobot.util.CustomProperties;
import lombok.Getter;
import lombok.NonNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Classe permettant de lire et de valider les différents fichiers de configuration.
 *
 * @author Stéphane Marengo
 * @author Loris Marzullo
 */
public class ConfigurationManager {
    private final static Logger LOG = Logger.getLogger(ConfigurationManager.class.getName());

    private static final String EOL = "\r\n";
    private static final String DEFAULT_MESSAGE_SEPARATOR = "---";
    private static final String DEFAULT_TARGET_SEPARATOR = ":";
    private static final String DEFAULT_WITNESS_SEPARATOR = ",";
    private static final String DEFAULT_DOMAIN = "prank";

    private static final int INVALID_VALUE = -1;

    private final String configDirectory;
    @Getter
    private String serverAddress;
    @Getter
    private int serverPort;
    @Getter
    private int numberOfGroups;
    @Getter
    private String domain;
    private String messageSeparator;
    private String targetSeparator;

    private List<String> witnessesEmails;
    private List<Person> targets;
    private List<String> messages;

    /**
     * Créé une instance et récupère les différentes informations des fichiers spécifiés.
     * Affiche des messages en cas d'erreur.
     *
     * @param configDirectory le chemin vers le dossier de configuration
     * @param configFile      le fichier contenant la configuration
     * @param messagesFile    le fichier contenant les messages
     * @param targetsFile     le fichier contenant les victimes
     * @throws IOException                    si une erreur survient lors des traitements I/O
     * @throws MissingFormatArgumentException si une ou plusieurs valeurs sont incorrectes
     */
    public ConfigurationManager(@NonNull String configDirectory, @NonNull String configFile,
                                @NonNull String messagesFile, @NonNull String targetsFile)
            throws IOException, MissingFormatArgumentException {
        this.configDirectory = configDirectory;
        loadProperties(configFile);
        messages = loadMessages(messagesFile);
        targets = loadTargets(targetsFile);

        if (!validate())
            throw new MissingFormatArgumentException("An error occurred. See logs for details.");
    }

    /**
     * Vérifie les divers valeurs lues.
     *
     * @return vrai si les valeurs sont valides, faux autrement
     */
    private boolean validate() {
        boolean isValid = true;

        if (serverAddress.isEmpty()) {
            printError("serverAddress", "must be specified");
            isValid = false;
        }
        if (serverPort <= 0) {
            printError("serverPort", "must be > 0");
            isValid = false;
        }
        if (numberOfGroups <= 0) {
            printError("numberOfGroups", "must be > 0");
            isValid = false;
        }
        if (witnessesEmails.isEmpty()) {
            printError("witnessesEmails", "must have at least 1 address");
            isValid = false;
        } else {
            for (String witness : witnessesEmails) {
                if (!validateEmail("witnessesEmails", witness))
                    isValid = false;
            }
        }
        if (messages.isEmpty()) {
            printError("messages", "no messages found");
            isValid = false;
        }
        if (targets.isEmpty()) {
            printError("targets", "no targets found");
            isValid = false;
        } else {
            for (Person person : targets) {
                if (!validateEmail("targets", person.getEmail()))
                    isValid = false;
            }
        }

        return isValid;
    }

    /**
     * Vérifie que l'email fourni est correct. Affiche un message en cas d'erreur.
     * Source : https://www.geeksforgeeks.org/check-email-address-valid-not-java/
     *
     * @param field le champ passé à la méthode d'affichage d'erreur
     * @param email l'adresse à valider
     * @return vrai si l'email est correct, faux autrement
     */
    private boolean validateEmail(@NonNull String field, @NonNull String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(regex);

        if (pat.matcher(email).matches()) {
            return true;
        } else {
            printError(field, email + " is not a valid email.");
            return false;
        }
    }

    /**
     * Log une erreur avec le format suivant: field: msg
     *
     * @param field le nom du champ
     * @param msg   le message d'erreur
     */
    private void printError(String field, String msg) {
        LOG.info(field + ": " + msg);
    }

    /**
     * Retourne le chemin relatif au dossier de configuration du fichier spécifié.
     *
     * @param fileName le nom du fichier
     * @return le chemin relatif au dossier de configuration
     */
    private String getPath(String fileName) {
        return configDirectory + (fileName.startsWith("/") ? fileName : "/" + fileName);
    }

    /**
     * Charge les données du fichier de propriétés passé en paramètre.
     *
     * @param fileName le fichier à lire
     * @throws IOException si le fichier ne peut pas être lu
     */
    private void loadProperties(String fileName) throws IOException {
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(getPath(fileName), StandardCharsets.UTF_8))) {
            CustomProperties prop = new CustomProperties();
            prop.load(reader);

            messageSeparator = prop.getProperty("messageSeparator", DEFAULT_MESSAGE_SEPARATOR);
            targetSeparator = prop.getProperty("targetSeparator", DEFAULT_TARGET_SEPARATOR);
            String witnessSeparator = prop.getProperty("witnessSeparator", DEFAULT_WITNESS_SEPARATOR);

            serverAddress = prop.getProperty("serverAddress", "");
            serverPort = prop.getInteger("serverPort", INVALID_VALUE);
            numberOfGroups = prop.getInteger("numberOfGroups", INVALID_VALUE);
            domain = prop.getProperty("domain", DEFAULT_DOMAIN);

            String witnesses = prop.getProperty("witnessesEmails", "");
            witnessesEmails = new ArrayList<>();
            if (!witnesses.isEmpty())
                witnessesEmails.addAll(Arrays.asList(witnesses.split(witnessSeparator)));
        } catch (IOException e) {
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
        List<String> messages = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(getPath(fileName), StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            for (String line; br.ready() && (line = br.readLine()) != null; ) {
                if (line.startsWith(messageSeparator)) {
                    messages.add(builder.toString());
                    builder.setLength(0);
                    continue;
                }
                builder.append(line).append(System.lineSeparator());
            }

            if (builder.length() > 0) {
                messages.add(builder.toString());
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
    private List<Person> loadTargets(String fileName) throws IOException {
        List<Person> targets = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(getPath(fileName), StandardCharsets.UTF_8))) {
            for (String line; br.ready() && (line = br.readLine()) != null; ) {
                String[] person = line.split(targetSeparator);
                if (person.length != 3) {
                    printError(fileName, line + " has invalid syntax.");
                } else {
                    targets.add(new Person(person[0], person[1], person[2]));
                }
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error while parsing targets file " + fileName + ".", e);
            throw e;
        }
        return targets;
    }

    /**
     * Retourne une liste non modifiable de cibles.
     *
     * @return la liste des cibles
     */
    public List<Person> getTargets() {
        return Collections.unmodifiableList(targets);
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
    public List<String> getWitnessesEmails() {
        return Collections.unmodifiableList(witnessesEmails);
    }
}