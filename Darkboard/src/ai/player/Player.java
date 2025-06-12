package ai.player;

import ai.mc.MCSTSNode;
import ai.opponent.MetapositionPool;
import core.EvaluationGlobals;
import core.Globals;
import core.Metaposition;
import core.Move;
import core.uberposition.Uberposition;
import database.PlayerModel;
import debug.DebugObject;
import java.util.Dictionary;
import java.util.Vector;
import pgn.ExtendedPGNGame;
import umpire.Umpire;

public class Player extends DebugObject {
   public static final String PARAM_OPPONENT_NAME = "opponentName";
   public static final String PARAM_IS_WHITE = "isWhite";
   public static final String PARAM_TIME = "time";
   public static final String PARAM_TIME_INCREMENT = "timeIncrement";
   public static final int PARAM_CHECKMATE_VICTORY = 1;
   public static final int PARAM_STALEMATE_DRAW = 2;
   public static final int PARAM_CHECKMATE_DEFEAT = 3;
   public static final int PARAM_50_DRAW = 4;
   public static final int PARAM_NO_MATERIAL = 5;
   public static final int PARAM_RESIGN_VICTORY = 6;
   public static final int PARAM_RESIGN_DEFEAT = 7;
   public static final int PARAM_AGREED_DRAW = 8;
   public static final int INFO_DRAW_OFFER_REJECTED = 1;
   public boolean experimental = false;
   public String playerName;
   public String currentOpponentName;
   public boolean isWhite;
   public int genericOppModel = 0;
   public Metaposition simplifiedBoard;
   public Uberposition complexBoard;
   public EvaluationGlobals globals;
   public Vector bannedMoves = new Vector();
   public short[] shortBannedMoves = new short[0];
   protected Vector playerListeners = new Vector();
   public Move lastMove;
   public int moveNumber = 1;
   public Umpire currentUmpire = null;
   public MetapositionPool pool;

   public Player() {
      if (this.usesGlobals()) {
         this.globals = new EvaluationGlobals(this);
      }

   }

   public boolean usesGlobals() {
      return false;
   }

   public void startMatch(Dictionary d) {
      boolean isWhite = (Boolean)d.get("isWhite");
   }

   public Move getNextMove() {
      return this.lastMove;
   }

   public void setHasTurn(boolean b) {
   }

   public void emulateNextMove(Move m) {
      this.lastMove = m;
   }

   public void playMove(Move m) {
      this.emulateNextMove(m);
      this.getNextMove();
   }

   public void startFromAlternatePosition(Metaposition m) {
      this.simplifiedBoard = m;
   }

   public void communicateIllegalMove(Move m) {
      for(int k = 0; k < this.getPlayerListenerNumber(); ++k) {
         this.getPlayerListener(k).communicateIllegalMove(this, m);
      }

   }

   public String communicateLegalMove(int capture, int oppTries, int oppCheck, int oppCheck2) {
      if (!this.isWhite) {
         ++this.moveNumber;
      }

      int k = 0;
      return k < this.getPlayerListenerNumber() ? this.getPlayerListener(k).communicateLegalMove(this, capture, oppTries, oppCheck, oppCheck2) : "";
   }

   public String communicateUmpireMessage(int capX, int capY, int tries, int check, int check2, int captureType) {
      if (this.isWhite) {
         ++this.moveNumber;
      }

      int k = 0;
      return k < this.getPlayerListenerNumber() ? this.getPlayerListener(k).communicateUmpireMessage(this, capX, capY, tries, check, check2, captureType) : "";
   }

   public void communicateUmpireInfo(int code, int parameter) {
      for(int k = 0; k < this.getPlayerListenerNumber(); ++k) {
         this.getPlayerListener(k).communicateInfo(this, code, parameter);
      }

   }

   public void updateTime(long newQty) {
      for(int k = 0; k < this.getPlayerListenerNumber(); ++k) {
         this.getPlayerListener(k).updateTime(this, newQty);
      }

   }

   public void communicateOutcome(int outcome) {
      for(int k = 0; k < this.getPlayerListenerNumber(); ++k) {
         this.getPlayerListener(k).communicateOutcome(this, outcome);
      }

   }

   public String getCurrentOpponentName() {
      return this.currentOpponentName;
   }

   public String getPlayerName() {
      return this.playerName;
   }

   public void setCurrentOpponentName(String string) {
      this.currentOpponentName = string;
   }

   public void setPlayerName(String string) {
      this.playerName = string;
   }

   public Umpire getCurrentUmpire() {
      return this.currentUmpire;
   }

   public void setCurrentUmpire(Umpire umpire) {
      this.currentUmpire = umpire;
   }

   public boolean isHuman() {
      return false;
   }

   public boolean shouldAskDraw() {
      return false;
   }

   public boolean shouldAcceptDraw() {
      return false;
   }

   public boolean isMoveBanned(Move m) {
      for(int k = 0; k < this.bannedMoves.size(); ++k) {
         Move m2 = (Move)this.bannedMoves.get(k);
         if (m2.equals(m)) {
            return true;
         }
      }

      return false;
   }

   public boolean isShortMoveBanned(short m) {
      for(int k = 0; k < this.shortBannedMoves.length; ++k) {
         if (m == this.shortBannedMoves[k]) {
            return true;
         }
      }

      return false;
   }

   public void banMove(Move m) {
      if (!this.isMoveBanned(m)) {
         this.bannedMoves.add(m);
         this.banShortMove(MCSTSNode.move2Short(m));
      }

   }

   public void banShortMove(short m) {
      if (!this.isShortMoveBanned(m)) {
         short[] a = new short[this.shortBannedMoves.length + 1];

         for(int k = 0; k < this.shortBannedMoves.length; ++k) {
            a[k] = this.shortBannedMoves[k];
         }

         a[this.shortBannedMoves.length] = m;
         this.shortBannedMoves = a;
         this.banMove(MCSTSNode.short2Move(m));
      }

   }

   public void unbanMoves() {
      this.bannedMoves.clear();
      this.shortBannedMoves = new short[0];
   }

   public void addPlayerListener(PlayerListener pl) {
      this.playerListeners.add(pl);
   }

   public void removePlayerListener(PlayerListener pl) {
      this.playerListeners.remove(pl);
   }

   public int getPlayerListenerNumber() {
      return this.playerListeners.size();
   }

   public PlayerListener getPlayerListener(int k) {
      return (PlayerListener)this.playerListeners.get(k);
   }

   public boolean IsAnyoneInterestedIn(Object tag, int messageType) {
      for(int k = 0; k < this.getPlayerListenerNumber(); ++k) {
         if (this.getPlayerListener(k).isInterestedInObject(this, tag, messageType)) {
            return true;
         }
      }

      return false;
   }

   public void communicateObject(Object tag, Object value, int messageType) {
      for(int k = 0; k < this.getPlayerListenerNumber(); ++k) {
         if (this.getPlayerListener(k).isInterestedInObject(this, tag, messageType)) {
            this.getPlayerListener(k).communicateObject(this, tag, value, messageType);
         }
      }

   }

   public PlayerModel getOpponentModel() {
      return this.isWhite ? Globals.blackModel[this.genericOppModel] : Globals.whiteModel[this.genericOppModel];
   }

   public void receiveAftermath(ExtendedPGNGame game) {
   }
}
