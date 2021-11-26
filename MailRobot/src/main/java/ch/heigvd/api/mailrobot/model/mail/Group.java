package ch.heigvd.api.mailrobot.model.mail;

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
   private final List<Person> persons;

   /**
    * Créé un groupe.
    */
   public Group() {
      persons = new LinkedList<>();
   }

   /**
    * Ajoute la personne passée en paramètre au groupe.
    *
    * @param person la personne
    */
   public void addPerson(Person person) {
      persons.add(person);
   }

   /**
    * Retourne une liste non modifiable des personnes composant groupe.
    *
    * @return la liste non modifiable
    */
   public List<Person> getPersons() {
      return Collections.unmodifiableList(persons);
   }
}
