package ai.planner;

import ai.chunk.regboard.RegBoard;
import core.Metaposition;
import java.util.Vector;

public class RegBoardArea implements Area {
   RegBoard board;
   Vector<int[]> activeSquares;

   public RegBoardArea(RegBoard rba) {
      this.board = rba;

      for(int k = 0; k < rba.getMoveNumber(); ++k) {
         String[] s = rba.getMove(k);
         int[] sq = new int[]{Integer.parseInt(s[0]), Integer.parseInt(s[1])};
         this.activeSquares.add(sq);
      }

   }

   public Vector<int[]> getAreaSquares(Metaposition m) {
      Vector<int[]> result = new Vector();
      RegBoard.RegBoardPOV pov = this.board.new RegBoardPOV(false, false, false, false, false);
      int[] offset = this.board.match2(m, pov);
      if (offset[0] < 0) {
         return result;
      } else {
         for(int k = 0; k < this.activeSquares.size(); ++k) {
            int[] sq = (int[])this.activeSquares.get(k);
            int x = pov.mirrorH ? this.board.getWidth(pov) - sq[0] - 1 + offset[0] : sq[0] + offset[0];
            int y = pov.mirrorV ? this.board.getHeight(pov) - sq[1] - 1 + offset[1] : sq[1] + offset[1];
            int[] add = new int[]{x, y};
            if (x >= 0 && y >= 0 && x < 8 && y < 8) {
               result.add(add);
            }
         }

         return result;
      }
   }

   public int getSquareNumber(Metaposition m) {
      return this.activeSquares.size();
   }

   public boolean isIncluded(int x, int y, Metaposition m) {
      return false;
   }
}
