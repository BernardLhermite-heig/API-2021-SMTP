package ch.heigvd.api.mailrobot.smtp;

import ch.heigvd.api.mailrobot.model.mail.Message;
import ch.heigvd.api.mailrobot.model.mail.Person;
import lombok.NonNull;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Implémentation d'un client SMTP. Les échanges avec le serveur sont loggés.
 *
 * @author Stéphane Marengo
 * @author Loris Marzullo
 */
public class SmtpClient {
    private static final Logger LOG = Logger.getLogger(SmtpClient.class.getName());
    private static final String EOL = "\r\n";

    private static final int CODE_READY = 220;
    private static final int CODE_OK = 250;
    private static final int CODE_START_INPUT = 354;
    private static final int CODE_CLOSING = 221;
    private static final int CODE_UNKNOWN = 0;

    private final String HOST;
    private final int PORT;
    private final String DOMAIN;
    private BufferedWriter out;
    private BufferedReader in;
    private Socket socket;

    /**
     * Construit un client qui utilisera l'hôte, le port et le domaine fourni.
     * <p>
     * Remarque : la connexion avec l'hôte n'est pas établie à la construction
     *
     * @param host   l'adresse du serveur SMTP
     * @param port   le port à utiliser
     * @param domain le domaine utilisé avec la commande EHLO
     * @throws IllegalArgumentException si le port n'est pas strictement positif
     */
    public SmtpClient(@NonNull String host, int port, @NonNull String domain) throws IllegalArgumentException {
        if (port <= 0)
            throw new IllegalArgumentException("Invalid port provided.");

        this.HOST = host;
        this.PORT = port;
        this.DOMAIN = domain;
    }

