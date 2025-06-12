package agent;

import game.Game;
import java.util.Iterator;
import java.util.List;
import main.collections.FastArrayList;
import other.AI;
import other.context.Context;
import other.move.Move;
import other.state.container.ContainerState;

public class DarkboardAgent extends AI {
   protected int player = -1;
   protected int opponent = -1;
   protected int players = 2;
   protected boolean firstMove;
   protected int promotionPiece;
   protected int lastFrom = -1;
   protected ContainerState previousBoardState = null;
   protected Darkboard agent;

   public DarkboardAgent() {
      this.friendlyName = "Darkboard";
   }

   public boolean aiPlayerWhite() {
      return this.player == 1;
   }

   public Move performMove(core.Move move, FastArrayList<Move> pseudoLegalMoves) {
      int from = move.fromY * 8 + move.fromX;
      int to = move.toY * 8 + move.toX;
      Iterator var6 = pseudoLegalMoves.iterator();

      Move pseudoMove;
      do {
         if (!var6.hasNext()) {
            return null;
         }

         pseudoMove = (Move)var6.next();
      } while(pseudoMove.from() != from || pseudoMove.to() != to);

      this.lastFrom = from;
      return pseudoMove;
   }

   public Move promotePawn(int pieceLetter, FastArrayList<Move> pseudoLegalMoves) {
      int promotionPiece = 0;
      boolean var6;
      if (this.aiPlayerWhite()) {
         switch(pieceLetter) {
         case 2:
            var6 = true;
         case 3:
            var6 = true;
         case 1:
            var6 = true;
         case 4:
            promotionPiece = 11;
         }
      } else {
         switch(pieceLetter) {
         case 2:
            var6 = true;
         case 3:
            var6 = true;
         case 1:
            var6 = true;
         case 4:
            promotionPiece = 12;
         }
      }

      Iterator var5 = pseudoLegalMoves.iterator();

      while(var5.hasNext()) {
         Move move = (Move)var5.next();
         if (move.what() == promotionPiece) {
            return move;
         }
      }

      return null;
   }

   private int getX(int sq) {
      return sq % 8;
   }

   private int getY(int sq) {
      return sq / 8;
   }

   public String toLetter(int i) {
      String a = "";
      switch(i) {
      case 0:
         a = "A";
         break;
      case 1:
         a = "B";
         break;
      case 2:
         a = "C";
         break;
      case 3:
         a = "D";
         break;
      case 4:
         a = "E";
         break;
      case 5:
         a = "F";
         break;
      case 6:
         a = "G";
         break;
      case 7:
         a = "H";
      }

      return a;
   }

   public String opponentPromotionCapture(Context context) {
      String note = "";
      ContainerState currentBoardState = context.containerState(0);
      byte from;
      byte to;
      if (this.aiPlayerWhite()) {
         from = 0;
         to = 7;
      } else {
         from = 56;
         to = 63;
      }

      for(int i = from; i <= to; ++i) {
         if (currentBoardState.whoCell(i) == this.opponent && this.previousBoardState.whoCell(i) == this.player && i != this.lastFrom) {
            String sq = this.toLetter(this.getX(i)) + (this.getY(i) + 1);
            note = "Piece at " + sq + " captured";
         }
      }

      return note;
   }

   public Move selectAction(Game game, Context context, double maxSeconds, int maxIterations, int maxDepth) {
      FastArrayList<Move> pseudoLegalMoves = game.moves(context).moves();
      List<String> legalMove = context.getNotes(this.player);
      if (legalMove.size() != 0 && ((String)legalMove.get(0)).equals("Illegal move")) {
         this.agent.illegalMove();
         core.Move lastAttemptedMove = this.agent.sendNextMove();
         this.promotionPiece = lastAttemptedMove.promotionPiece;
         return this.performMove(lastAttemptedMove, pseudoLegalMoves);
      } else if (((Move)pseudoLegalMoves.get(0)).actionDescriptionStringShort().contentEquals("Promote")) {
         return this.promotePawn(this.promotionPiece, pseudoLegalMoves);
      } else {
         legalMove.add(String.valueOf(context.score(this.opponent)).concat(context.score(this.opponent) == 1 ? " try" : " tries"));
         List<String> umpireMessages = context.getNotes(this.opponent);
         umpireMessages.add(String.valueOf(context.score(this.player)).concat(context.score(this.player) == 1 ? " try" : " tries"));
         if (!this.firstMove) {
            String note = this.opponentPromotionCapture(context);
            if (note != "" && !umpireMessages.contains(note)) {
               umpireMessages.add(note);
            }
         }

         if (this.firstMove) {
            this.firstMove = false;
            if (!this.aiPlayerWhite()) {
               this.agent.umpireMessage(umpireMessages);
            }
         } else {
            this.agent.legalMove(legalMove);
            this.agent.umpireMessage(umpireMessages);
         }

         core.Move lastAttemptedMove = this.agent.sendNextMove();
         this.promotionPiece = lastAttemptedMove.promotionPiece;
         this.previousBoardState = context.containerState(0);
         return this.performMove(lastAttemptedMove, pseudoLegalMoves);
      }
   }

   public void initAI(Game game, int playerID) {
      this.player = playerID;
      this.opponent = this.players - playerID + 1;
      this.firstMove = true;

      try {
         this.agent = new Darkboard(this.aiPlayerWhite());
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }
}
