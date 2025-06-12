package database;

import core.Move;
import pgn.ExtendedPGNGame;

public class Opening extends MoveDatabase {
   boolean white;

   public Opening(boolean w) {
      this.white = w;
   }

   public void extractFromGame(ExtendedPGNGame game) {
      boolean finished = false;
      int k = 0;

      while(!finished) {
         ExtendedPGNGame.PGNMoveData data1 = game.getMove(true, k);
         ExtendedPGNGame.PGNMoveData data2 = game.getMove(false, k);
         ++k;
         ExtendedPGNGame.PGNMoveData data = this.white ? data1 : data2;
         if (data == null || data.capturewhat != 0 || data1 != null && !this.white && (data1.capturewhat != 0 || data1.check1 != 0)) {
            break;
         }

         if (data != null) {
            this.addMove(data.finalMove);
         } else {
            finished = true;
         }

         if (data1 != null && (data1.capturewhat != 0 || data1.check1 != 0) || data2 != null && (data2.capturewhat != 0 || data2.check1 != 0)) {
            break;
         }
      }

   }

   boolean isOpeningSubset(Opening o, boolean subset) {
      if (this.isWhite() != o.isWhite()) {
         return false;
      } else {
         for(int k = 0; k < this.getMoveNumber(); ++k) {
            Move m1 = this.getMove(k);
            Move m2 = o.getMove(k);
            if (m1 == null || m2 == null || !m1.equals(m2)) {
               return false;
            }
         }

         if (subset) {
            if (o.getMoveNumber() < this.getMoveNumber()) {
               return true;
            } else {
               return false;
            }
         } else if (o.getMoveNumber() >= this.getMoveNumber()) {
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean isWhite() {
      return this.white;
   }

   public void setWhite(boolean b) {
      this.white = b;
   }
}
