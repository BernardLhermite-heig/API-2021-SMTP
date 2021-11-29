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
   private String firstName;
   @Getter
   private String lastName;
   @Getter
   private String address;

   /**
    * Créé une personne depuis un tableau de String.
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

   /**
    * Créé une personne avec un nom, un prénom et une adresse mail.
    *
    * @param firstName le prénom de la personne
    * @param lastName  le nom de la personne
    * @param address   l'adresse mail de la personne
    */
   public Person(@NonNull String firstName, @NonNull String lastName, @NonNull String address) {
      this.firstName = firstName;
      this.lastName = lastName;
      this.address = address;
   }

   /**
    * Obtiens une chaîne de caractères représentant la personne de la forme :
    * prénom nom <adresse>
    *
    * @return la chaîne de caractères
    */
   @Override
   public String toString() {
      return firstName + " " + lastName + " <" + address + ">";
   }
}
