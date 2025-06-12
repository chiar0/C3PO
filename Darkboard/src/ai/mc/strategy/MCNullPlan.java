package ai.mc.strategy;

import ai.mc.MCLink;
import ai.mc.MCState;
import ai.mc.MCStrategy;

public class MCNullPlan extends MCStrategy {
   public static MCNullPlan pl = new MCNullPlan();

   public MCStrategy[] continuations(MCStrategy[] sofar) {
      MCStrategy[] out = new MCStrategy[]{pl};
      return out;
   }

   public double[] progressInformation(MCState m, MCLink[] moves) {
      return new double[moves.length];
   }

   public String toString() {
      return "NULL";
   }
}
