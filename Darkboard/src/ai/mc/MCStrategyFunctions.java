package ai.mc;

import ai.mc.strategy.MCMoveTowardsSquareStrategy;
import ai.mc.strategy.MCNullPlan;
import core.uberposition.Uberposition;
import java.util.Vector;

public class MCStrategyFunctions {
   public static void initializeHighestLevelFunctions(MCStrategyNode firstRoot, MCState state) {
      Uberposition u = (Uberposition)state;
      Vector<MCStrategy> v = new Vector();
      int empt = 0;

      for(int k = 0; k < 8; ++k) {
         for(int j = 0; j < 8; ++j) {
            if (u.allied[k][j] == 6) {
               ++empt;
            }
         }
      }

      double avg = 1.0D * ((double)(u.pawnsLeft + u.piecesLeft) + 1.0D) / (double)empt;
      double avg2 = 1.0D - avg - 0.1D;
      boolean cont = true;

      int k;
      for(k = 0; k < 8; ++k) {
         for(int j = 0; j < 8; ++j) {
            if (cont && u.allied[k][j] == 6 && (double)u.empty[k][j] < avg2) {
               v.add(new MCMoveTowardsSquareStrategy(k, j));
            }
         }
      }

      v.add(new MCNullPlan());
      firstRoot.links = new MCLink[v.size()];

      for(k = 0; k < v.size(); ++k) {
         MCStrategy s = (MCStrategy)v.get(k);
         firstRoot.links[k] = new MCLink(firstRoot);
         firstRoot.links[k].setChild(new MCStrategyNode(s, (MCStrategyNode)null, (MCStrategyNode)null));
      }

   }
}
