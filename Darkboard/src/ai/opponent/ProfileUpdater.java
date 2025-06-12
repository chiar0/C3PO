package ai.opponent;

import ai.opening.OpeningTreeNode;
import ai.player.Darkboard;
import java.io.File;
import java.util.Vector;
import pgn.ExtendedPGNGame;
import tester.OpeningTester;

public class ProfileUpdater implements Runnable {
   public static ProfileUpdater globalUpdater = new ProfileUpdater();
   public static int DURATION = 30;
   Vector<ProfileUpdater.ProfileTask> queue = new Vector();
   boolean timeCompression = true;
   boolean emptyProfiles = false;

   public void addTask(String player, ExtendedPGNGame g) {
      if (player != null && g != null) {
         boolean w = player.toLowerCase().equals(g.getWhite().toLowerCase());
         this.addTask(player, w, g);
      }
   }

   public synchronized void addTask(String player, boolean w, ExtendedPGNGame g) {
      ProfileUpdater.ProfileTask pt = new ProfileUpdater.ProfileTask();
      pt.name = player;
      pt.white = w;
      pt.game = g;
      this.queue.add(pt);
      this.notifyAll();
   }

   private void handleTask(ProfileUpdater.ProfileTask pt) {
      OpponentProfile op = OpponentProfile.getProfile(pt.name);
      OpeningTreeNode otn = pt.white ? op.openingBookWhite : op.openingBookBlack;
      if (pt.game != null) {
         otn.addGame(pt.game, pt.white, 12, false);
      }

      if (otn.visits >= 10) {
         OpeningTester ot = new OpeningTester(otn, pt.white);
         int duration = DURATION * 1000;
         int q = this.queue.size();
         if (this.timeCompression) {
            for(int k = 2; k < q; ++k) {
               duration /= 2;
            }
         }

         OpeningTreeNode strategy = ot.test(duration, 1);
         strategy.prune();
         System.out.println("Best strategy for " + pt.name + ": " + strategy);
         if (pt.white) {
            op.customStrategyBlack = strategy;
         } else {
            op.customStrategyWhite = strategy;
         }
      }

      if (otn.visits >= 10 || pt.game != null) {
         op.save();
      }

      if (this.emptyProfiles) {
         OpponentProfile.profiles.clear();
      }

   }

   public void run() {
      try {
         while(true) {
            synchronized(this) {
               while(this.queue.size() == 0) {
                  this.wait();
               }
            }

            ProfileUpdater.ProfileTask pt = (ProfileUpdater.ProfileTask)this.queue.remove(0);
            this.handleTask(pt);
         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }
   }

   public static void main(String[] args) {
      String path = System.getProperty("user.home") + "/darkboard_data/";
      Darkboard.initialize(path);
      globalUpdater.emptyProfiles = true;
      globalUpdater.timeCompression = false;
      new File(path + "profiles/");
      String[] list = new String[]{"darkboard"};

      for(int k = 0; k < list.length; ++k) {
         if (!list[k].startsWith(".")) {
            globalUpdater.addTask(list[k], true, (ExtendedPGNGame)null);
            globalUpdater.addTask(list[k], false, (ExtendedPGNGame)null);
         }
      }

   }

   public class ProfileTask {
      String name;
      boolean white;
      ExtendedPGNGame game;
   }
}
