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
   private Person from;
   private List<Person> recipients;
   private List<String> witnesses;
   @Getter
   private String subject;
   @Getter
   private String body;

   /**
    * Créé un message avec l'expéditeur, le titre et le contenu fourni.
    *
    * @param from    l'expéditeur du message
    * @param subject le titre du message
    * @param body    le contenu du message
    */
   public Message(@NonNull Person from, @NonNull String subject, @NonNull String body) {
      this.from = from;
      this.subject = subject;
      this.body = body;

      recipients = new LinkedList<>();
      witnesses = new LinkedList<>();
   }

   /**
    * Ajoute la personne passée en paramètre dans la liste des destinataires.
    *
    * @param person le destinataire à ajouter
    */
   public void addRecipient(@NonNull Person person) {
      recipients.add(person);
   }

   /**
    * Ajoute la liste de personnes passée en paramètre dans la liste des destinataires.
    *
    * @param persons la liste des destinataires à ajouter
    */
   public void addRecipients(@NonNull List<Person> persons) {
      recipients.addAll(persons);
   }

   /**
    * Ajoute l'email passé en paramètre dans la liste des destinataires cachés.
    *
    * @param email l'adresse du destinataire caché à ajouter
    */
   public void addWitness(@NonNull String email) {
      witnesses.add(email);
   }

   /**
    * Ajoute la liste d'adresses email passée en paramètre dans la liste des destinataires.
    *
    * @param emails la liste d'adresses à ajouter
    */
   public void addWitnesses(List<String> emails) {
      witnesses.addAll(emails);
   }

   /**
    * Retourne une liste non modifiable des destinataires.
    *
    * @return la liste des destinataires
    */
   public List<Person> getRecipients() {
      return Collections.unmodifiableList(recipients);
   }

   /**
    * Retourne une liste non modifiable d'adresses des destinataires cachés.
    *
    * @return la liste d'adresses des destinataires cachés
    */
   public List<String> getWitnesses() {
      return Collections.unmodifiableList(witnesses);
   }
}
