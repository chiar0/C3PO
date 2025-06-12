package ai.mc.strategy;

import ai.mc.MCLink;
import ai.mc.MCLowLevelStrategy;
import ai.mc.MCState;
import core.Move;

public class MCStrikeSquareStrategy extends MCLowLevelStrategy {
   public MCStrikeSquareStrategy(int x, int y) {
      this.parameters[0] = x;
      this.parameters[1] = y;
      this.name = "X";
   }

   public double[] progressInformation(MCState m, MCLink[] moves) {
      double[] out = new double[moves.length];

      for(int k = 0; k < out.length; ++k) {
         Move mv = moves[k].move;
         out[k] = mv.toX != this.parameters[0] || mv.toY != this.parameters[1] || mv.piece == 0 && mv.toX == mv.fromX ? -100.0D : 1.0D;
      }

      return out;
   }
}
