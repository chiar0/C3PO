package ai.opponent;

import core.Metaposition;
import java.util.Random;
import java.util.Vector;

public class MetapositionConstraint {
   public Vector<int[]> squares = new Vector();
   public boolean[] pieces = new boolean[7];
   public int movesAgo = 0;
   public double fadeoffRate = 1.0D;
   public boolean unsatisfiable = false;

   public void addSquare(int x, int y, int prob) {
      int[] abc = new int[]{x, y, prob};
      this.squares.add(abc);
   }

   public double metapositionSatisfiesConstraint(Metaposition m) {
      if (this.unsatisfiable) {
         return 0.0D;
      } else {
         for(int k = 0; k < this.squares.size(); ++k) {
            int[] a = (int[])this.squares.get(k);
            int x = a[0];
            int y = a[1];
            int piece = m.getFriendlyPiece(x, y);
            if (this.pieces[piece]) {
               return 1.0D;
            }
         }

         return this.movesAgo == 0 ? 0.0D : 1.6D / (double)(m.pawnsLeft + m.piecesLeft + 1) * this.fadeoffRate * (double)this.movesAgo;
      }
   }

   public double pieceRatio(int x, int y) {
      int total = 0;
      int mine = 0;

      for(int k = 0; k < this.squares.size(); ++k) {
         total += ((int[])this.squares.get(k))[2];
         if (((int[])this.squares.get(k))[0] == x && ((int[])this.squares.get(k))[1] == y) {
            mine = ((int[])this.squares.get(k))[2];
         }
      }

      return 1.0D * (double)mine / (double)total;
   }

   public double pieceBonusWithConstraintVector(int x, int y, Vector<MetapositionConstraint> v) {
      double bonus = 1.0D;

      for(int k = 0; k < v.size(); ++k) {
         MetapositionConstraint mc = (MetapositionConstraint)v.get(k);
         if (mc != this) {
            bonus *= 1.0D + mc.pieceRatio(x, y);
         }
      }

      return bonus;
   }

   public int pieceType() {
      Random r = new Random();
      boolean var2 = false;

      int k;
      do {
         k = r.nextInt(7);
      } while(!this.pieces[k]);

      return k;
   }
}
