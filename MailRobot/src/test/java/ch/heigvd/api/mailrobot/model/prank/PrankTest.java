package ch.heigvd.api.mailrobot.model.prank;

import ch.heigvd.api.mailrobot.model.mail.Group;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Stéphane Marengo
 * @author Loris Marzullo
 */
class PrankTest {
    @Test
    void itShouldThrowWhenGroupIsTooSmall() {
        assertThrows(IllegalArgumentException.class, () -> new Prank(new Group(), "message"));
    }
}