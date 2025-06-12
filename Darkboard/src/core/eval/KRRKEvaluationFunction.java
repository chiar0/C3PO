package core.eval;

import core.EvaluationFunction;
import core.Metaposition;
import core.Move;
import java.util.Vector;

public class KRRKEvaluationFunction extends EvaluationFunction {
   int[] pieces = new int[11];
   Metaposition[] s2 = new Metaposition[5];
   public static boolean verbose = false;

   public KRRKEvaluationFunction() {
      this.addComponent(new KRRKMainComponent());
   }

   public String getName() {
      return "KRRK Evaluation Function";
   }

   public boolean useLoopDetector() {
      return true;
   }

   public boolean useKillerPruning() {
      return false;
   }

   public float getExplorationAlpha(Metaposition m) {
      return 0.05F;
   }

   public float getNodeMultFactor(Metaposition m) {
      return 1.0F;
   }

   public boolean canHandleSituation(Metaposition root, int capx, int capy, int check1, int check2, int tries) {
      return root.pawnsLeft + root.piecesLeft == 0 && root.owner.globals.queens + root.owner.globals.rooks > 1;
   }

   private static boolean moveCertainlyIllegal(Metaposition source, Move m, Metaposition dest) {
      int candidates = 0;
      if (m.piece != 5) {
         return false;
      } else if (dest.getSquareWithEnemyPiece(5) >= 0) {
         return false;
      } else {
         for(byte x = 0; x < 8; ++x) {
            for(byte y = 0; y < 0; ++y) {
               if (source.canContainEnemyKing(x, y)) {
                  int dx = x - m.toX;
                  int dy = y - m.toY;
                  if (dx < 0) {
                     dx = -dx;
                  }

                  if (dy < 0) {
                     dy = -dy;
                  }

                  int d = dx > dy ? dx : dy;
                  if (d > 1) {
                     ++candidates;
                  }
               }
            }
         }

         if (candidates > 0) {
            return false;
         } else {
            return true;
         }
      }
   }

   public Metaposition generateMostLikelyEvolution(Metaposition source, Move m) {
      Vector v = new Vector();
      Metaposition standard = super.generateMostLikelyEvolution(source, m);
      this.s2[0] = standard;
      this.s2[1] = Metaposition.evolveAfterMove(source, m, 0, -1, -1, 2, 0, 0);
      this.s2[2] = Metaposition.evolveAfterMove(source, m, 0, -1, -1, 1, 0, 0);

      for(int k = 0; k < 3; ++k) {
         if (verbose) {
            System.out.println("->" + this.s2[k].getSquareWithEnemyPiece(5) + " " + this.evaluate(source, this.s2[k], m, v));
         }

         if (moveCertainlyIllegal(source, m, this.s2[k])) {
            this.s2[k] = null;
         }
      }

      Metaposition best = null;
      float bestScore = 1000000.0F;

      for(int k = 0; k < 3; ++k) {
         float sc = this.s2[k] != null ? this.evaluate(source, this.s2[k], m, v) : 2000000.0F;
         if (sc < bestScore) {
            bestScore = sc;
            best = this.s2[k];
         }
      }

      return best;
   }

   public float getMinValueForExpansion() {
      return -200.0F;
   }

   public float getMaxValueForExpansion() {
      return 5000.0F;
   }
}
