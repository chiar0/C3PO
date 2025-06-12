package core.eval;

import core.EvaluationFunctionComponent;
import core.Metaposition;
import core.Move;
import java.util.Vector;

public class KRRKMainComponent extends EvaluationFunctionComponent {
   boolean[] rows = new boolean[8];
   boolean[] columns = new boolean[8];
   boolean[] attackerrows = new boolean[8];
   boolean[] attackercolumns = new boolean[8];

   public float evaluate(Metaposition start, Metaposition dest, Move m, Vector history) {
      float result = 0.0F;

      int mateDetector;
      for(mateDetector = 0; mateDetector < 8; ++mateDetector) {
         this.rows[mateDetector] = this.columns[mateDetector] = this.attackerrows[mateDetector] = this.attackercolumns[mateDetector] = false;
      }

      mateDetector = KRKMainComponent.stalemateAlert(start, m, dest);
      if (mateDetector < 0) {
         result -= 10000.0F;
      }

      for(int k = 0; k < 8; ++k) {
         for(int j = 0; j < 8; ++j) {
            int p = dest.getFriendlyPiece(k, j);
            if (p == 3 || p == 4) {
               if (this.rookUnderAttack(dest, k, j) == 0) {
                  result -= 10000.0F;
               }

               this.attackerrows[k] = true;
               this.attackercolumns[j] = true;
            }
         }
      }

      if (result == 0.0F && mateDetector > 0) {
         result += 10000.0F;
      }

      result += (float)this.columnBonus(dest);
      return result;
   }

   private int columnBonus(Metaposition m) {
      int result = 0;

      int bestStreak;
      for(bestStreak = 0; bestStreak < 8; ++bestStreak) {
         for(int j = 0; j < 8; ++j) {
            if (m.canContainEnemyKing((byte)bestStreak, (byte)j)) {
               this.rows[bestStreak] = true;
               this.columns[j] = true;
               --result;
               if (this.attackerrows[bestStreak] && bestStreak != 0 && bestStreak != 7 && !this.attackerrows[bestStreak - 1] && !this.attackerrows[bestStreak + 1]) {
                  result -= 5;
               }

               if (this.attackercolumns[bestStreak] && bestStreak != 0 && bestStreak != 7 && !this.attackercolumns[bestStreak - 1] && !this.attackercolumns[bestStreak + 1]) {
                  result -= 5;
               }
            }
         }
      }

      bestStreak = 0;

      for(int k = 0; k < 4; ++k) {
         int streak = 0;
         boolean[] sequence;
         if (k < 2) {
            sequence = this.rows;
         } else {
            sequence = this.columns;
         }

         int f;
         if (k % 2 == 0) {
            for(f = 0; f < 8 && !sequence[f]; ++f) {
               ++streak;
            }
         } else {
            for(f = 7; f >= 0 && !sequence[f]; --f) {
               ++streak;
            }
         }

         if (streak > bestStreak) {
            bestStreak = streak;
         }
      }

      result += bestStreak * 12;
      return result;
   }

   private int rookUnderAttack(Metaposition m, int rookx, int rooky) {
      if (m.owner.globals.protectionMatrix[rookx][rooky] > 0) {
         return 1;
      } else {
         for(int k = rookx - 1; k <= rookx + 1; ++k) {
            for(int j = rooky - 1; j <= rooky + 1; ++j) {
               if (k >= 0 && k <= 7 && j >= 0 && j <= 7 && (k != rookx || j != rooky) && m.canContain((byte)k, (byte)j, (byte)5)) {
                  return 0;
               }
            }
         }

         return 1;
      }
   }
}
