package ch.heigvd.api.mailrobot.model.mail;

import lombok.Getter;
import lombok.NonNull;

/**
 * Modélisation d'une personne.
 *
 * @author Stéphane Marengo
 * @author Loris Marzullo
 */
public class Person {
   private static final int NB_ARGS = 3;

   @Getter
   private final String firstName;
   @Getter
   private final String lastName;
   @Getter
   private final String address;

   /**
    * Créé une personne avec un nom, un prénom et une adresse mail.
    *
    * @param args tableau de String contenant le prénom, le nom et l'adresse mail de la personne
    */
   public Person(@NonNull String[] args) {
      if (args.length != NB_ARGS)
         throw new IllegalArgumentException(NB_ARGS + " arguments are required.");

      firstName = args[0];
      lastName = args[1];
      address = args[2];
   }

   @Override
   public String toString() {
      return firstName + " " + lastName + " <" + address + ">";
   }
}
