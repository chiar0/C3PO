package ai.mc.strategy;

import ai.mc.MCLink;
import ai.mc.MCLowLevelStrategy;
import ai.mc.MCState;
import ai.mc.MCStateNode;
import core.Move;
import core.uberposition.UPUtilities;
import core.uberposition.Uberposition;

public class MCThreatenSquareStrategy extends MCLowLevelStrategy {
   public MCThreatenSquareStrategy(int x, int y) {
      this.parameters[0] = x;
      this.parameters[1] = y;
      this.name = "TS";
   }

   public double[] progressInformation(MCState m, MCLink[] moves) {
      Uberposition[] u = new Uberposition[moves.length];
      Move[] mv = new Move[moves.length];

      for(int k = 0; k < u.length; ++k) {
         MCStateNode mcs = (MCStateNode)moves[k].child;
         u[k] = mcs != null ? (Uberposition)mcs.state : null;
         mv[k] = moves[k].move;
      }

      return UPUtilities.progressTowardsThreateningSquare((Uberposition)m, mv, u, this.parameters[0], this.parameters[1]);
   }
}
