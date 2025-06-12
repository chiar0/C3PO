package core.eval.components;

import core.EvaluationFunction;
import core.EvaluationFunctionComponent;
import core.EvaluationGlobals;
import core.Metaposition;
import core.Move;
import core.eval.RiskManager;
import java.util.Vector;

public class ExtendedMaterialComponent extends EvaluationFunctionComponent {
   public ExtendedMaterialComponent() {
      this.name = "Extended Material";
   }

   public float evaluate(Metaposition start, Metaposition dest, Move m, Vector history) {
      float totalvalue = 0.0F;
      float materialvalue = 0.0F;
      float result = 0.0F;
      float value = 0.0F;
      boolean isExtendedTree = EvaluationFunction.currentNode != null;

      for(int k = 0; k < 64; ++k) {
         if ((dest.squares[k] & 128) != 0) {
            int piece = dest.squares[k] & 127;
            switch(piece) {
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
            double averageDanger = 1.0D;
            double varianceDanger = 1.0D;
            float danger = dest.dangerRating(k / 8, k % 8);
            if (piece >= 0 && start.owner.globals.totalMaterial >= 0 && dest.pawnsLeft + dest.piecesLeft >= 0) {
               averageDanger = RiskManager.array[piece][start.owner.globals.totalMaterial][dest.pawnsLeft + dest.piecesLeft][0];
               varianceDanger = RiskManager.array[piece][start.owner.globals.totalMaterial][dest.pawnsLeft + dest.piecesLeft][1];
               if (varianceDanger < 0.1D) {
                  varianceDanger = 0.1D;
               }
            }

            double modifier = (double)danger <= averageDanger ? 0.0D : 0.02D * ((double)danger - averageDanger) / varianceDanger;
            materialvalue = (float)((double)materialvalue + (double)value * ((double)dest.pieceSafety(k / 8, k % 8, moved, moved && m != null && m.capture) - modifier));
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
