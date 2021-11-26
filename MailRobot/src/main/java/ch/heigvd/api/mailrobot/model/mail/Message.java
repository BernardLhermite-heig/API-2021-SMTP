package ch.heigvd.api.mailrobot.model.mail;

import lombok.Getter;
import lombok.NonNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Modélisation d'un email.
 *
 * @author Stéphane Marengo
 * @author Loris Marzullo
 */
public class Message {
   @Getter
   private final Person from;
   private final List<Person> recipients;
   private final List<Person> hiddenRecipients;
   @Getter
   private final String subject;
   @Getter
   private final String body;

   /**
    * Créé un message avec l'expéditeur, le titre et le contenu fourni.
    *
    * @param from    l'expéditeur
    * @param subject le titre
    * @param body    le contenu
    */
   public Message(@NonNull Person from, @NonNull String subject, @NonNull String body) {
      this.from = from;
      this.subject = subject;
      this.body = body;

      recipients = new LinkedList<>();
      hiddenRecipients = new LinkedList<>();
   }

   /**
    * Ajoute la personne passée en paramètre dans la liste des destinataires.
    *
    * @param person le destinataire
    */
   public void addRecipient(@NonNull Person person) {
      recipients.add(person);
   }

   /**
    * Ajoute la personne passée en paramètre dans la liste des destinataires cachés.
    *
    * @param person le destinataire caché
    */
   public void addHiddenRecipient(@NonNull Person person) {
      hiddenRecipients.add(person);
   }

   /**
    * Retourne une liste non modifiable des destinataires.
    *
    * @return la liste non modifiable
    */
   public List<Person> getRecipients() {
      return Collections.unmodifiableList(recipients);
   }

   /**
    * Retourne une liste non modifiable des destinataires cachés.
    *
    * @return la liste non modifiable
    */
   public List<Person> getHiddenRecipients() {
      return Collections.unmodifiableList(hiddenRecipients);
   }
}
