package ch.heigvd.api.mailrobot.model.mail;

import java.util.ArrayList;
import java.util.List;

public class Group {
   private final List<Person> personnes = new ArrayList<>();

   public List<Person> getVictims() {
      return new ArrayList<>(personnes);
   }
   public void addVictim(Person p) {
      personnes.add(p);
   }
}
