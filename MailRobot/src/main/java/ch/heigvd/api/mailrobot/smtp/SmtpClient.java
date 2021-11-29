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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implémentation du client SMTP. Les échanges différents échanges sont loggés.
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
    private BufferedWriter out;
    private BufferedReader in;
    private Socket socket;

    private StringBuilder content;

    /**
     * Construit un client qui utilisera l'hôte et le port fourni.
     *
     * @param host l'adresse du serveur SMTP
     * @param port le port à utiliser
     * @throws IllegalArgumentException si le port est invalide
     */
    public SmtpClient(@NonNull String host, int port) throws IllegalArgumentException {
        if (port <= 0)
            throw new IllegalArgumentException("Invalid port provided.");

        this.HOST = host;
        this.PORT = port;
        content = new StringBuilder("Content-Type: text/plain; charset=utf-8");
    }

    /**
     * Envoie au serveur le message passé en paramètre en utilisant le domaine fourni.
     *
     * @param mail   le message à envoyer
     * @param domain le domaine utilisé lors de la commande EHLO
     * @return vrai si l'envoi s'est déroulé correctement, faux sinon
     * @throws IllegalArgumentException s'il n'y a pas de destinataire ou d'expéditeur
     */
    public boolean send(@NonNull Message mail, @NonNull String domain) throws IllegalArgumentException {
        List<Person> recipients = mail.getRecipients();
        List<Person> hiddenRecipients = mail.getHiddenRecipients();
        Person from = mail.getFrom();

        if (recipients.isEmpty() || from.getAddress().isEmpty()) {
            throw new IllegalArgumentException("Missing recipients or destination address.");
        }

        try {
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            
            if (!read(CODE_READY)
                    || !sendHello(domain)
                    || !sendMailFrom(from.getAddress())
                    || !sendMailTo(recipients)
                    || !sendMailTo(hiddenRecipients)
                    || !sendData()) {
                return false;
            }

            // Préparation du contenu
            addDate();
            addFrom(from);
            addSubject(mail.getSubject());
            addTo(mail.getRecipients());
            addBody(mail.getBody());

            if (!sendContent()
                    || !sendQuit()) {
                return false;
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error while trying to send mail.", e);
            return false;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Error while closing reader.", e);
            }
            try {
                out.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Error while closing writer.", e);
            }
            try {
                socket.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Error while closing socket.", e);
            }
        }
        return true;
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
     * Envoi la chaîne de caractères passée en paramètre au serveur.
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
     * Envoi la chaîne de caractères passée en paramètre puis lis la réponse du serveur
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
     * Envoi une commande MAIL FROM avec l'adresse spécifiée.
     *
     * @param address l'adresse de l'expéditeur
     * @return vrai si le serveur a accepté la commande, faux sinon
     * @throws IOException si une erreur survient lors de l'envoi ou de la lecture
     */
    private boolean sendMailFrom(@NonNull String address) throws IOException {
        return sendAndRead("MAIL FROM:<" + address + ">", CODE_OK);
    }

    /**
     * Envoi une commande MAIL TO avec l'adresse de chaque personne présente dans la liste fournie.
     *
     * @param recipients la liste des destinataires
     * @return vrai si le serveur a accepté la commande, faux sinon
     * @throws IOException si une erreur survient lors de l'envoi ou de la lecture
     */
    private boolean sendMailTo(@NonNull List<Person> recipients) throws IOException {
        for (Person p : recipients) {
            if (!sendAndRead("RCPT TO:<" + p.getAddress() + ">", CODE_OK)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Envoi la commande DATA au serveur.
     *
     * @return vrai si le serveur a accepté la commande, faux sinon
     * @throws IOException si une erreur survient lors de l'envoi ou de la lecture
     */
    private boolean sendData() throws IOException {
        return sendAndRead("DATA", CODE_START_INPUT);
    }

    /**
     * Ajoute la date courante au contenu du mail.
     */
    private void addDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
        String formattedDate = ZonedDateTime.now().format(formatter);
        content.append(formattedDate).append(EOL);
    }

    /**
     * Ajoute l'expéditeur passé en paramètre au contenu du mail.
     *
     * @param person l'expéditeur
     */
    private void addFrom(@NonNull Person person) {
        content.append("From: ").append(person).append(EOL);
    }

    /**
     * Ajoute les destinataires fournies au contenu du mail.
     *
     * @param recipients la liste des destinataires
     */
    private void addTo(@NonNull List<Person> recipients) {
        content.append("To: ");
        String prefix = "";
        for (Person p : recipients) {
            content.append(prefix).append(p);
            prefix = ", ";
        }
        content.append(EOL);
    }

    /**
     * Ajoute le sujet au contenu du mail.
     * Remarque : le sujet est encodé en base64 et sera décodé par le serveur.
     *
     * @param subject le sujet du mail
     */
    private void addSubject(@NonNull String subject) {
        content.append("Subject: =?utf-8?B?")
                .append(Base64.getEncoder().encodeToString(subject.getBytes()))
                .append("?=")
                .append(EOL);
    }

    /**
     * Ajoute le texte au contenu du mail.
     * Remarque : la séquence de terminaison (CRLF . CRLF) est ajoutée au contenu.
     *
     * @param body le texte du mail
     */
    private void addBody(@NonNull String body) {
        content.append(EOL)
                .append(body)
                .append(EOL).append(".").append(EOL);
    }

    /**
     * Envoi le contenu du mail au serveur.
     *
     * @return vrai si le serveur a accepté le mail, faux sinon
     * @throws IOException si une erreur survient lors de l'envoi ou de la lecture
     */
    private boolean sendContent() throws IOException {
        return sendAndRead(content.toString(), CODE_OK);
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
