package tester;

import ai.player.Darkboard;
import ai.player.DeepDarkboard10;
import ai.player.Player;
import core.Globals;
import java.util.Vector;
import pgn.ExtendedPGNGame;
import umpire.local.LocalUmpire;

public class Tester {
   double trueRatio = 0.8D;
   static int parameter = 0;
   Vector<Tester.PlayerData> players = new Vector();

   public void test(int rounds, int printEvery) {
      int k;
      for(k = 0; k < this.getPlayerNumber(); ++k) {
         Tester.PlayerData pd = new Tester.PlayerData();
         pd.elo = this.getStartingElo();
         pd.scores = new int[this.getPlayerNumber()][3];
         this.players.add(pd);
      }

      for(k = 0; k != rounds; ++k) {
         for(int p1 = 0; p1 < this.getPlayerNumber(); ++p1) {
            for(int p2 = 0; p2 < this.getPlayerNumber(); ++p2) {
               if (p1 != p2) {
                  this.match(p1, p2);
                  System.out.println("-------");

                  for(int p = 0; p < this.getPlayerNumber(); ++p) {
                     System.out.println(p + ": " + ((Tester.PlayerData)this.players.get(p)).playerName + " (" + (int)((Tester.PlayerData)this.players.get(p)).elo + ")");

                     for(int q = 0; q < this.getPlayerNumber(); ++q) {
                        if (q != p) {
                           System.out.println("-> " + q + ": " + ((Tester.PlayerData)this.players.get(p)).scores[q][0] + " wins, " + ((Tester.PlayerData)this.players.get(p)).scores[q][1] + " draws, " + ((Tester.PlayerData)this.players.get(p)).scores[q][2] + " losses");
                        }
                     }
                  }

                  System.out.println("-------");
               }
            }
         }
      }

   }

   public void test2(int rounds, int printEvery) {
      int k;
      for(k = 0; k < this.getPlayerNumber(); ++k) {
         Tester.PlayerData pd = new Tester.PlayerData();
         pd.elo = this.getStartingElo();
         pd.scores = new int[this.getPlayerNumber()][3];
         this.players.add(pd);
      }

      while(true) {
         for(k = 0; k != rounds; ++k) {
            int period = k % (2 * (this.getPlayerNumber() - 1));
            int smallPeriod = period % (this.getPlayerNumber() - 1);
            boolean[] played = new boolean[this.getPlayerNumber()];

            int matchNumber;
            for(matchNumber = 0; matchNumber < played.length; ++matchNumber) {
               played[matchNumber] = false;
            }

            matchNumber = this.getPlayerNumber() / 2;

            int p;
            int q;
            for(p = 0; p < matchNumber; ++p) {
               q = 0;

               int p2;
               for(p2 = 0; p2 < this.getPlayerNumber(); ++p2) {
                  if (!played[p2]) {
                     q = p2;
                     played[p2] = true;
                     break;
                  }
               }

               p2 = (q + smallPeriod + 1) % this.getPlayerNumber();
               if (period != smallPeriod) {
                  int swap = q;
                  q = p2;
                  p2 = swap;
               }

               this.match(q, p2);
            }

            if ((k + 1) % printEvery == 0) {
               System.out.println("-------");

               for(p = 0; p < this.getPlayerNumber(); ++p) {
                  System.out.println(p + ": " + ((Tester.PlayerData)this.players.get(p)).playerName + " (" + ((Tester.PlayerData)this.players.get(p)).elo + ")");

                  for(q = 0; q < this.getPlayerNumber(); ++q) {
                     if (q != p) {
                        System.out.println("-> " + q + ": " + ((Tester.PlayerData)this.players.get(p)).scores[q][0] + " wins, " + ((Tester.PlayerData)this.players.get(p)).scores[q][1] + " draws, " + ((Tester.PlayerData)this.players.get(p)).scores[q][2] + " losses");
                     }
                  }
               }

               System.out.println("-------");
            }
         }

         Tester.PlayerData pd = (Tester.PlayerData)this.players.get(0);
         double score1 = (double)pd.scores[1][0] + 0.5D * (double)pd.scores[1][1];
         double score2 = (double)pd.scores[1][2] + 0.5D * (double)pd.scores[1][1];
         pd.scores[1][0] = 0;
         pd.scores[1][1] = 0;
         pd.scores[1][2] = 0;
         double ratio = score1 / (score1 + score2);
         double correctionFactor = (ratio - 0.5D) * 2.0D;
         this.trueRatio += 0.05D * correctionFactor;
         if (this.trueRatio < 0.0D) {
            this.trueRatio = 0.0D;
         }

         if (this.trueRatio > 1.0D) {
            this.trueRatio = 1.0D;
         }

         double correctionIntensity = correctionFactor < 0.0D ? -correctionFactor : correctionFactor;
         rounds = (int)((double)rounds + (double)rounds * 1.0D * (1.0D - correctionIntensity));
      }
   }

