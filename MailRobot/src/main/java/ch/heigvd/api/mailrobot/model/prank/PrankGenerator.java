package ch.heigvd.api.mailrobot.model.prank;

import ch.heigvd.api.mailrobot.config.ConfigurationManager;
import ch.heigvd.api.mailrobot.model.mail.Group;
import ch.heigvd.api.mailrobot.model.mail.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe permettant de générer une liste de pranks à partir d'une configuration donnée
 *
 * @author Stéphane Marengo
 * @author Loris Marzullo
 */
public class PrankGenerator {

   private final static Logger LOG = Logger.getLogger(PrankGenerator.class.getName());
   private final static Random RANDOM = new Random();
   private final ConfigurationManager config;
   private final List<Person> persons;
   private final List<String> messages;

   private static final int MIN_GROUP_SIZE = 3;


   PrankGenerator(ConfigurationManager config) {
      this.config = config;
      persons = config.getPersons();
      messages = config.getMessages();
   }

   /**
    * Génère et retourne une liste de groupes contenant les personnes de la configuration
    *
    * @param nbrGroups nombre de groupes à générer
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

   /**
    * Génère et retourne une liste de pranks
    *
    * @return la liste des pranks
    */
   public List<Prank> generatePranks() {
      int nbrPersons = persons.size();
      int nbrGroups = config.getNumberOfGroups();
      int nbrPersonsInGroup = nbrPersons / nbrGroups;

      // Vérifie qu'il y a assez de personnes pour chaque groupe
      if (nbrPersonsInGroup < MIN_GROUP_SIZE) {
         LOG.log(Level.SEVERE, "Erreur : Nombre de personnes insuffisant, reçues " + nbrPersons
         + ". Nécessaires " + nbrPersons * nbrGroups);
         return null;
      } else {
         List<Prank> pranks = new ArrayList<>();

         // Crée un prank pour chaque groupe
         List<Group> groups = generateGroups(nbrGroups, nbrPersonsInGroup, nbrPersons % nbrGroups);
         for (int i = 0; i < nbrGroups; i++) {
            pranks.add(new Prank(groups.get(i), messages.get(RANDOM.nextInt(messages.size()))));
         }
         return pranks;
      }
   }
}
