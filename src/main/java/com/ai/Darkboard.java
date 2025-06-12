package agent;

import ai.opening.OpeningTreeNode;
import ai.opponent.OpponentProfile;
import ai.player.DeepDarkboard101;
import core.Move;
import database.OpeningBook;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class Darkboard extends Umpire {
   boolean localPlayerWhite;
   boolean currentTurnWhite;
   Move lastAttemptedMove;
   Vector failedMoves = new Vector();
   int promotionPiece;
   private static int NO_CAPTURE = 0;
   private static int CAPTURE_PAWN = 1;
   private static int CAPTURE_PIECE = 2;
   private static int NO_CHECK = 0;
   private static int CHECK_FILE = 1;
   private static int CHECK_RANK = 2;
   private static int CHECK_LONG_DIAGONAL = 3;
   private static int CHECK_SHORT_DIAGONAL = 4;
   private static int CHECK_KNIGHT = 5;

   public OpponentProfile load() {
      try {
         String path = Darkboard.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("Darkboard.jar", "resources/rjay");
         FileInputStream fis = new FileInputStream(path);
         ObjectInputStream ois = new ObjectInputStream(fis);
         OpponentProfile ob = new OpponentProfile();

         try {
            ob.name = (String)ois.readObject();
         } catch (ClassNotFoundException var17) {
            var17.printStackTrace();
         }

         try {
            ob.where = (String)ois.readObject();
         } catch (ClassNotFoundException var16) {
            var16.printStackTrace();
         }

         try {
            ob.whitesize = (Integer)ois.readObject();
         } catch (ClassNotFoundException var15) {
            var15.printStackTrace();
         }

         try {
            ob.blacksize = (Integer)ois.readObject();
         } catch (ClassNotFoundException var14) {
            var14.printStackTrace();
         }

         try {
            ob.whiteBook = (OpeningBook)ois.readObject();
         } catch (ClassNotFoundException var13) {
            var13.printStackTrace();
         }

         try {
            ob.blackBook = (OpeningBook)ois.readObject();
         } catch (ClassNotFoundException var12) {
            var12.printStackTrace();
         }

         try {
            ob.piecePresenceWhite = (float[][][][])ois.readObject();
         } catch (ClassNotFoundException var11) {
            var11.printStackTrace();
         }

         try {
            ob.piecePresenceBlack = (float[][][][])ois.readObject();
         } catch (ClassNotFoundException var10) {
            var10.printStackTrace();
         }

         try {
            ob.openingBookWhite = (OpeningTreeNode)ois.readObject();
         } catch (ClassNotFoundException var9) {
            var9.printStackTrace();
         }

         try {
            ob.openingBookBlack = (OpeningTreeNode)ois.readObject();
         } catch (ClassNotFoundException var8) {
            var8.printStackTrace();
         }

         try {
            ob.customStrategyWhite = (OpeningTreeNode)ois.readObject();
         } catch (ClassNotFoundException var7) {
            var7.printStackTrace();
         }

         try {
            ob.customStrategyBlack = (OpeningTreeNode)ois.readObject();
         } catch (ClassNotFoundException var6) {
            var6.printStackTrace();
         }

         ois.close();
         return ob;
      } catch (IOException var18) {
         var18.printStackTrace();
         return null;
      }
   }

   public Darkboard(boolean isWhite) {
      this.localPlayerWhite = isWhite;
      this.failedMoves.clear();
      OpponentProfile op = this.load();
      this.setP1(new DeepDarkboard101(this.localPlayerWhite, op.openingBookWhite, op.openingBookBlack));
      this.getP1().setCurrentUmpire(this);
      this.currentTurnWhite = true;
      Hashtable<String, Object> d1 = new Hashtable();
      d1.put("isWhite", isWhite);
      d1.put("white", 2);
      d1.put("black", 1);
      d1.put("where", "ludii");
      this.getP1().startMatch(d1);
   }

   public void illegalMove() {
      this.getP1().communicateIllegalMove(this.lastAttemptedMove);
      this.failedMoves.add(this.lastAttemptedMove.clone());
   }

   private int getTries(String message) {
      int i = 0;

      int tries;
      for(tries = 0; message.charAt(i) >= '0' && message.charAt(i) <= '9'; ++i) {
         tries *= 10;
         tries += message.charAt(i) - 48;
      }

      return tries;
   }

   private String getCapturePosition(String message) {
      String square = "";

      for(int i = 1; i < message.length(); ++i) {
         if (Character.isUpperCase(message.charAt(i)) || Character.isDigit(message.charAt(i))) {
            square = square + message.charAt(i);
         }
      }

      return square;
   }

   public void umpireMessage(List<String> messages) {
      int captureX = -1;
      int captureY = -1;
      int check1 = NO_CHECK;
      int check2 = NO_CHECK;
      int pawnTries = 0;
      int captureType = NO_CAPTURE;

      for(int i = 0; i < messages.size(); ++i) {
         String message = (String)messages.get(i);
         if (message.matches("(.*) captured")) {
            String square = this.getCapturePosition(message);
            captureX = square.charAt(0) - 65;
            captureY = square.charAt(1) - 49;
            if (message.matches("Pawn at (.*) captured")) {
               captureType = CAPTURE_PAWN;
            } else if (message.matches("Piece at (.*) captured")) {
               captureType = CAPTURE_PIECE;
            }
         } else if (message.matches("Short diagonal check")) {
            if (check1 > 0) {
               check2 = CHECK_SHORT_DIAGONAL;
            } else {
               check1 = CHECK_SHORT_DIAGONAL;
            }
         } else if (message.matches("Long diagonal check")) {
            if (check1 > 0) {
               check2 = CHECK_LONG_DIAGONAL;
            } else {
               check1 = CHECK_LONG_DIAGONAL;
            }
         } else if (message.matches("File check")) {
            if (check1 > 0) {
               check2 = CHECK_FILE;
            } else {
               check1 = CHECK_FILE;
            }
         } else if (message.matches("Rank check")) {
            if (check1 > 0) {
               check2 = CHECK_RANK;
            } else {
               check1 = CHECK_RANK;
            }
         } else if (message.matches("Knight Check")) {
            if (check1 > 0) {
               check2 = CHECK_KNIGHT;
            } else {
               check1 = CHECK_KNIGHT;
            }
         } else if (message.matches("(.*) try") || message.matches("(.*) tries")) {
            pawnTries = this.getTries(message);
         }
      }

      if (captureX == -1 && (this.lastAttemptedMove == null || this.lastAttemptedMove.piece != 0)) {
         if (this.currentTurnWhite == this.localPlayerWhite) {
            ++this.fiftyMoves;
         }
      } else {
         this.fiftyMoves = 0;
      }

      this.currentTurnWhite = !this.currentTurnWhite;
      this.getP1().communicateUmpireMessage(captureX, captureY, pawnTries, check1, check2, captureType);
   }

   public void legalMove(List<String> messages) {
      int capture = NO_CAPTURE;
      int oppTries = 0;
      int oppCheck = NO_CHECK;
      int oppCheck2 = NO_CHECK;

      for(int i = 0; i < messages.size(); ++i) {
         String message = (String)messages.get(i);
         if (message.matches("Pawn at (.*) captured")) {
            capture = CAPTURE_PAWN;
         } else if (message.matches("Piece at (.*) captured")) {
            capture = CAPTURE_PIECE;
         } else if (message.matches("Short diagonal check")) {
            if (oppCheck > 0) {
               oppCheck2 = CHECK_SHORT_DIAGONAL;
            } else {
               oppCheck = CHECK_SHORT_DIAGONAL;
            }
         } else if (message.matches("Long diagonal check")) {
            if (oppCheck > 0) {
               oppCheck2 = CHECK_LONG_DIAGONAL;
            } else {
               oppCheck = CHECK_LONG_DIAGONAL;
            }
         } else if (message.matches("File check")) {
            if (oppCheck > 0) {
               oppCheck2 = CHECK_FILE;
            } else {
               oppCheck = CHECK_FILE;
            }
         } else if (message.matches("Rank check")) {
            if (oppCheck > 0) {
               oppCheck2 = CHECK_RANK;
            } else {
               oppCheck = CHECK_RANK;
            }
         } else if (message.matches("Knight Check")) {
            if (oppCheck > 0) {
               oppCheck2 = CHECK_KNIGHT;
            } else {
               oppCheck = CHECK_KNIGHT;
            }
         } else if (message.matches("(.*) try") || message.matches("(.*) tries")) {
            oppTries = this.getTries(message);
         }
      }

      if (capture == 0 && (this.lastAttemptedMove == null || this.lastAttemptedMove.piece != 0)) {
         if (this.currentTurnWhite == this.localPlayerWhite) {
            ++this.fiftyMoves;
         }
      } else {
         this.fiftyMoves = 0;
      }

      this.failedMoves.clear();
      this.currentTurnWhite = !this.currentTurnWhite;
      this.getP1().communicateLegalMove(capture, oppTries, oppCheck, oppCheck2);
   }

   public Move sendNextMove() {
      try {
         if (this.getP1().shouldAskDraw()) {
            this.offerDraw(this.getP1());
         } else {
            this.lastAttemptedMove = this.getP1().getNextMove();
            if (this.lastAttemptedMove == null) {
               this.resign(this.getP1());
               return null;
            }
         }
      } catch (Exception var2) {
         this.lastAttemptedMove = null;
         var2.printStackTrace();
         this.resign(this.getP1());
         return null;
      }

      return this.lastAttemptedMove;
   }
}
