package tester;

import ai.opening.OpeningTreeNode;
import ai.opponent.OpponentProfile;
import ai.player.Darkboard;
import ai.player.Player;
import core.Globals;
import core.Move;
import database.GameDatabase;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import pgn.ExtendedPGNGame;
import umpire.local.LocalUmpire;

public class OpeningTester {
   public static OpeningTreeNode WHITE = null;
   public static OpeningTreeNode BLACK = null;
   OpeningTreeNode playerWhite = new OpeningTreeNode();
   OpeningTreeNode playerBlack;
   OpeningTreeNode opponentWhite;
   OpeningTreeNode opponentBlack;
   OpponentProfile profile;
   static int moveLimit = 12;

   public static OpeningTreeNode load(String s) {
      File f = new File(s);
      OpeningTreeNode result = null;

      try {
         FileInputStream fos = new FileInputStream(f);
         ObjectInputStream oos = new ObjectInputStream(fos);
         result = (OpeningTreeNode)oos.readObject();
         oos.close();
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      return result;
   }

   public OpeningTester(OpeningTreeNode otn, boolean w) {
      Vector<Move> mv = (new LocalUmpire(new Player(), new Player())).legalMoves(true);

      int k;
      for(k = 0; k < mv.size(); ++k) {
         this.playerWhite.addChild((Move)mv.get(k), true);
      }

      this.playerBlack = new OpeningTreeNode();
      mv = (new LocalUmpire(new Player(), new Player())).legalMoves(false);

      for(k = 0; k < mv.size(); ++k) {
         this.playerBlack.addChild((Move)mv.get(k), true);
      }

      this.opponentBlack = !w ? otn : null;
      this.opponentWhite = w ? otn : null;
      this.profile = new OpponentProfile();
      this.profile.openingBookWhite = this.opponentWhite;
      this.profile.openingBookBlack = this.opponentBlack;
   }

   public OpeningTester(GameDatabase gd, String playerName, String opponentName) {
      Vector<Move> mv = (new LocalUmpire(new Player(), new Player())).legalMoves(true);

      int k;
      for(k = 0; k < mv.size(); ++k) {
         this.playerWhite.addChild((Move)mv.get(k), true);
      }

      this.playerBlack = new OpeningTreeNode();
      mv = (new LocalUmpire(new Player(), new Player())).legalMoves(false);

      for(k = 0; k < mv.size(); ++k) {
         this.playerBlack.addChild((Move)mv.get(k), true);
      }

      this.opponentWhite = new OpeningTreeNode();
      this.opponentBlack = new OpeningTreeNode();

      for(k = 0; k < gd.gameNumber(); ++k) {
         ExtendedPGNGame pgn = gd.getGame(k);
         if (playerName == null || playerName.toUpperCase().equals(pgn.getWhite().toUpperCase())) {
            this.playerWhite.addGame(pgn, true, moveLimit, true);
         }

         if (playerName == null || playerName.toUpperCase().equals(pgn.getBlack().toUpperCase())) {
            this.playerBlack.addGame(pgn, false, moveLimit, true);
         }

         if (opponentName == null || opponentName.toUpperCase().equals(pgn.getWhite().toUpperCase())) {
            this.opponentWhite.addGame(pgn, true, moveLimit, true);
         }

         if (opponentName == null || opponentName.toUpperCase().equals(pgn.getBlack().toUpperCase())) {
            this.opponentBlack.addGame(pgn, false, moveLimit, true);
         }
      }

      this.profile = new OpponentProfile();
      this.profile.openingBookWhite = this.opponentWhite;
      this.profile.openingBookBlack = this.opponentBlack;
   }

   public OpeningTreeNode test(int timeLimit, int threads) {
      ExecutorService threadManager = Executors.newCachedThreadPool();
      Future[] f = new Future[threads];
      OpeningTester.TesterTask[] tt = new OpeningTester.TesterTask[threads];

      for(int k = 0; k < threads; ++k) {
         boolean color = this.opponentWhite == null;
         tt[k] = new OpeningTester.TesterTask(this, color, k);
         f[k] = threadManager.submit(tt[k]);
      }

      long millis = System.currentTimeMillis();
      long end = millis + (long)timeLimit - 5L;

      try {
         boolean finish = false;

         while(!finish) {
            Thread.sleep((long)timeLimit);
            long millis2 = System.currentTimeMillis();
            if (millis2 >= end) {
               finish = true;
            } else {
               timeLimit = (int)(end - millis2);
            }
         }
      } catch (Exception var13) {
      }

      for(int k = 0; k < threads; ++k) {
         tt[k].terminate();
         f[k].cancel(false);
      }

      return this.opponentWhite != null ? this.playerBlack : this.playerWhite;
   }

   public void test() {
      try {
         ExecutorService threadManager = Executors.newCachedThreadPool();
         Future[] f = new Future[Globals.threadNumber];

         for(int k = 0; k < Globals.threadNumber; ++k) {
            boolean color = k < Globals.threadNumber / 2;
            if (color && this.opponentWhite == null) {
               color = false;
            } else if (!color && this.opponentBlack == null) {
               color = true;
            }

            f[k] = threadManager.submit(new OpeningTester.TesterTask(this, color, k));
         }

         boolean alt = false;

         while(true) {
            try {
               Thread.sleep(60000L);
            } catch (Exception var5) {
            }

            System.out.println("White stats");
            System.out.println(this.playerWhite);

            int k;
            for(k = 0; k < this.playerWhite.moveChildren.size(); ++k) {
               System.out.println(this.playerWhite.moveChildren.get(k));
            }

            System.out.println("Black stats");
            System.out.println(this.playerBlack);

            for(k = 0; k < this.playerBlack.moveChildren.size(); ++k) {
               System.out.println(this.playerBlack.moveChildren.get(k));
            }

            alt = !alt;

            for(k = 0; k < Globals.threadNumber; ++k) {
               if (f[k].isDone()) {
                  f[k] = threadManager.submit(new OpeningTester.TesterTask(this, k < Globals.threadNumber / 2, k));
               }
            }
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }
   }

   public static void main(String[] args) {
      String path = System.getProperty("user.home") + "/darkboard_data/";
      Darkboard.initialize(path);
      GameDatabase db = new GameDatabase();
      Globals.threadNumber = 2;
      db.addGames(new File(OpeningTester.class.getResource("/Darkboard/databasecream.pgn").getPath()));
      db.addGames(new File(OpeningTester.class.getResource("/Darkboard/db2.pgn").getPath()));
      (new OpeningTester(db, "dcencjnwejneih", "meisterzinger")).test();
   }

   public class TesterTask implements Runnable {
      OpeningTester ot;
      boolean white;
      int number;
      boolean terminated = false;

      public TesterTask(OpeningTester o, boolean w, int n) {
         this.ot = o;
         this.white = w;
         this.number = n;
      }

      public void evaluationRun() {
         OpeningTreeNode var10000;
         if (this.white) {
            var10000 = OpeningTester.this.playerWhite;
         } else {
            var10000 = OpeningTester.this.playerBlack;
         }

      }

      public void terminate() {
         this.terminated = true;
      }

      public void run() {
         this.evaluationRun();
      }
   }
}