   public void asymmetricTest(int rounds, int printEvery) {
      int k;
      for(k = 0; k < this.getPlayerNumber(); ++k) {
         Tester.PlayerData pd = new Tester.PlayerData();
         pd.elo = this.getStartingElo();
         pd.scores = new int[this.getPlayerNumber()][3];
         this.players.add(pd);
      }

      for(k = 0; k != rounds; ++k) {
         int period = k % (2 * (this.getPlayerNumber() - 1));
         int smallPeriod = period % (this.getPlayerNumber() - 1);
         boolean[] played = new boolean[this.getPlayerNumber()];

         int matchNumber;
         for(matchNumber = 0; matchNumber < played.length; ++matchNumber) {
            played[matchNumber] = false;
         }

         matchNumber = this.getPlayerNumber() / 2;

         int p;
         int q;
         for(p = 0; p < matchNumber; ++p) {
            q = 0;

            int p2;
            for(p2 = 0; p2 < this.getPlayerNumber(); ++p2) {
               if (!played[p2]) {
                  q = p2;
                  played[p2] = true;
                  break;
               }
            }

            p2 = (q + smallPeriod + 1) % this.getPlayerNumber();
            if (period != smallPeriod) {
               int swap = q;
               q = p2;
               p2 = swap;
            }

            this.asymmetricMatch(q, p2);
         }

         if ((k + 1) % printEvery == 0) {
            System.out.println("-------");

            for(p = 0; p < this.getPlayerNumber(); ++p) {
               System.out.println(p + ": " + ((Tester.PlayerData)this.players.get(p)).playerName + " (" + ((Tester.PlayerData)this.players.get(p)).elo + ")");

               for(q = 0; q < this.getPlayerNumber(); ++q) {
                  if (q != p) {
                     System.out.println("-> " + q + ": " + ((Tester.PlayerData)this.players.get(p)).scores[q][0] + " wins, " + ((Tester.PlayerData)this.players.get(p)).scores[q][1] + " draws, " + ((Tester.PlayerData)this.players.get(p)).scores[q][2] + " losses");
                  }
               }
            }

            System.out.println("-------");
         }
      }

   }

   public void test3(int rounds, int printEvery) {
      int k;
      for(k = 0; k < this.getPlayerNumber(); ++k) {
         Tester.PlayerData pd = new Tester.PlayerData();
         pd.elo = this.getStartingElo();
         pd.scores = new int[this.getPlayerNumber()][3];
         this.players.add(pd);
      }

      for(k = 0; k != rounds; ++k) {
         int period = k % (2 * (this.getPlayerNumber() - 1));
         int smallPeriod = period % (this.getPlayerNumber() - 1);
         boolean[] played = new boolean[this.getPlayerNumber()];

         int matchNumber;
         for(matchNumber = 0; matchNumber < played.length; ++matchNumber) {
            played[matchNumber] = false;
         }

         matchNumber = this.getPlayerNumber() / 2;

         for(int j = 0; j < matchNumber; ++j) {
            int p1 = 0;

            int p2;
            for(p2 = 0; p2 < this.getPlayerNumber(); ++p2) {
               if (!played[p2]) {
                  p1 = p2;
                  played[p2] = true;
                  break;
               }
            }

            p2 = (p1 + smallPeriod + 1) % this.getPlayerNumber();
            if (period != smallPeriod) {
               int swap = p1;
               p1 = p2;
               p2 = swap;
            }

            this.match(p1, p2);
         }

         if ((k + 1) % printEvery == 0) {
            System.out.println("-------");
            System.out.print(Globals.sampler);
            System.out.println("-------");
         }
      }

   }

   public LocalUmpire getUmpire(int player1, int player2) {
      Player p1 = this.getPlayer(player1, true);
      Player p2 = this.getPlayer(player2, false);
      if (((Tester.PlayerData)this.players.get(player1)).playerName == null) {
         ((Tester.PlayerData)this.players.get(player1)).playerName = p1.getPlayerName();
      }

      if (((Tester.PlayerData)this.players.get(player2)).playerName == null) {
         ((Tester.PlayerData)this.players.get(player2)).playerName = p2.getPlayerName();
      }

      return new LocalUmpire(p1, p2);
   }

