package ai.player;

import core.Metaposition;
import core.Move;

public class HumanPlayer extends Player {
   Move nextMove = null;
   int setPromotionPiece = 4;
   boolean hasTurn;

   public HumanPlayer(boolean white) {
      this.isWhite = white;
      this.hasTurn = white;
      this.playerName = "Human";
      this.simplifiedBoard = Metaposition.getChessboard((Player)this);
      this.simplifiedBoard.setup(this.isWhite);
   }

   public void setHasTurn(boolean b) {
      this.hasTurn = b;
   }

   public Move getNextMove() {
      while(this.nextMove == null) {
         try {
            this.wait();
         } catch (Exception var2) {
         }
      }

      Move m = this.nextMove;
      this.lastMove = m;
      this.nextMove = null;
      this.hasTurn = false;
      return m;
   }

   public synchronized void provideMove(Move m) {
      if (this.hasTurn) {
         this.nextMove = m;
         if (m.piece == 0 && (m.toY == 0 || m.toY == 7)) {
            m.promotionPiece = (byte)this.setPromotionPiece;
         }

         this.notifyAll();
      }
   }

   public void emulateNextMove(Move m) {
      this.lastMove = m;
   }

   public void setPromotionPiece(int what) {
      this.setPromotionPiece = what;
   }

   public boolean isHuman() {
      return true;
   }

   public void communicateIllegalMove(Move m) {
      super.communicateIllegalMove(m);
      this.hasTurn = true;
   }

   public String communicateUmpireMessage(int capX, int capY, int tries, int check, int check2, int captureType) {
      this.hasTurn = true;
      return super.communicateUmpireMessage(capX, capY, tries, check, check2, captureType);
   }

   public String communicateLegalMove(int capture, int oppTries, int oppCheck, int oppCheck2) {
      return super.communicateLegalMove(capture, oppTries, oppCheck, oppCheck2);
   }
}
