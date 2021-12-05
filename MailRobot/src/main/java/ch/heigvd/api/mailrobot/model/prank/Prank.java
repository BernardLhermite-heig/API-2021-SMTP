package ch.heigvd.api.mailrobot.model.prank;

import ch.heigvd.api.mailrobot.model.mail.Group;
import ch.heigvd.api.mailrobot.model.mail.Person;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collections;
import java.util.Iterator;
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
   private static final String EOL = "\r\n";

   @Getter
   private Person expeditor;
   @Getter
   private String body;
   @Getter
   private String subject;
   private List<Person> recipients;

   /**
    * Créé un prank. La première personne du groupe est l'expéditeur du
    * message et les personnes restantes sont les destinataires. La première ligne du message correspond au sujet.
    *
    * @param group   le groupe de victimes
    * @param message le message
    * @throws IllegalArgumentException si le nombre de personnes dans le groupe est insuffisant ou que le message est
    *                                  mal formaté
    */
   public Prank(@NonNull Group group, @NonNull String message) throws IllegalArgumentException {
      List<Person> victims = group.getPersons();

      if (victims.size() < MINIMUM_GROUP_SIZE)
         throw new IllegalArgumentException("Group must have at least " + MINIMUM_GROUP_SIZE +
                 " persons");

      Iterator<String> it = message.lines().iterator();
      if (!it.hasNext())
         throw new IllegalArgumentException("Message must have at least 1 line defining the subject.");

      subject = it.next();

      StringBuilder builder = new StringBuilder();
      it.forEachRemaining(s -> builder.append(s).append(EOL));
      body = builder.toString();

      expeditor = victims.get(0);
      recipients = victims.subList(1, victims.size());
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
