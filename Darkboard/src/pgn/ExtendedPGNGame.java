package pgn;

import ai.player.Player;
import core.Move;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import umpire.local.FENData;
import umpire.local.LocalUmpire;
import umpire.local.StepwiseLocalUmpire;

public class ExtendedPGNGame implements Serializable {
   String event = "ICC w16";
   String site = "";
   String date = "";
   String time = "";
   String round = "1";
   String white = "Player 1";
   String black = "Player 2";
   String result = "*";
   String ICCResult = "*";
   String variant = "";
   int whiteElo = -1;
   int blackElo = -1;
   protected static int counter = 0;
   Hashtable<String, String> tags = new Hashtable();
   Vector whiteMoves = new Vector();
   Vector blackMoves = new Vector();
   Vector changeListeners = new Vector();
   public String associatedString = "";

   public static String utilityParseInt(int i) {
      String s = String.valueOf(i);
      if (i < 10) {
         s = "0" + s;
      }

      return s;
   }

   public ExtendedPGNGame() {
      Calendar rightNow = Calendar.getInstance();
      this.setDate(rightNow.get(1) + "." + utilityParseInt(rightNow.get(2) + 1) + "." + utilityParseInt(rightNow.get(5)));
      this.setTime(utilityParseInt(rightNow.get(11)) + ":" + utilityParseInt(rightNow.get(12)) + ":" + utilityParseInt(rightNow.get(13)));
   }

   public void addMove(boolean white, Move m, int cx, int cy, int cwhat, int ch1, int ch2, int tries) {
      ExtendedPGNGame.PGNMoveData data = new ExtendedPGNGame.PGNMoveData();
      data.finalMove = m;
      data.capturex = cx;
      data.capturey = cy;
      data.capturewhat = cwhat;
      data.check1 = ch1;
      data.check2 = ch2;
      data.pawntries = tries;
      if (white) {
         this.whiteMoves.add(data);
      } else {
         this.blackMoves.add(data);
      }

      this.firePGNChange();
   }

   public ExtendedPGNGame.PGNMoveData getMove(boolean white, int index) {
      Vector v = white ? this.whiteMoves : this.blackMoves;
      return index >= v.size() ? null : (ExtendedPGNGame.PGNMoveData)v.get(index);
   }

   public int getMoveNumber() {
      return this.whiteMoves.size();
   }

   public ExtendedPGNGame.PGNMoveData getLatestMove(boolean white) {
      Vector v = white ? this.whiteMoves : this.blackMoves;
      return (ExtendedPGNGame.PGNMoveData)v.lastElement();
   }

   public String outputPGNTag(String tag, String value) {
      if (tag == null) {
         return "";
      } else {
         if (value == null) {
            value = "??";
         }

         return "[" + tag + " \"" + value + "\"]\n";
      }
   }

   public boolean isPlayerWhite(String name) {
      return this.getWhite().equals(name);
   }

   public String toString() {
      String result = "";
      StepwiseLocalUmpire u = new StepwiseLocalUmpire((Player)null, (Player)null);
      FENData f = null;

      try {
         String s = (String)this.tags.get("FEN");
         f = s != null ? new FENData(s) : null;
      } catch (Exception var7) {
         System.out.println("bah");
      }

      u.stepwiseInit(f, (ExtendedPGNGame)null);
      result = result + this.outputPGNTag("Event", this.getEvent());
      result = result + this.outputPGNTag("Site", this.getSite());
      result = result + this.outputPGNTag("Date", this.getDate());
      result = result + this.outputPGNTag("Round", this.getRound());
      result = result + this.outputPGNTag("White", this.getWhite());
      result = result + this.outputPGNTag("Black", this.getBlack());
      result = result + this.outputPGNTag("Result", this.getResult());
      result = result + this.outputPGNTag("Variant", this.getVariant());
      result = result + this.outputPGNTag("Time", this.getTime());

      String t;
      for(Enumeration e = this.tags.keys(); e.hasMoreElements(); result = result + this.outputPGNTag(t, (String)this.tags.get(t))) {
         t = (String)e.nextElement();
      }

      result = result + "\n";

      for(int k = 0; k < this.whiteMoves.size(); ++k) {
         String pgnComments = this.addExtendedPGNComments(k, true, u);
         result = result + (k + 1) + ".   " + u.getPGNMoveString(this.getMove(true, k).finalMove, true, true) + " ";
         result = result + pgnComments;
         result = result + "\n";
         if (k < this.blackMoves.size()) {
            pgnComments = this.addExtendedPGNComments(k, false, u);
            result = result + "     " + u.getPGNMoveString(this.getMove(false, k).finalMove, false, true) + " ";
            result = result + pgnComments;
            result = result + "\n";
         }
      }

      if (this.getResult() != null) {
         result = result + this.getResult();
      }

      result = result + "\n\n";
      return result;
   }