   public void match(int a, int b) {
      double points1 = 0.0D;
      double points2 = 0.0D;
      LocalUmpire lu = this.getUmpire(a, b);
      lu.autoFinish = true;
      lu.adjudication = true;
      Player p1 = lu.getP1();
      lu.verbose = false;
      System.gc();

      try {
         ExtendedPGNGame g = lu.arbitrate();
         int var10002;
         if (lu.getGameOutcome() == 1) {
            if (lu.getWinner() == p1) {
               points1 = 1.0D;
               points2 = 0.0D;
               var10002 = ((Tester.PlayerData)this.players.get(a)).scores[b][0]++;
               var10002 = ((Tester.PlayerData)this.players.get(b)).scores[a][2]++;
            } else {
               points1 = 0.0D;
               points2 = 1.0D;
               var10002 = ((Tester.PlayerData)this.players.get(a)).scores[b][2]++;
               var10002 = ((Tester.PlayerData)this.players.get(b)).scores[a][0]++;
            }
         } else {
            points2 = 0.5D;
            points1 = 0.5D;
            var10002 = ((Tester.PlayerData)this.players.get(a)).scores[b][1]++;
            var10002 = ((Tester.PlayerData)this.players.get(b)).scores[a][1]++;
         }

         double elo1 = ((Tester.PlayerData)this.players.get(a)).elo;
         double elo2 = ((Tester.PlayerData)this.players.get(b)).elo;
         double p1expect = 1.0D / (1.0D + Math.pow(10.0D, (elo2 - elo1) / 400.0D));
         double p2expect = 1.0D / (1.0D + Math.pow(10.0D, (elo1 - elo2) / 400.0D));
         double p1delta = (points1 - p1expect) * this.getEloKFactor();
         double p2delta = (points2 - p2expect) * this.getEloKFactor();
         Tester.PlayerData var10000 = (Tester.PlayerData)this.players.get(a);
         var10000.elo += (double)((int)p1delta);
         var10000 = (Tester.PlayerData)this.players.get(b);
         var10000.elo += (double)((int)p2delta);
      } catch (Exception var22) {
         var22.printStackTrace(System.out);
      }

   }

   public void asymmetricMatch(int a, int b) {
      double points1 = 0.0D;
      double points2 = 0.0D;
      LocalUmpire lu = this.getUmpire(a, b);
      Player p1 = lu.getP1();
      lu.verbose = false;
      ExtendedPGNGame g = lu.arbitrate(a == 0 ? LocalUmpire.boardLayoutFromFEN("1nb1kbn1/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR") : LocalUmpire.boardLayoutFromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/1NB1KBN1"));
      System.out.println(g);
      int var10002;
      if (lu.getGameOutcome() == 1) {
         if (lu.getWinner() == p1) {
            points1 = 1.0D;
            points2 = 0.0D;
            var10002 = ((Tester.PlayerData)this.players.get(a)).scores[b][0]++;
            var10002 = ((Tester.PlayerData)this.players.get(b)).scores[a][2]++;
         } else {
            points1 = 0.0D;
            points2 = 1.0D;
            var10002 = ((Tester.PlayerData)this.players.get(a)).scores[b][2]++;
            var10002 = ((Tester.PlayerData)this.players.get(b)).scores[a][0]++;
         }
      } else {
         points2 = 0.5D;
         points1 = 0.5D;
         var10002 = ((Tester.PlayerData)this.players.get(a)).scores[b][1]++;
         var10002 = ((Tester.PlayerData)this.players.get(b)).scores[a][1]++;
      }

      double elo1 = ((Tester.PlayerData)this.players.get(a)).elo;
      double elo2 = ((Tester.PlayerData)this.players.get(b)).elo;
      double p1expect = 1.0D / (1.0D + Math.pow(10.0D, (elo2 - elo1) / 400.0D));
      double p2expect = 1.0D / (1.0D + Math.pow(10.0D, (elo1 - elo2) / 400.0D));
      double p1delta = (points1 - p1expect) * this.getEloKFactor();
      double p2delta = (points2 - p2expect) * this.getEloKFactor();
      Tester.PlayerData var10000 = (Tester.PlayerData)this.players.get(a);
      var10000.elo += p1delta;
      var10000 = (Tester.PlayerData)this.players.get(b);
      var10000.elo += p2delta;
   }

   public int getPlayerNumber() {
      return 2;
   }

   public Player getPlayer(int index, boolean white) {
      return index == 0 ? new DeepDarkboard10(white) : null;
   }

   public double getStartingElo() {
      return 1600.0D;
   }

   public double getEloKFactor() {
      return 0.1D;
   }

   public static void main(String[] args) {
      String path = System.getProperty("user.home") + "/darkboard_data/";
      Darkboard.initialize(path);
      if (args.length > 0) {
         parameter = Integer.parseInt(args[0]);
      }

      Globals.hasGui = false;
      Globals.threadNumber = 2;
      (new Tester()).test(-1, 1);
   }

   public class PlayerData {
      String playerName = null;
      double elo;
      int[][] scores;
   }
}
