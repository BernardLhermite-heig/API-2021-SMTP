package ch.heigvd.api.mailrobot.model.prank;

import ch.heigvd.api.mailrobot.config.ConfigurationManager;
import ch.heigvd.api.mailrobot.model.mail.Group;
import ch.heigvd.api.mailrobot.model.mail.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Classe permettant de générer une liste de pranks à partir d'une configuration donnée
 *
 * @author Stéphane Marengo
 * @author Loris Marzullo
 */
public class PrankGenerator {
   private static final int MIN_GROUP_SIZE = 3;
   private final static Random RANDOM = new Random();

   private ConfigurationManager config;
   private List<Person> persons;
   private List<String> messages;

   /**
    * Créé un générateur de prank utilisant la configuration fournie.
    * <p>
    * Remarque : la liste des personnes est mélangée.
    *
    * @param config la configuration à utiliser lors de la génération de prank
    * @throws IllegalArgumentException si le nombre de personnes n'est pas suffisant pour générer des pranks
    *                                  ou que le nombre de groupes n'est pas strictement positif
    */
   public PrankGenerator(ConfigurationManager config) throws IllegalArgumentException {
      this.config = config;
      persons = config.getPersons();

      int nbGroups = config.getNumberOfGroups();
      if (nbGroups <= 0)
         throw new IllegalArgumentException("Le nombre de groupe doit être strictement positif.");

      int nbPersons = persons.size();
      if (nbPersons < MIN_GROUP_SIZE)
         throw new IllegalArgumentException("Le nombre de personnes (" + nbPersons + ") n'est pas suffisant. Il faut " +
                 "au minimum " + nbPersons * nbGroups + " personnes.");

      messages = config.getMessages();
      Collections.shuffle(persons);
   }

   /**
    * Génère et retourne une liste de pranks.
    *
    * @return la liste des pranks
    */
   public List<Prank> generatePranks() {
      int nbPersons = persons.size();
      int nbGroups = config.getNumberOfGroups();
      int groupSize = nbPersons / nbGroups;

      List<Prank> pranks = new ArrayList<>();

      // Crée un prank pour chaque groupe
      List<Group> groups = generateGroups(nbGroups, groupSize, nbPersons % nbGroups);
      for (int i = 0; i < nbGroups; i++) {
         pranks.add(new Prank(groups.get(i), messages.get(RANDOM.nextInt(messages.size()))));
      }
      return pranks;

   }

   /**
    * Génère et retourne une liste de groupes contenant les personnes de la configuration
    *
    * @param nbrGroups         nombre de groupes à générer
    * @param nbrPersonsInGroup nombre de personnes par groupe
    * @return la liste des groupes
    */
   private List<Group> generateGroups(int nbrGroups, int nbrPersonsInGroup, int nbrExtraPersons) {
      List<Group> groups = new ArrayList<>();

      for (int i = 0; i < nbrGroups; i++)
      {
         Group group = new Group();
         for (int j = 0; j < nbrPersonsInGroup + (i == nbrGroups - 1 ? nbrExtraPersons : 0); j++)
         {
            group.addPerson(persons.get(i * nbrPersonsInGroup + j));
         }
         groups.add(group);
      }
      return groups;
   }
}
