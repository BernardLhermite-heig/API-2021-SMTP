package ch.heigvd.api.mailrobot.model.mail;

public class Person {

   private String firstName;
   private String lastName;
   private String adress;

   public Person(String firstName, String lastName, String adress) {
      this.firstName = firstName;
      this.lastName = lastName;
      this.adress = adress;
   }

   public String getFirstName() {
      return firstName;
   }
   public String getLastName() {
      return lastName;
   }
   public String getAdress() {
      return adress;
   }
}
