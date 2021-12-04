package ch.heigvd.api.mailrobot.model.mail;

import lombok.Getter;
import lombok.NonNull;

/**
 * Modélisation d'une personne.
 *
 * @author Stéphane Marengo
 * @author Loris Marzullo
 *
 */
public class Person {
   @Getter
   private String firstName;
   @Getter
   private String lastName;
   @Getter
   private String email;

   /**
    * Créé une personne avec un nom, un prénom et une adresse mail.
    *
    * @param firstName le prénom de la personne
    * @param lastName  le nom de la personne
    * @param email     l'adresse mail de la personne
    * @throws IllegalArgumentException si l'email est vide
    */
   public Person(@NonNull String firstName, @NonNull String lastName, @NonNull String email) {
      if (email.isEmpty())
         throw new IllegalArgumentException("Email cannot be empty.");

      this.firstName = firstName;
      this.lastName = lastName;
      this.email = email;
   }

   /**
    * Obtiens une chaîne de caractères représentant la personne de la forme :
    * prénom nom <adresse>
    *
    * @return la chaîne de caractères
    */
   public String toString() {
      return firstName + " " + lastName + " <" + email + ">";
   }
}
