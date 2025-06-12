package ai.planner;

import ai.player.Darkboard;
import core.Metaposition;
import core.Move;
import java.util.Vector;

public class PlanDashboard {
   Vector plans = new Vector();
   Vector planners = new Vector();
   public Darkboard owner;

   public PlanDashboard(Darkboard d) {
      this.owner = d;
   }

   public void addPlan(Plan p) {
      this.plans.add(p);
      p.isAdded();
   }

   public int getPlanNumber() {
      return this.plans.size();
   }

   public Plan getPlan(int k) {
      return (Plan)this.plans.get(k);
   }

   public void deletePlan(Plan p) {
      this.plans.remove(p);
   }

   public void addPlan(Planner p) {
      this.planners.add(p);
   }

   public int getPlannerNumber() {
      return this.planners.size();
   }

   public Planner getPlanner(int k) {
      return (Planner)this.planners.get(k);
   }

   public void deletePlanner(Planner p) {
      this.planners.remove(p);
   }

   public void evolveAfterMove(Metaposition root, Metaposition ev, Move m, int cap, int capx, int capy, int check1, int check2, int tries) {
      int j;
      for(j = 0; j < this.getPlannerNumber(); ++j) {
         this.getPlanner(j).evolveAfterMove(root, ev, m, cap, capx, capy, check1, check2, tries);
      }

      for(j = 0; j < this.getPlanNumber(); ++j) {
         Plan p = this.getPlan(j);
         ++p.age;
         --p.ttl;
         p.modifier *= p.falloff;
         if (p.ttl != 0 && p.active) {
            this.getPlan(j).evolveAfterMove(root, ev, m, cap, capx, capy, check1, check2, tries);
         } else {
            this.plans.remove(p);
            --j;
         }
      }

   }

   public void evolveAfterOpponentMove(Metaposition root, Metaposition ev, int capx, int capy, int check1, int check2, int tries) {
      int k;
      for(k = 0; k < this.getPlannerNumber(); ++k) {
         this.getPlanner(k).evolveAfterOpponentMove(root, ev, capx, capy, check1, check2, tries);
      }

      for(k = 0; k < this.getPlanNumber(); ++k) {
         if (this.getPlan(k).active) {
            this.getPlan(k).evolveAfterOpponentMove(root, ev, capx, capy, check1, check2, tries);
         }
      }

   }

   public void evolveAfterIllegalMove(Metaposition root, Metaposition ev, Move m) {
      int k;
      for(k = 0; k < this.getPlannerNumber(); ++k) {
         this.getPlanner(k).evolveAfterIllegalMove(root, ev, m);
      }

      for(k = 0; k < this.getPlanNumber(); ++k) {
         if (this.getPlan(k).active) {
            this.getPlan(k).evolveAfterIllegalMove(root, ev, m);
         }
      }

   }

   public Move findPlanExecutingMove(Vector moves, Metaposition start, float threshold) {
      Move best = null;
      int bestPriority = -1;
      float bestMatch = 0.0F;

      for(int k = 0; k < moves.size(); ++k) {
         Move m = (Move)moves.get(k);

         for(int j = 0; j < this.getPlanNumber(); ++j) {
            Plan p = this.getPlan(j);
            if (p.active) {
               float match = p.isApplicable(start, m) * p.modifier;
               if (match > threshold && (p.priority > bestPriority || p.priority == bestPriority && match > bestMatch)) {
                  best = m;
                  bestPriority = p.priority;
                  bestMatch = match;
               }
            }
         }
      }

      return best;
   }
}
