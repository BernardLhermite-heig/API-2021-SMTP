package ch.heigvd.api.mailrobot.model.prank;

import ch.heigvd.api.mailrobot.config.ConfigurationManager;
import ch.heigvd.api.mailrobot.model.mail.Group;
import ch.heigvd.api.mailrobot.model.mail.Person;

import java.util.*;

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
   private List<Person> targets;
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

      // Créé une copie pour pouvoir la mélanger
      targets = new LinkedList<>(config.getTargets());

      int nbGroups = config.getNumberOfGroups();
      if (nbGroups <= 0)
         throw new IllegalArgumentException("Number of group must be > 0.");

      int targetsCount = targets.size();
      if (targetsCount < MIN_GROUP_SIZE)
         throw new IllegalArgumentException("Not enough targets (" + targetsCount + "). Minimum required: " + targetsCount * nbGroups + ".");

      messages = config.getMessages();
      Collections.shuffle(targets);
   }

   /**
    * Génère et retourne une liste de pranks.
    *
    * @return la liste des pranks
    */
   public List<Prank> generatePranks() {
      int nbPersons = targets.size();
      int nbGroups = config.getNumberOfGroups();
      int groupSize = nbPersons / nbGroups;

      List<Prank> pranks = new LinkedList<>();

      // Crée un prank pour chaque groupe
      List<Group> groups = generateGroups(nbGroups, groupSize);
      for (Group group : groups) {
         pranks.add(new Prank(group, messages.get(RANDOM.nextInt(messages.size()))));
      }
      return pranks;

   }

   /**
    * Génère et retourne une liste de groupes contenant les personnes de la configuration
    *
    * @param nbGroups  le nombre de groupes à générer
    * @param groupSize le nombre de personnes par groupe
    * @return la liste des groupes
    */
   private List<Group> generateGroups(int nbGroups, int groupSize) {
      List<Group> groups = new LinkedList<>();

      Iterator<Person> it = targets.iterator();
      for (int i = 0; i < nbGroups; i++) {
         Group group = new Group();

         for (int j = 0; j < groupSize; j++) {
            group.addPerson(it.next());
         }

         // Ajoute le surplus de personnes (nbGroups % groupSize) au dernier groupe
         if (i == nbGroups - 1) {
            it.forEachRemaining(group::addPerson);
         }

         groups.add(group);
      }
      return groups;
   }
}
