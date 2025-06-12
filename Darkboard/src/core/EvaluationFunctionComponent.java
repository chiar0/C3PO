package core;

import java.util.Vector;

public class EvaluationFunctionComponent {
   protected String name;

   public float evaluate(Metaposition start, Metaposition dest, Move m, Vector history) {
      return 0.0F;
   }

   public boolean generatesCustomReport() {
      return false;
   }
}
