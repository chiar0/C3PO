package ai.evolve;

import java.io.Serializable;
import java.util.Random;

public class SimpleWeightSet implements Serializable {
   private static final Random r = new Random();
   public static final int WEIGHT_NUMBER = 2048;
   public float[] weights = new float[2048];
   public static final int W_PAWN_V = 0;
   public static final int W_KNIGHT_V = 1;
   public static final int W_BISHOP_V = 2;
   public static final int W_ROOK_V = 3;
   public static final int W_QUEEN_V = 4;
   public static final int W_PAWN_I = 5;
   public static final int W_KNIGHT_I = 6;
   public static final int W_BISHOP_I = 7;
   public static final int W_ROOK_I = 8;
   public static final int W_QUEEN_I = 9;
   public static final int W_PASSED_PAWN_I = 10;
   public static final int W_ADJACENT_PAWN_I = 11;
   public static final int W_ADVANCEMENT_PAWN_I = 12;
   public static final int W_MAX_MOVES_KNIGHT_I = 13;
   public static final int W_CENTRAL_BISHOP_I = 14;
   public static final int W_CENTRAL_ROOK_I = 15;
   public static final int W_CENTRAL_QUEEN_I = 16;
   public static final int W_RISK_MODIFIER_I = 17;
   public static final int W_PAWN_TRY_MODIFIER = 18;
   public static final int W_MOBILITY_MODIFIER = 19;
   public static final int MAX_INDEX = 20;

   public SimpleWeightSet() {
      this.weights[0] = 1.6F;
      this.weights[1] = 2.8F;
      this.weights[2] = 3.0F;
      this.weights[3] = 4.0F;
      this.weights[4] = 9.0F;
      this.weights[5] = 1.0F;
      this.weights[6] = 1.0F;
      this.weights[7] = 1.0F;
      this.weights[8] = 1.0F;
      this.weights[9] = 1.0F;
      this.weights[10] = 1.4F;
      this.weights[11] = 1.2F;
      this.weights[12] = 1.15F;
      this.weights[13] = 1.2F;
      this.weights[14] = 1.01F;
      this.weights[15] = 1.01F;
      this.weights[16] = 1.01F;
      this.weights[17] = 0.85F;
      this.weights[18] = 500.0F;
      this.weights[19] = 0.025F;
   }

   public SimpleWeightSet evolve(float percentage, float amount) {
      SimpleWeightSet s = new SimpleWeightSet();
      System.arraycopy(this.weights, 0, s.weights, 0, 19);

      for(int k = 0; k < 20; ++k) {
         if (r.nextFloat() <= percentage) {
            float actualAmount = amount * (float)r.nextInt(1000) / 1000.0F;
            ++actualAmount;
            if (r.nextBoolean()) {
               actualAmount = 1.0F / actualAmount;
            }

            float[] var10000 = s.weights;
            var10000[k] *= actualAmount;
         }
      }

      return s;
   }

   public void blend(SimpleWeightSet s, float intensity) {
      if (!(intensity < 0.0F) && !(intensity > 1.0F)) {
         for(int k = 0; k < 20; ++k) {
            this.weights[k] = s.weights[k] * intensity + this.weights[k] * (1.0F - intensity);
         }

      }
   }

   public String toString() {
      String result = "";

      for(int k = 0; k < 20; ++k) {
         result = result + this.weights[k] + "\n";
      }

      return result;
   }
}
