package core.eval;

import ai.chunk.regboard.RegBoardArray;
import ai.player.Player;
import core.EvaluationFunction;
import core.EvaluationTree;
import core.Metaposition;
import core.Move;
import java.util.Vector;

public class KRKEvaluationFunction extends EvaluationFunction {
   int[] pieces = new int[11];
   Metaposition[] s2 = new Metaposition[5];
   public static boolean verbose = false;
   public static int quadrant = -1;
   public static int[] deltaX = new int[]{-1, 1, -1, 1};
   public static int[] deltaY = new int[]{-1, -1, 1, 1};
   Player owner;
   static RegBoardArray boyce;
   EvaluationTree algorithmicTree;

   public KRKEvaluationFunction(Player p) {
      if (boyce == null) {
         boyce = new RegBoardArray("darkboard_data/boyce.txt");
      }

      this.owner = p;
      this.addComponent(new KRKMainComponent());
   }

   public String getName() {
      return "KRK Evaluation Function";
   }

   public boolean useLoopDetector() {
      return true;
   }

   public boolean useKillerPruning() {
      return false;
   }

   public float getExplorationAlpha(Metaposition m) {
      return 0.02F;
   }

   public float getNodeMultFactor(Metaposition m) {
      return 1.0F;
   }

   public boolean canHandleSituation(Metaposition root, int capx, int capy, int check1, int check2, int tries) {
      return root.pawnsLeft + root.piecesLeft == 0 && root.owner.globals.queens == 0 && root.owner.globals.rooks == 1;
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

   public Move getAutomaticMove(Metaposition m) {
      return boyce.getMoveSuggestion(m, this.owner.bannedMoves);
   }

   public void algorithmicMoveAccepted(Metaposition m1, Move move, Metaposition m2) {
      if (this.algorithmicTree != null) {
         if (this.algorithmicTree != null && this.algorithmicTree.getChildNumber() > 0) {
            this.algorithmicTree = this.algorithmicTree.getChild(0);
         } else {
            this.algorithmicTree = null;
         }
      }

   }

   public void algorithmicMoveRejected(Metaposition m1, Move move, Metaposition m2) {
      if (this.algorithmicTree != null) {
         if (this.algorithmicTree != null && this.algorithmicTree.getChildNumber() > 0) {
            this.algorithmicTree.removeChild(0);
            if (this.algorithmicTree.getChildNumber() < 1) {
               this.algorithmicTree = null;
            }
         } else {
            this.algorithmicTree = null;
         }
      }

   }

   public boolean meetAlgorithmicConditions(Metaposition m) {
      quadrant = -1;
      if (m.getSquareWithPiece(2) == -1 && m.getSquareWithPiece(1) == -1 && m.getSquareWithPiece(0) == -1) {
         int k = m.getSquareWithPiece(3);
         int rx = k / 8;
         int ry = k % 8;
         int j = m.getSquareWithPiece(5);
         int kx = j / 8;
         int ky = j % 8;
         int[] quad = new int[4];

         int x;
         int y;
         for(x = 0; x < 8; ++x) {
            for(y = 0; y < 8; ++y) {
               if (m.canContainEnemyKing((byte)x, (byte)y)) {
                  int var10002;
                  if (x <= rx && y <= ry) {
                     var10002 = quad[0]++;
                  }

                  if (x >= rx && y <= ry) {
                     var10002 = quad[1]++;
                  }

                  if (x <= rx && y >= ry) {
                     var10002 = quad[2]++;
                  }

                  if (x >= rx && y >= ry) {
                     var10002 = quad[3]++;
                  }
               }
            }
         }

         x = 0;

         for(y = 0; y < 4; ++y) {
            if (quad[y] > 0) {
               ++x;
            }
         }

         if (x > 1) {
            return false;
         } else {
            for(y = 0; y < 4; ++y) {
               if (quad[y] > 0) {
                  quadrant = y;
               }
            }

            y = deltaX[quadrant];
            int dy = deltaY[quadrant];
            if (kx == rx + y && ky == ry + dy) {
               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private EvaluationTree makeTree(Move m) {
      EvaluationTree et = new EvaluationTree();
      et.m = m;
      return et;
   }

   protected void makeAlgorithmicTree(Metaposition m) {
      int k = m.getSquareWithPiece(3);
      int rx = k / 8;
      int ry = k % 8;
      int j = m.getSquareWithPiece(5);
      int kx = j / 8;
      int ky = j % 8;
      int dx = deltaX[quadrant];
      int dy = deltaY[quadrant];
      int distanceX = dx < 0 ? rx : 7 - rx;
      int distanceY = dy < 0 ? ry : 7 - ry;
      boolean var10000 = distanceX <= distanceY;
      this.algorithmicTree = new EvaluationTree();
   }
}
