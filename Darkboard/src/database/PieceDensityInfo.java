package database;

import ai.player.Player;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import pgn.ExtendedPGNGame;
import umpire.local.LocalUmpire;

public class PieceDensityInfo implements Serializable {
   public float[][][] data = new float[16][16][64];
   private static int[][][] support = new int[16][16][64];
   private static int[][][] supporttot = new int[16][16][64];

   public void getFromDatabase(GameDatabase gd) {
      if (gd != null) {
         LocalUmpire lu = new LocalUmpire((Player)null, (Player)null);

         int my;
         int opp;
         int sq;
         for(my = 0; my < 16; ++my) {
            for(opp = 0; opp < 16; ++opp) {
               for(sq = 0; sq < 64; ++sq) {
                  support[my][opp][sq] = supporttot[my][opp][sq] = 0;
               }
            }
         }

         for(my = gd.gameNumber() - 1; my >= 0; --my) {
            ExtendedPGNGame g = gd.getGame(my);
            lu.resetBoard();

            for(sq = 0; sq < g.getMoveNumber(); ++sq) {
               ExtendedPGNGame.PGNMoveData data = g.getMove(true, sq);
               if (data == null) {
                  break;
               }

               lu.doMove(data.finalMove, 0);
               this.examinePosition(lu, true);
               data = g.getMove(false, sq);
               if (data == null) {
                  break;
               }

               lu.doMove(data.finalMove, 1);
               this.examinePosition(lu, false);
            }

            if (my % 100 == 99) {
               System.out.print("*");
            }
         }

         for(my = 0; my < 16; ++my) {
            for(opp = 0; opp < 16; ++opp) {
               if (supporttot[my][opp][0] < 1) {
                  for(sq = 0; sq < 64; ++sq) {
                     this.data[my][opp][sq] = 1.0F * (float)(opp + 1) / 64.0F;
                  }
               } else {
                  for(sq = 0; sq < 64; ++sq) {
                     this.data[my][opp][sq] = 1.0F * (float)support[my][opp][sq] / (float)supporttot[my][opp][sq];
                  }
               }
            }
         }

      }
   }

   private void examinePosition(LocalUmpire lu, boolean white) {
      int who = white ? 0 : 1;
      int wp = lu.getPieceNumber(0) - 1;
      int bp = lu.getPieceNumber(1) - 1;

      for(int k = 0; k < 8; ++k) {
         for(int j = 0; j < 8; ++j) {
            int var10002;
            int index;
            if (white) {
               index = k * 8 + (7 - j);
               var10002 = supporttot[bp][wp][index]++;
               if (lu.occupied(k, j, who)) {
                  var10002 = support[bp][wp][index]++;
               }
            } else {
               index = k * 8 + j;
               var10002 = supporttot[wp][bp][index]++;
               if (lu.occupied(k, j, who)) {
                  var10002 = support[wp][bp][index]++;
               }
            }
         }
      }

   }

   public static PieceDensityInfo load(File f) {
      try {
         FileInputStream fis = new FileInputStream(f);
         ObjectInputStream ois = new ObjectInputStream(fis);
         PieceDensityInfo ob = (PieceDensityInfo)ois.readObject();
         ois.close();
         fis.close();
         return ob;
      } catch (Exception var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public void save(File f) {
      try {
         FileOutputStream fos = new FileOutputStream(f);
         ObjectOutputStream oos = new ObjectOutputStream(fos);
         oos.writeObject(this);
         oos.close();
         fos.close();
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }
}
