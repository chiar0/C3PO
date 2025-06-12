package core.algorithm;

import ai.player.Darkboard;
import core.EvaluationFunction;
import core.EvaluationTree;
import core.Move;

public class EvaluationTableAlgorithm extends SelectionAlgorithm {
   Darkboard p;
   int maxPositions = 5000;

   public Move select(Darkboard pl, EvaluationTree et) {
      this.p = pl;
      pl.globals.evaluatedPositions = 0;
      pl.globals.evaluationTable.clear();
      pl.globals.evaluationTableSize = 0;
      pl.globals.currentEvaluator = this.p.isWhite ? pl.globals.whiteEvaluator : pl.globals.blackEvaluator;
      this.p.simplifiedBoard.setAge((byte)0);
      EvaluationFunction ef = this.p.findAppropriateFunction(this.p.simplifiedBoard);
      this.maxPositions = (int)((float)this.maxPositions * ef.getNodeMultFactor(this.p.simplifiedBoard));
      if (pl.globals.OUTPUT_MOVE_TREE && pl.globals.moveTree != null) {
         System.out.println(pl.globals.moveTree.getBestSequence());
      }

      if (pl.globals.OUTPUT_MOVE_TREE && pl.globals.SAVE_MOVE_TREE_TO_DISK && pl.globals.moveTree != null) {
         pl.globals.moveTree.saveToFile();
      }

      pl.globals.bestMoveValue = this.p.minimaxPositionValue;
      return this.p.minimaxBestMove;
   }
}
