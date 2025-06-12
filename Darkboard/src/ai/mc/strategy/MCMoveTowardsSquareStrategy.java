package ai.mc.strategy;

import ai.mc.MCHighLevelStrategy;
import ai.mc.MCStrategy;

public class MCMoveTowardsSquareStrategy extends MCHighLevelStrategy {
   public MCMoveTowardsSquareStrategy(int x, int y) {
      super("MV", x, y, 0, 0);
   }

   protected void init() {
      MCStrategy[] st = new MCStrategy[]{new MCThreatenSquareStrategy(this.parameters[0], this.parameters[1]), new MCStrikeSquareStrategy(this.parameters[0], this.parameters[1])};
      this.messages = st;
      this.transitions = new int[2][2];
      this.transitions[0][0] = 0;
      this.transitions[0][1] = 1;
      this.transitions[1][0] = -1;
      this.transitions[1][1] = -1;
      this.continuations = new MCStrategy[2][];
      this.continuations[0] = st;
      this.continuations[1] = new MCStrategy[1];
      this.continuations[1][0] = null;
      this.stratStrings = new String[this.messages.length];

      for(int k = 0; k < this.stratStrings.length; ++k) {
         this.stratStrings[k] = this.messages[k].toString();
      }

   }
}
