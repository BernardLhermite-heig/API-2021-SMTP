package ch.heigvd.api.mailrobot.model.prank;

import ch.heigvd.api.mailrobot.TestWithFiles;
import ch.heigvd.api.mailrobot.config.ConfigurationManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Stéphane Marengo
 * @author Loris Marzullo
 */
class PrankGeneratorTest extends TestWithFiles {
   @Test
   void itShouldGeneratePranks() throws IOException {
      ConfigurationManager config = new ConfigurationManager(CONFIG_DIR, CONFIG_FILE, MESSAGES_FILE, TARGETS_FILE);

      List<Prank> prankList = new PrankGenerator(config).generatePranks();

      assertEquals(2, prankList.size());
   }

   @Test
   void itShouldThrowWhenThereIsNotEnoughTargets() throws IOException {
      ConfigurationManager config = new ConfigurationManager(CONFIG_DIR, CONFIG_FILE, MESSAGES_FILE, TARGETS_SMALL_FILE);

      assertThrows(IllegalArgumentException.class, () -> new PrankGenerator(config));
   }
}
