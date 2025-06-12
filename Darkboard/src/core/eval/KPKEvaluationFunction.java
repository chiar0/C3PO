package core.eval;

import core.EvaluationFunction;
import core.Metaposition;
import umpire.local.LocalUmpire;

public class KPKEvaluationFunction extends EvaluationFunction {
   static int[] pawnLocs = new int[8];

   public KPKEvaluationFunction() {
      this.addComponent(new KPKMainComponent());
   }

   public String getName() {
      return "KPK Evaluation Function";
   }

   public boolean useKillerPruning() {
      return false;
   }

   public boolean canHandleSituation(Metaposition root, int capx, int capy, int check1, int check2, int tries) {
      return root.pawnsLeft + root.piecesLeft < 5 && root.owner.globals.queens == 0 && root.owner.globals.rooks == 0 && root.owner.globals.pawns > 0;
   }

   private static void prepareBoard(LocalUmpire u) {
      u.emptyBoard();
      u.insertPiece(5, 0, false);
      u.insertPiece(3, 0, true);
      u.insertPiece(5, 1, false);
   }
}
