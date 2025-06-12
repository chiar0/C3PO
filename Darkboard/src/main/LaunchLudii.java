package main;

import agent.DarkboardAgent;
import app.StartDesktopApp;
import utils.AIRegistry;

public class LaunchLudii {
   public static void main(String[] args) {
      if (!AIRegistry.registerAI("Darkboard", () -> {
         return new DarkboardAgent();
      }, (game) -> {
         return true;
      })) {
         System.err.println("WARNING! Failed to register AI because one with that name already existed!");
      }

      StartDesktopApp.main(new String[0]);
   }
}
