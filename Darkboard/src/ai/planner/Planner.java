package ai.planner;

import ai.player.Darkboard;
import core.Metaposition;
import core.Move;

public class Planner {
   protected PlanDashboard dashboard;
   public Darkboard owner;

   public Planner(PlanDashboard d) {
      this.init(d);
   }

   protected void init(PlanDashboard d) {
      this.dashboard = d;
      this.owner = d.owner;
   }

   public void evolveAfterMove(Metaposition root, Metaposition ev, Move m, int cap, int capx, int capy, int check1, int check2, int tries) {
   }

   public void evolveAfterOpponentMove(Metaposition root, Metaposition ev, int capx, int capy, int check1, int check2, int tries) {
   }

   public void evolveAfterIllegalMove(Metaposition root, Metaposition ev, Move m) {
   }
}
