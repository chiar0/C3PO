package core.eval.components;

import ai.planner.Plan;
import ai.planner.PlanDashboard;
import core.EvaluationFunction;
import core.EvaluationFunctionComponent;
import core.Metaposition;
import core.Move;
import java.util.Vector;

public class PlanExecutionComponent extends EvaluationFunctionComponent {
   PlanDashboard board;

   public PlanExecutionComponent(PlanDashboard d) {
      this.board = d;
      this.name = "Plan Execution";
   }

   public float evaluate(Metaposition start, Metaposition dest, Move m, Vector history) {
      float result = 0.0F;
      if (this.board == null) {
         return result;
      } else {
         int p = this.board.getPlanNumber();

         for(int k = 0; k < p; ++k) {
            Plan pl = this.board.getPlan(k);
            if (pl.active) {
               result += pl.evaluate(start, dest, m, history);
            }

            if (this.generatesCustomReport() && EvaluationFunction.currentNode != null) {
               EvaluationFunction.currentNode.addInformation(Plan.description);
            }
         }

         return result;
      }
   }

   public boolean generatesCustomReport() {
      return true;
   }
}
