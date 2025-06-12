package ai.player;

import core.Move;
import java.util.Random;
import java.util.Vector;
import umpire.local.LocalUmpire;

public class RandomMovingPlayer extends Player {
   Random r;
   boolean w;
   int capx;
   int capy;
   int check1;
   int check2;
   int tries;

   public RandomMovingPlayer(boolean isWhite) {
      this.w = isWhite;
      this.r = new Random();
   }

   public Move getNextMove() {
      Vector<Move> v = ((LocalUmpire)this.currentUmpire).legalMoves(this.w);
      Vector<Move> v2 = new Vector();

      for(int k = 0; k < v.size(); ++k) {
         Move m = (Move)v.get(k);
         if (m.piece == 0 && m.toX != m.fromX || m.toX == this.capx && m.toY == this.capy) {
            v2.add(m);
         }
      }

      if (v2.size() == 0) {
         this.lastMove = (Move)v.get(this.r.nextInt(v.size()));
      } else {
         this.lastMove = (Move)v2.get(this.r.nextInt(v2.size()));
      }

      return this.lastMove;
   }

   public String communicateUmpireMessage(int capX, int capY, int tries, int check, int check2, int captureType) {
      super.communicateUmpireMessage(capX, capY, tries, check, check2, captureType);
      this.capx = capX;
      this.capy = capY;
      this.tries = tries;
      this.check1 = check;
      this.check2 = check2;
      return "";
   }
}
