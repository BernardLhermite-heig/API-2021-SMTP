package ch.heigvd.api.mailrobot.model.prank;

import ch.heigvd.api.mailrobot.config.ConfigurationManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrankGeneratorTest {
   private static final String CORRECT_CONFIG = "config.properties";
   private static final String MESSAGE_FILE = "messages.txt";
   private static final String TARGET_FILE = "targets.txt";

   @Test
   void itShouldGeneratePranks() throws IOException {
      ConfigurationManager config = new ConfigurationManager(CORRECT_CONFIG, MESSAGE_FILE, TARGET_FILE);

      List<Prank> prankList = new PrankGenerator(config).generatePranks();
   }
}
