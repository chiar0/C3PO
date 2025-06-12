package core.eval;

import core.EvaluationFunctionComponent;
import core.Metaposition;
import core.Move;
import java.util.Vector;

public class KPKMainComponent extends EvaluationFunctionComponent {
   static int[] pawnLocs = new int[8];

   public float evaluate(Metaposition start, Metaposition dest, Move m, Vector history) {
      dest.computeProtectionMatrix(true);
      dest.getSquaresWithPiece(0, pawnLocs);
      float result = 0.0F;
      int q;
      if (pawnLocs[0] == -1) {
         q = dest.getSquareWithPiece(4);
         if (q == -1) {
            return 0.0F;
         } else {
            return start.owner.globals.protectionMatrix[q / 8][q % 8] < 1 ? -120.0F : 120.0F;
         }
      } else {
         q = dest.getSquareWithPiece(5);
         int kx = q / 8;
         int ky = q % 8;
         int minDist = 8;

         for(int k = 0; k < 8 && pawnLocs[k] != -1; ++k) {
            int px = pawnLocs[k] / 8;
            int py = pawnLocs[k] % 8;
            int dx = px > kx ? px - kx : kx - px;
            int dy = py > ky ? py - ky : ky - py;
            int d = dx > dy ? dx : dy;
            if (d < minDist) {
               minDist = d;
            }

            if (start.owner.globals.protectionMatrix[px][py] < 1) {
               result += (float)(dest.isWhite() ? py : 7 - py);
            }
         }

         result += 5.0F * (8.0F - (float)minDist);
         result += (float)(dest.isWhite() ? ky : 7 - ky);
         return result;
      }
   }
}
