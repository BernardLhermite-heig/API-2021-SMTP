package ch.heigvd.api.mailrobot.model.mail;

import lombok.NonNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Modélisation d'un groupe de personnes.
 *
 * @author Stéphane Marengo
 * @author Loris Marzullo
 */
public class Group {
   private List<Person> persons;

   /**
    * Créé un groupe vide.
    */
   public Group() {
      persons = new LinkedList<>();
   }

   /**
    * Ajoute la personne passée en paramètre au groupe.
    *
    * @param person la personne à ajouter
    */
   public void addPerson(@NonNull Person person) {
      persons.add(person);
   }

   /**
    * Retourne une liste non modifiable des personnes composant le groupe.
    *
    * @return la liste des personnes
    */
   public List<Person> getPersons() {
      return Collections.unmodifiableList(persons);
   }
}
