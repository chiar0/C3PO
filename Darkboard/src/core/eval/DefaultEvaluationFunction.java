package core.eval;

import ai.player.Darkboard;
import core.EvaluationFunction;
import core.eval.components.CheckmateProgressComponent;
import core.eval.components.DefaultPositionComponent;
import core.eval.components.ExtendedMaterialComponent;

public class DefaultEvaluationFunction extends EvaluationFunction {
   public DefaultEvaluationFunction(Darkboard d) {
      this.addComponent(new ExtendedMaterialComponent());
      this.addComponent(new DefaultPositionComponent());
      this.addComponent(new CheckmateProgressComponent());
   }

   public String getName() {
      return "Default Evaluation Function";
   }

   public boolean useLoopDetector() {
      return true;
   }
}
