package ai.mc;

import core.Move;
import core.uberposition.Uberposition;
import java.util.Random;

public class MCLink {
   public static final int ALT_ILLEGAL = 0;
   public static final int ALT_CAPTURE = 1;
   public static final int ALT_PAWN = 2;
   public static final int ALT_CHECK = 3;
   protected static Random r = new Random();
   public MCNode child;
   public Move move;
   MCNode parent;
   double heuristicBias = 0.0D;
   double silentChance = 1.0D;
   double[] altChances = null;
   double[] altValues = null;
   public double theRestValue = 0.0D;

   public MCLink(MCNode p) {
      this.parent = p;
   }

   public void setChild(MCNode ch) {
      this.child = ch;
      this.child.parent = this.parent;
      this.child.parentLink = this;
   }

   public void setMove(Move m) {
      this.move = m;
   }

   public boolean refined() {
      return this.altChances != null;
   }

   public void refine() {
      Uberposition s = (Uberposition)((MCStateNode)this.parent).state;
      double legal = (double)s.chanceOfLegality(this.move);
      double pawnTries = (double)s.chanceOfPawnTries(this.move);
      double cap = (double)s.chanceOfCapture(this.move);
      double check = (double)s.chanceOfCheck(this.move);
      this.silentChance = legal * (1.0D - cap) * (1.0D - pawnTries);
      this.heuristicBias = 0.0D;
      this.altChances = new double[4];
      this.altValues = new double[4];
      if (this.silentChance == 1.0D) {
         this.theRestValue = 0.0D;
      } else {
         int attackPower = s.attackPower(this.move.toX, this.move.toY);
         this.altChances[0] = 1.0D - legal;
         this.altChances[1] = legal * cap;
         this.altChances[2] = legal * pawnTries;
         this.altChances[3] = check;
         this.altValues[0] = this.parent.value - 0.01D;
         this.altValues[2] = this.move.piece == 0 && attackPower >= 1 ? this.parent.value - 0.01D : -1.0D;
         this.altValues[1] = this.parent.value + 50.0D * cap * cap / ((double)(s.pawnsLeft + s.piecesLeft) + 1.0D) * ((double)s.evaluateWarSaga(this.move) - 0.1D);
         if (this.altValues[1] > 1.0D) {
            this.altValues[1] = 1.0D;
         }

         this.altValues[3] = attackPower < 1 ? -1.0D : this.parent.value + 0.01D;
         this.theRestValue = 0.0D;

         for(int k = 0; k < 4; ++k) {
            this.theRestValue += this.altValues[k] * this.altChances[k];
         }

         this.theRestValue /= this.altChances[0] + this.altChances[1] + this.altChances[2] + this.altChances[3];
         if (this.theRestValue > 1.0D || this.theRestValue < -1.0D) {
            ;
         }
      }
   }

   public boolean somethingElseHappens() {
      if (this.altChances == null) {
         return false;
      } else {
         return r.nextDouble() > this.silentChance;
      }
   }
}
