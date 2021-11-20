package ch.heigvd.api.mailrobot.model.mail;

import lombok.Getter;

public class Person {
   private static final int NB_ARGS = 3;

   @Getter
   private String firstName;
   @Getter
   private String lastName;
   @Getter
   private String address;
   
   public Person(String[] args) {
      if (args.length != NB_ARGS)
         throw new IllegalArgumentException(NB_ARGS + " arguments are required.");

      firstName = args[0];
      lastName = args[1];
      address = args[2];
   }
}