   public String addExtendedPGNComments(int index, boolean white, LocalUmpire game) {
      String result = "{(";
      ExtendedPGNGame.PGNMoveData move = this.getMove(white, index);
      boolean previousTag = false;
      if (move.capturewhat != 0) {
         previousTag = true;
         result = result + "X";
         result = result + Move.squareString(move.capturex, move.capturey);
      }

      if (move.check1 != 0 || move.check2 != 0) {
         if (previousTag) {
            result = result + ",";
         }

         previousTag = true;
         result = result + "C";
         if (move.check1 == 1 || move.check2 == 1) {
            result = result + "F";
         }

         if (move.check1 == 3 || move.check2 == 3) {
            result = result + "L";
         }

         if (move.check1 == 5 || move.check2 == 5) {
            result = result + "N";
         }

         if (move.check1 == 2 || move.check2 == 2) {
            result = result + "R";
         }

         if (move.check1 == 4 || move.check2 == 4) {
            result = result + "S";
         }
      }

      if (move.pawntries > 0) {
         if (previousTag) {
            result = result + ",";
         }

         previousTag = true;
         result = result + "P" + move.pawntries;
      }

      result = result + ":";
      if (move.failedMoves != null) {
         for(int k = 0; k < move.failedMoves.size(); ++k) {
            Move m = (Move)move.failedMoves.get(k);
            if (k > 0) {
               result = result + ",";
            }

            result = result + game.getPGNMoveString(m, white, false);
         }
      }

      result = result + ")}";
      return result;
   }

   public void replayGame(LocalUmpire lu) {
      lu.resetBoard();

      for(int k = 0; k < this.getMoveNumber(); ++k) {
         ++lu.moveCount;
         ExtendedPGNGame.PGNMoveData data = this.getMove(true, k);
         if (data == null) {
            return;
         }

         lu.doMove(data.finalMove, 0);
         data = this.getMove(false, k);
         if (data == null) {
            return;
         }

         lu.doMove(data.finalMove, 1);
      }

   }

   public String getFileName() {
      String result = this.getWhite() + "_" + this.getBlack() + "_" + this.getDate() + "_" + counter++ + ".pgn";
      return result;
   }

   public void saveToFile() {
      String PGNpath = this.getFileName();
      File f = new File("./" + PGNpath);
      this.saveToFile(f);
   }

   public void saveToFile(File f) {
      try {
         f.delete();
         f.createNewFile();
         FileOutputStream stream = new FileOutputStream(f);
         stream.write(this.toString().getBytes());
         stream.close();
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   public void addTag(String k, String v) {
      if (this.tags.containsKey(k)) {
         this.tags.remove(k);
      }

      this.tags.put(k, v);
   }

   public String getTag(String k) {
      return (String)this.tags.get(k);
   }

   public String getBlack() {
      return this.black;
   }

   public String getDate() {
      return this.date;
   }

   public String getEvent() {
      return this.event;
   }

   public String getResult() {
      return this.result;
   }

   public String getRound() {
      return this.round;
   }

   public String getSite() {
      return this.site;
   }

   public String getVariant() {
      return this.variant;
   }

   public String getWhite() {
      return this.white;
   }

   public void setBlack(String string) {
      this.black = string;
   }

   public void setDate(String string) {
      this.date = string;
   }

   public void setEvent(String string) {
      this.event = string;
   }

   public void setResult(String string) {
      this.result = string;
   }

   public void setRound(String string) {
      this.round = string;
   }

   public void setSite(String string) {
      this.site = string;
   }

   public void setVariant(String string) {
      this.variant = string;
   }

   public void setWhite(String string) {
      this.white = string;
   }

   public String getTime() {
      return this.time;
   }

   public void setTime(String string) {
      this.time = string;
   }

   public String getICCResult() {
      return this.ICCResult;
   }

   public void setICCResult(String string) {
      this.ICCResult = string;
   }

   public int getWhiteElo() {
      return this.whiteElo;
   }

   public void setWhiteElo(int whiteElo) {
      this.whiteElo = whiteElo;
   }

   public int getBlackElo() {
      return this.blackElo;
   }

   public void setBlackElo(int blackElo) {
      this.blackElo = blackElo;
   }

   public int getBestElo() {
      if (this.result.equals("1-0")) {
         return this.whiteElo;
      } else if (this.result.equals("0-1")) {
         return this.blackElo;
      } else {
         return this.whiteElo > this.blackElo ? this.whiteElo : this.blackElo;
      }
   }

   public boolean isCheckmate() {
      return this.getICCResult().indexOf("checkmate") != -1;
   }

   public boolean isStalemate() {
      return this.getICCResult().indexOf("stalemate") != -1;
   }

   public void addListener(PGNChangeListener l) {
      this.changeListeners.add(l);
   }

   public void removeListener(PGNChangeListener l) {
      this.changeListeners.remove(l);
   }

   public void firePGNChange() {
      for(int k = 0; k < this.changeListeners.size(); ++k) {
         ((PGNChangeListener)this.changeListeners.get(k)).pgnChangeNotify(this);
      }

   }

   public class PGNMoveData implements Serializable {
      public Move finalMove = null;
      public int capturex;
      public int capturey;
      public int capturewhat;
      public int check1;
      public int check2;
      public int pawntries = -1;
      Vector<Move> failedMoves = null;

      public void addFailedMove(Move m) {
         if (this.failedMoves == null) {
            this.failedMoves = new Vector();
         }

         this.failedMoves.add(m);
      }

      public int getFailedMoveNumber() {
         return this.failedMoves == null ? 0 : this.failedMoves.size();
      }

      public Move getFailedMove(int k) {
         return this.failedMoves != null && k >= 0 && k < this.failedMoves.size() ? (Move)this.failedMoves.get(k) : null;
      }
   }
}
