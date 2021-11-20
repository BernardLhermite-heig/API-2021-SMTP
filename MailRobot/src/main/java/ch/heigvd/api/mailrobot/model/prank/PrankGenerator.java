package ch.heigvd.api.mailrobot.model.prank;

import ch.heigvd.api.mailrobot.config.ConfigurationManager;
import ch.heigvd.api.mailrobot.model.mail.Group;

import java.util.ArrayList;
import java.util.List;

public class PrankGenerator {

   private ConfigurationManager config;
   private final List<Group> groups = new ArrayList<>();

   PrankGenerator(ConfigurationManager config) {
      this.config = config;
   }

   public List<Prank> generatePranks() {

      // TODO
      // 1. Diviser la liste d'adresse par nbrGroupes choisi (récupérer dans config)
      // 2. Choisir une adresse au hasard pour envoyer des pranks

      return null;
   }
}
