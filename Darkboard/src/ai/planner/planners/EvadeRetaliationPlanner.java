package ai.planner.planners;

import ai.planner.Plan;
import ai.planner.PlanDashboard;
import ai.planner.Planner;
import ai.planner.plans.LeaveSquarePlan;
import core.Metaposition;
import core.Move;

public class EvadeRetaliationPlanner extends Planner {
   public EvadeRetaliationPlanner(PlanDashboard d) {
      super(d);
   }

   public void evolveAfterMove(Metaposition root, Metaposition ev, Move m, int cap, int capx, int capy, int check1, int check2, int tries) {
      if (cap != 0) {
         LeaveSquarePlan lsp = new LeaveSquarePlan(this.owner, m.toX, m.toY);
         lsp.ttl = 5;
         this.dashboard.addPlan((Plan)lsp);
      }

   }

   public void evolveAfterOpponentMove(Metaposition root, Metaposition ev, int capx, int capy, int check1, int check2, int tries) {
      try {
         for(int k = 0; k < this.dashboard.getPlanNumber(); ++k) {
            if (this.dashboard.getPlan(k).getClass().equals(Class.forName("ai.planner.plans.LeaveSquarePlan"))) {
               LeaveSquarePlan lsp = (LeaveSquarePlan)this.dashboard.getPlan(k);
               if (lsp.getSx() == capx && lsp.getSy() == capy) {
                  this.dashboard.deletePlan(lsp);
                  --k;
               }
            }
         }
      } catch (ClassNotFoundException var10) {
         var10.printStackTrace();
      }

   }
}
