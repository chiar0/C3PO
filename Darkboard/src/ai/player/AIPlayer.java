package ai.player;

import core.Metaposition;
import core.Move;
import java.util.Vector;

public class AIPlayer extends Player {
   public static final int MESSAGE_TYPE_MOVE = 0;
   public static final int MESSAGE_TYPE_INFO = 1;
   public static final int MESSAGE_TYPE_ERROR = 2;

   public float evaluate(Metaposition start, Move m, Metaposition dest, Vector history) {
      return 0.0F;
   }

   public boolean usesGlobals() {
      return true;
   }
}
