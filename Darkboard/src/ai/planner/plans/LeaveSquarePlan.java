package ai.planner.plans;

import ai.planner.Plan;
import ai.player.Darkboard;
import core.Metaposition;
import core.Move;
import java.util.Vector;

public class LeaveSquarePlan extends Plan {
   int sx;
   int sy;

   public LeaveSquarePlan(Darkboard d, int x, int y) {
      super(d);
      this.sx = x;
      this.sy = y;
   }

   protected float fitness(Metaposition start, Metaposition end, Move m, Vector history) {
      float result = 0.01F;
      description = "LS -> ";
      if (history.size() == 0) {
         result = -0.05F;
      }

      int k;
      for(k = 0; k < history.size(); ++k) {
         description = description + history.get(k) + " ";
      }

      for(k = 0; k < history.size(); ++k) {
         Move move = (Move)history.get(k);
         if (move.fromX == this.sx && move.fromY == this.sy) {
            break;
         }

         result -= 3.0F;
      }

      description = description + result;
      return result;
   }

   public String toString() {
      return "Leave square " + Move.squareString(this.sx, this.sy);
   }

   public int getSx() {
      return this.sx;
   }

   public void setSx(int sx) {
      this.sx = sx;
   }

   public int getSy() {
      return this.sy;
   }

   public void setSy(int sy) {
      this.sy = sy;
   }
}
