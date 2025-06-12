package core.eval.components;

import core.EvaluationFunction;
import core.EvaluationFunctionComponent;
import core.EvaluationGlobals;
import core.Metaposition;
import core.Move;
import java.util.Vector;

public class DefaultMaterialComponent extends EvaluationFunctionComponent {
   public DefaultMaterialComponent() {
      this.name = "Material";
   }

   public float evaluate(Metaposition start, Metaposition dest, Move m, Vector history) {
      float totalvalue = 0.0F;
      float materialvalue = 0.0F;
      float result = 0.0F;
      float value = 0.0F;
      boolean isExtendedTree = EvaluationFunction.currentNode != null;

      for(int k = 0; k < 64; ++k) {
         if ((dest.squares[k] & 128) != 0) {
            switch(dest.squares[k] & 127) {
            case 0:
               value = start.owner.globals.pieceValues[0];
               result += 0.0F * (float)(dest.isWhite() ? k % 8 : 7 - k % 8) * start.owner.globals.pieceValues[0] / 5.0F;
               break;
            case 1:
               value = start.owner.globals.pieceValues[1];
               break;
            case 2:
               value = start.owner.globals.pieceValues[2];
               break;
            case 3:
               value = start.owner.globals.pieceValues[3];
               break;
            case 4:
               value = start.owner.globals.pieceValues[4];
               result += 10.0F;
               break;
            case 5:
               value = 5.0F * (float)(dest.piecesLeft + dest.pawnsLeft) / 15.0F;
            }

            boolean moved = EvaluationFunction.index == k;
            totalvalue += value;
            materialvalue += value * dest.pieceSafety(k / 8, k % 8, moved, moved && m != null && m.capture);
         } else {
            result += start.owner.globals.currentEvaluator.opponentSquareValues[dest.squares[k]][k] * EvaluationGlobals.dangerFunction[dest.ageMatrix[k]];
         }
      }

      result += materialvalue;
      result = (float)((double)result - (double)dest.pawnsLeft * 2.0D);
      result = (float)((double)result - (double)dest.piecesLeft * 5.0D);
      result += 1.0F * (float)start.owner.globals.materialDelta;
      return result;
   }
}
