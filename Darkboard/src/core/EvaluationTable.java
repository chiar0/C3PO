package core;

import ai.player.Darkboard;

public class EvaluationTable {
   public Darkboard.MoveEvaluation[][][] moves = new Darkboard.MoveEvaluation[64][64][6];

   public void clear() {
      for(int k = 0; k < 64; ++k) {
         for(int j = 0; j < 64; ++j) {
            for(int p = 0; p < 6; ++p) {
               this.moves[k][j][p] = null;
            }
         }
      }

   }
}
