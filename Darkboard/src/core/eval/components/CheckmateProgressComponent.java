package core.eval.components;

import core.EvaluationFunctionComponent;
import core.Metaposition;
import core.Move;
import java.util.Vector;

public class CheckmateProgressComponent extends EvaluationFunctionComponent {
   public CheckmateProgressComponent() {
      this.name = "Progress";
   }

   public float evaluate(Metaposition start, Metaposition dest, Move m, Vector history) {
      float result = m != null ? dest.computeEnemyKingBonus(start, dest, m) : 0.0F;
      return result;
   }
}