    /**
     * Envoie au serveur le message passé en paramètre.
     * <p>
     * Remarque : une nouvelle connexion est établie à chaque envoi
     *
     * @param mail le message à envoyer
     * @return vrai si l'envoi s'est déroulé correctement, faux sinon
     * @throws IllegalArgumentException s'il n'y a pas de destinataire ou d'expéditeur
     */
    public boolean send(@NonNull Message mail) throws IllegalArgumentException {
        List<Person> recipients = mail.getRecipients();
        List<String> hiddenRecipients = mail.getWitnesses();
        Person from = mail.getFrom();

        if (recipients.isEmpty()) {
            throw new IllegalArgumentException("Missing recipients.");
        }

        try {
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

            if (!read(CODE_READY)
                    || !sendHello(DOMAIN)
                    || !sendMailFrom(from.getEmail())
                    || !sendRcptTo(recipients, hiddenRecipients)
                    || !sendData()) {
                return false;
            }

            // Envoi du contenu
            send("Content-Type: text/plain; charset=UTF-8");
            sendDate();
            sendFrom(from);
            sendTo(recipients);
            sendSubject(mail.getSubject());
            sendBody(mail.getBody());

            return read(CODE_OK);
        } catch (Exception e) {
            LOG.severe("Error while trying to send mail.");
            LOG.severe(e.getMessage());
            return false;
        } finally {
            try {
                if (socket != null && !socket.isClosed() && !sendQuit())
                    LOG.severe("Server did not accept QUIT command.");
            } catch (IOException e) {
                LOG.severe("Error while sending QUIT command.");
                LOG.severe(e.getMessage());
            }
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                LOG.severe("Error while closing input stream.");
                LOG.severe(e.getMessage());
            }
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                LOG.severe("Error while closing output stream.");
                LOG.severe(e.getMessage());
            }
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                LOG.severe("Error while closing socket.");
                LOG.severe(e.getMessage());
            }
        }
    }

    /**
     * Extrait le code (3 premiers caractères) de la chaîne fournie.
     *
     * @param line la chaîne qui contient le code
     * @return le code
     */
    private int getCode(@NonNull String line) {
        try {
            return Integer.parseInt(line.substring(0, 3));
        } catch (NumberFormatException e) {
            return CODE_UNKNOWN;
        }
    }

    /**
     * Lis la réponse du serveur et vérifie que le code reçu correspond à celui attendu.
     *
     * @param exceptedCode le code attendu
     * @return vrai si les codes correspondent, faux sinon
     * @throws IOException si une erreur survient lors de la lecture
     */
    private boolean read(int exceptedCode) throws IOException {
        int code = CODE_UNKNOWN;
        do {
            for (String line; in.ready() && (line = in.readLine()) != null; ) {
                int tmp = getCode(line);
                if (tmp != CODE_UNKNOWN) code = tmp;

                LOG.info("Received: " + line);
            }
        } while (code == CODE_UNKNOWN);
        return code == exceptedCode;
    }

    /**
     * Envoie la chaîne de caractères passée en paramètre au serveur.
     *
     * @param msg la chaîne à envoyer
     * @throws IOException si une erreur survient lors de l'envoi
     */
    private void send(@NonNull String msg) throws IOException {
        LOG.info("Sending: " + msg);
        out.write(msg + EOL);
        out.flush();
    }

    /**
     * Envoie la chaîne de caractères passée en paramètre puis lit la réponse du serveur
     * et compare le code obtenu avec celui attendu.
     *
     * @param msg          la chaîne à envoyer
     * @param exceptedCode le code attendu du serveur
     * @return vrai si les codes correspondent, faux sinon
     * @throws IOException si une erreur survient lors de l'envoi ou de la lecture
     */
    private boolean sendAndRead(@NonNull String msg, int exceptedCode) throws IOException {
        send(msg);
        return read(exceptedCode);
    }

    /**
     * Initie l'échange SMTP en envoyant la commande EHLO.
     *
     * @param domain le domaine de provenance envoyé avec la commande
     * @return vrai si le serveur a accepté la commande, faux sinon
     * @throws IOException si une erreur survient lors de l'envoi ou de la lecture
     */
    private boolean sendHello(@NonNull String domain) throws IOException {
        return sendAndRead("EHLO " + domain, CODE_OK);
    }

    /**
     * Envoie une commande MAIL FROM avec l'adresse spécifiée.
     *
     * @param address l'adresse de l'expéditeur
     * @return vrai si le serveur a accepté la commande, faux sinon
     * @throws IOException si une erreur survient lors de l'envoi ou de la lecture
     */
    private boolean sendMailFrom(@NonNull String address) throws IOException {
        return sendAndRead("MAIL FROM:<" + address + ">", CODE_OK);
    }

    /**
     * Envoie une commande RCPT TO pour chaque adresse passée en paramètre de chacune des listes.
     *
     * @param recipients la liste des personnes destinataires
     * @param emails     la liste des adresses
     * @return vrai si le serveur a accepté la commande, faux sinon
     * @throws IOException si une erreur survient lors de l'envoi ou de la lecture
     */
    private boolean sendRcptTo(List<Person> recipients, List<String> emails) throws IOException {
        for (Person p : recipients) {
            if (!sendRcptTo(p.getEmail())) {
                return false;
            }
        }
        for (String email : emails) {
            if (!sendRcptTo(email)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Envoie une commande RCPT TO avec l'adresse passée en paramètre.
     *
     * @param email l'adresse à envoyer
     * @return vrai si le serveur a accepté la commande, faux sinon
     * @throws IOException si une erreur survient lors de l'envoi ou de la lecture
     */
    private boolean sendRcptTo(String email) throws IOException {
        return sendAndRead("RCPT TO:<" + email + ">", CODE_OK);
    }

    /**
     * Envoie la commande DATA au serveur.
     *
     * @return vrai si le serveur a accepté la commande, faux sinon
     * @throws IOException si une erreur survient lors de l'envoi ou de la lecture
     */
    private boolean sendData() throws IOException {
        return sendAndRead("DATA", CODE_START_INPUT);
    }

    /**
     * Ajoute la date courante au contenu du mail.
     *
     * @throws IOException si une erreur survient lors de l'envoi ou de la lecture
     */
    private void sendDate() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
        String formattedDate = ZonedDateTime.now().format(formatter);
        send("Date: " + formattedDate);
    }

    /**
     * Ajoute l'expéditeur passé en paramètre au contenu du mail.
     *
     * @param person l'expéditeur
     * @throws IOException si une erreur survient lors de l'envoi ou de la lecture
     */
    private void sendFrom(@NonNull Person person) throws IOException {
        send("From: " + person);
    }

    /**
     * Envoie les destinataires passés en paramètre.
     *
     * @param recipients la liste des destinataires
     * @throws IOException si une erreur survient lors de l'envoi ou de la lecture
     */
    private void sendTo(@NonNull List<Person> recipients) throws IOException {
        StringBuilder builder = new StringBuilder("To: ");
        String prefix = "";
        for (Person p : recipients) {
            builder.append(prefix).append(p);
            prefix = ", ";
        }
        send(builder.toString());
    }

    /**
     * Envoie le sujet du mail.
     * <p>
     * Remarque : le sujet est encodé en base64 et sera décodé par le serveur.
     *
     * @param subject le sujet du mail
     * @throws IOException si une erreur survient lors de l'envoi ou de la lecture
     */
    private void sendSubject(@NonNull String subject) throws IOException {
        send("Subject: =?utf-8?B?"
                + Base64.getEncoder().encodeToString(subject.getBytes(StandardCharsets.UTF_8))
                + "?=");
    }

    /**
     * Envoie le contenu du mail.
     *
     * @param body le texte du mail
     * @throws IOException si une erreur survient lors de l'envoi ou de la lecture
     */
    private void sendBody(@NonNull String body) throws IOException {
        send(EOL + body + EOL + ".");
    }

    /**
     * Envoi la commande QUIT au serveur.
     *
     * @return vrai si le serveur a terminé la connection, faux sinon
     * @throws IOException si une erreur survient lors de l'envoi ou de la lecture
     */
    private boolean sendQuit() throws IOException {
        return sendAndRead("QUIT", CODE_CLOSING);
    }
}
