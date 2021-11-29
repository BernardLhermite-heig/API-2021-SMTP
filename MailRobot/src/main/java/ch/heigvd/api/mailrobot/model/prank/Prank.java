package ch.heigvd.api.mailrobot.model.prank;

import ch.heigvd.api.mailrobot.model.mail.Group;
import ch.heigvd.api.mailrobot.model.mail.Person;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * Modélisation d'un prank, défini par un groupe de victimes et un message.
 * Un prank doit posséder au minimum 2 victimes, à savoir un expéditeur et un destinataire.
 *
 * @author Stéphane Marengo
 * @author Loris Marzullo
 */
public class Prank {
   private static final int MINIMUM_GROUP_SIZE = 2;

   @Getter
   private Person expeditor;
   @Getter
   private String message;
   private List<Person> recipients;

   /**
    * Créé un prank. La première personne du groupe est l'expéditeur du
    * message et les personnes restantes sont les destinataires.
    *
    * @param group   le groupe de victimes
    * @param message le message
    * @throws IllegalArgumentException si le nombre de personnes dans le groupe est insuffisant
    */
   public Prank(@NonNull Group group, @NonNull String message) throws IllegalArgumentException {
      List<Person> victims = group.getPersons();

      if (victims.size() < MINIMUM_GROUP_SIZE)
         throw new IllegalArgumentException("Le groupe doit être composé au minimum de " + MINIMUM_GROUP_SIZE +
                 " personnes");

      expeditor = victims.get(0);
      recipients = victims.subList(1, victims.size());
      this.message = message;
   }

   /**
    * Retourne une liste non modifiable des destinataires.
    *
    * @return la liste non modifiable
    */
   public List<Person> getRecipients() {
      return Collections.unmodifiableList(recipients);
   }
}
