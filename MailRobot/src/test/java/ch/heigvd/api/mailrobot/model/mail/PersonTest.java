package ch.heigvd.api.mailrobot.model.mail;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonTest {
    @Test
    void itShouldThrowOnEmptyEmail() {
        assertThrows(IllegalArgumentException.class, () -> new Person("firstname", "lastname", ""));
    }
}