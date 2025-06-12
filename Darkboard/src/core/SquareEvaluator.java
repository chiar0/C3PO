package core;

public class SquareEvaluator {
   public float[][] opponentSquareValues = new float[128][64];

   public SquareEvaluator() {
   }

   public SquareEvaluator(boolean white) {
      float[] pawns = new float[64];
      float[] knights = new float[64];
      float[] bishops = new float[64];
      float[] rooks = new float[64];
      float[] queens = new float[64];
      float[] kings = new float[64];
      float[] empty = new float[64];
      float[] pawnValues = new float[]{-0.0F, -2.0F, -1.0F, -0.25F, -0.1F, -0.05F, -0.02F, -0.0F};

      for(int k = 0; k < 8; ++k) {
         for(int j = 0; j < 8; ++j) {
            int index = k * 8 + j;
            pawns[index] = white ? pawnValues[j] : pawnValues[7 - j];
            knights[index] = -0.01F;
            bishops[index] = -0.01F;
            rooks[index] = -0.02F;
            queens[index] = -0.025F;
            kings[index] = -0.005F;
            empty[index] = 0.0F;
         }
      }

      this.composeFromValues(pawns, knights, bishops, rooks, queens, kings, empty);
   }

   public void composeFromValues(float[] pawns, float[] knights, float[] bishops, float[] rooks, float[] queens, float[] kings, float[] empty) {
      float[][] array = new float[][]{pawns, knights, bishops, rooks, queens, kings, empty};

      for(int k = 0; k < 128; ++k) {
         for(int piece = 0; piece < 7; ++piece) {
            if ((k & 1 << piece) != 0) {
               for(int j = 0; j < 64; ++j) {
                  float[] var10000 = this.opponentSquareValues[k];
                  var10000[j] += array[piece][j];
               }
            }
         }
      }

   }
}
