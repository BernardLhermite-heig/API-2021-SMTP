package ch.heigvd.api.mailrobot.model.prank;

import lombok.Getter;

import ch.heigvd.api.mailrobot.model.mail.Person;
import ch.heigvd.api.mailrobot.model.mail.Group;

import java.util.Collections;
import java.util.List;

/**
 * Modélisation d'un prank.
 *
 * @author Stéphane Marengo
 * @author Loris Marzullo
 */
public class Prank {
   @Getter
   private final Person victimSender;
   private final List<Person> recipientsVictims;
   @Getter
   private final String message;

   /**
    * Créé un prank, défini par un groupe de victimes et un message. La première personne du groupe est l'expéditeur du
    * message et les personnes restantes sont les destinataires.
    *
    * @param group   groupe de victimes
    * @param message le message
    */
   public Prank(Group group, String message) {

      // TODO : Tester que le groupe a assez de personnes ?

      List<Person> victims = group.getPersons();
      this.victimSender = victims.get(0);
      this.recipientsVictims = victims.subList(1, victims.size());
      this.message = message;
   }

   /**
    * Retourne une liste non modifiable des destinataires.
    *
    * @return la liste non modifiable
    */
   public List<Person> getRecipientsVictims() {
      return Collections.unmodifiableList(recipientsVictims);
   }
}
