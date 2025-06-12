package core.eval.components;

import core.EvaluationFunction;
import core.EvaluationFunctionComponent;
import core.EvaluationGlobals;
import core.Globals;
import core.Metaposition;
import core.Move;
import java.util.Vector;

public class DefaultPositionComponent extends EvaluationFunctionComponent {
   public DefaultPositionComponent() {
      this.name = "Position";
   }

   public float evaluate(Metaposition start, Metaposition dest, Move m, Vector history) {
      float totalvalue = 0.0F;
      float materialvalue = 0.0F;
      float result = 0.0F;
      float value = 0.0F;
      boolean isExtendedTree = EvaluationFunction.currentNode != null;
      int mob = 0;

      int y;
      int k;
      for(y = 7; y >= 0; --y) {
         for(k = 7; k >= 0; --k) {
            if (start.owner.globals.protectionMatrix[y][k] > 0) {
               ++mob;
            }
         }
      }

      result += EvaluationGlobals.weights.weights[19] * (float)mob;
      if (start.owner.globals.totalMaterial < 4) {
         result = (float)((double)result + (double)dest.kingProximityBonus(start.owner.globals.kingLocationX, start.owner.globals.kingLocationY) * 0.1D);
      }

      for(y = 0; y < 8; ++y) {
         if (start.owner.globals.pawnSquaresInFiles[y] == 0) {
            ++result;
         }
      }

      if (start.owner.experimental && Globals.usePools) {
         if (m.piece != 0) {
            double protDelta = (double)start.owner.globals.protectionMatrix[m.toX][m.toY] - 2.0D * start.owner.globals.opponentProtectionData[m.toX][m.toY];
            result = (float)((double)result + (1.0D - start.owner.globals.opponentDensityData[m.toX][m.toY][6]) * 4.0D * protDelta);
         }

         y = start.isWhite() ? m.toY + 1 : m.toY - 1;
         if (y >= 0 && y <= 7) {
            for(k = 0; k < 2; ++k) {
               int x = k == 0 ? m.toX - 1 : m.toX + 1;
               if (x >= 0 && x <= 7) {
                  result = (float)((double)result - start.owner.globals.opponentDensityData[x][y][0] * 10.0D);
               }
            }
         }
      }

      return result;
   }
}
