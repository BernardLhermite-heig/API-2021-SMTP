package ch.heigvd.api.mailrobot.model.prank;

import ch.heigvd.api.mailrobot.model.mail.Person;

import java.util.ArrayList;
import java.util.List;

public class Prank {
   private Person victimSender;
   private final List<Person> recipientsVictims = new ArrayList<>();
   private String message;

   public Person getVictimSender() {
      return victimSender;
   }
   public void setVictimSender(Person victimSender) {
      this.victimSender = victimSender;
   }

   public List<Person> getRecipientsVictims() {
      return new ArrayList<>(recipientsVictims);
   }
   public void addRecipientVictim(Person p) {
      recipientsVictims.add(p);
   }

   public String getMessage() {
      return message;
   }
   public void setMessage(String message) {
      this.message = message;
   }
}
