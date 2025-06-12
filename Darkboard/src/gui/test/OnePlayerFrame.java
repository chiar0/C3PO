package gui.test;

import ai.opponent.OpponentProfile;
import ai.player.DeepDarkboard101;
import ai.player.HumanPlayer;
import ai.player.Player;
import ai.player.PlayerListener;
import core.Globals;
import core.Move;
import java.io.File;
import java.util.Scanner;
import pgn.ExtendedPGNGame;
import umpire.local.FENData;
import umpire.local.StepwiseLocalUmpire;

public class OnePlayerFrame {
   private static int gamesPlayed = 0;
   private static boolean initialized = false;
   OnePlayerFrame.UmpireText nodeDescriptionArea;
   public StepwiseLocalUmpire umpire;
   public Player p1;
   public Player p2;
   ExtendedPGNGame pgn = null;
   int turn = 0;
   boolean turnwhite = true;
   boolean gameInProgress = true;
   private String umpireMessages = "";

   public OnePlayerFrame(Player pl1, Player pl2, ExtendedPGNGame pgn, FENData fen) {
      this.p1 = pl1;
      this.p2 = pl2;
      this.umpire = new StepwiseLocalUmpire(this.p1, this.p2);
      this.umpire.adjudication = false;
      this.nodeDescriptionArea = new OnePlayerFrame.UmpireText();
      if (pl1.isHuman()) {
         pl1.addPlayerListener(this.nodeDescriptionArea);
      } else {
         pl2.addPlayerListener(this.nodeDescriptionArea);
      }

      this.umpire.stepwiseInit(fen, pgn);
      ExtendedPGNGame gam = this.umpire.transcript;
      gam.firePGNChange();
   }

   private void interrogatePlayer(boolean white) {
      Player ai = white ? this.p1 : this.p2;
      ai.globals.usingExtendedGameTree = true;
      Move m = ai.getNextMove();
   }

   public static Move parseMoveString(String s, boolean white) {
      Move m = new Move();
      if (s == null) {
         return null;
      } else if (s.equals("O-O")) {
         m.fromX = 4;
         m.toX = 6;
         m.piece = 5;
         if (white) {
            m.toY = m.fromY = 0;
         } else {
            m.toY = m.fromY = 7;
         }

         return m;
      } else if (s.equals("O-O-O")) {
         m.fromX = 4;
         m.toX = 2;
         m.piece = 5;
         if (white) {
            m.toY = m.fromY = 0;
         } else {
            m.toY = m.fromY = 7;
         }

         return m;
      } else {
         int index = 0;
         m.piece = 0;
         switch(s.charAt(0)) {
         case 'B':
            m.piece = 2;
            index = 1;
            break;
         case 'K':
            m.piece = 5;
            index = 1;
            break;
         case 'N':
            m.piece = 1;
            index = 1;
            break;
         case 'Q':
            m.piece = 4;
            index = 1;
            break;
         case 'R':
            m.piece = 3;
            index = 1;
         }

         m.fromX = (byte)(s.charAt(index) - 97);
         m.fromY = (byte)(s.charAt(index + 1) - 49);
         m.toX = (byte)(s.charAt(index + 3) - 97);
         m.toY = (byte)(s.charAt(index + 4) - 49);
         if (s.indexOf("=") != -1) {
            switch(s.charAt(s.length() - 1)) {
            case 'B':
               m.promotionPiece = 2;
               break;
            case 'N':
               m.promotionPiece = 1;
               break;
            case 'Q':
               m.promotionPiece = 4;
               break;
            case 'R':
               m.promotionPiece = 3;
            }
         }

         return m;
      }
   }

   private void loadGame(File f) {
   }

   public void loadGameFromFile() {
   }

   public void saveGame() {
   }

   public void startGame() {
      (new OnePlayerFrame.GameThread()).start();
   }

   public static void main(String[] args) {
      if (!initialized) {
         String path = System.getProperty("user.home") + "/darkboard_data/";
         initialized = true;
      }

      System.out.println(System.getProperty("java.class.path"));
      Globals.hasGui = true;
      OpponentProfile op = OpponentProfile.getProfile("rjay");
      OnePlayerFrame f;
      if (gamesPlayed % 2 == 0) {
         f = new OnePlayerFrame(new HumanPlayer(true), new DeepDarkboard101(false, op.openingBookWhite, op.openingBookBlack, "rjay"), (ExtendedPGNGame)null, (FENData)null);
      } else {
         f = new OnePlayerFrame(new DeepDarkboard101(true, op.openingBookWhite, op.openingBookBlack, "rjay"), new HumanPlayer(false), (ExtendedPGNGame)null, (FENData)null);
      }

      ++gamesPlayed;

      Move m;
      for(; f.umpire.getGameOutcome() == 0; f.umpire.stepwiseArbitrate(m)) {
         Player p = f.umpire.turn();
         System.out.print("\n");

         for(int i = 7; i >= 0; --i) {
            for(int j = 0; j < 8; ++j) {
               System.out.print(f.umpire.board[j][i]);
               System.out.print(" ");
            }

            System.out.print("\n");
         }

         if (p instanceof HumanPlayer) {
            Scanner InputStream = new Scanner(System.in);
            String playerMove = InputStream.nextLine();
            m = parseMoveString(playerMove, p.isWhite);
            p.emulateNextMove(m);
         } else {
            m = p.getNextMove();
         }
      }

      f.umpire.transcript.setWhite("player");
      f.umpire.transcript.setBlack("agent");
      f.umpire.transcript.saveToFile();
   }

   public void actionPerformed() {
      int pPiece = -1;
      if (pPiece != -1) {
         ((HumanPlayer)(this.p1.isHuman() ? this.p1 : this.p2)).setPromotionPiece(pPiece);
      }

   }

   public class GameThread extends Thread {
      public void run() {
         OnePlayerFrame.main(new String[0]);
      }
   }

   public class PGNFileFilter {
      public String getDescription() {
         return "PGN files";
      }

      public boolean accept(File f) {
         return f.getName().endsWith(".pgn") || f.isDirectory();
      }
   }

   public class UmpireText implements PlayerListener {
      boolean finished = false;

      public int currentMove() {
         return OnePlayerFrame.this.umpire.transcript.getMoveNumber();
      }

      public String playerColor(Player p) {
         return p.isWhite ? "White's turn" : "Black's turn";
      }

      public String opponentColor(Player p) {
         return !p.isWhite ? "White's turn" : "Black's turn";
      }

      public String checkString(int check) {
         switch(check) {
         case 1:
            return "File check; ";
         case 2:
            return "Rank check; ";
         case 3:
            return "Long-diagonal check; ";
         case 4:
            return "Short-diagonal check; ";
         case 5:
            return "Knight check; ";
         default:
            return "";
         }
      }

      public String communicateUmpireMessage() {
         return OnePlayerFrame.this.umpireMessages;
      }

      public String triesString(int tries) {
         return tries + (tries != 1 ? " Tries; " : " Try; ");
      }

      public void preAppend(String s) {
      }

      public void communicateIllegalMove(Player p, Move m) {
      }

      public String communicateLegalMove(Player p, int capture, int oppTries, int oppCheck, int oppCheck2) {
         if (p.isHuman() && !this.finished) {
            String s = this.currentMove() + (p.isWhite ? 0 : 1) + ". " + this.opponentColor(p) + "; ";
            if (capture != 0) {
               if (capture == 1) {
                  s = s + "Pawn at ";
               } else {
                  s = s + "Piece at ";
               }

               s = s + Move.squareString(p.lastMove.toX, p.lastMove.toY) + " captured; ";
            }

            if (oppTries > 0) {
               s = s + this.triesString(oppTries);
            }

            s = s + this.checkString(oppCheck);
            s = s + this.checkString(oppCheck2);
            OnePlayerFrame var10000 = OnePlayerFrame.this;
            var10000.umpireMessages = var10000.umpireMessages + s;
            var10000 = OnePlayerFrame.this;
            var10000.umpireMessages = var10000.umpireMessages + "\n";
            this.preAppend(s);
            return s;
         } else {
            return "";
         }
      }

      public void communicateOutcome(Player p, int outcome) {
         if (p.isHuman() && !this.finished) {
            String s = p.isWhite ? "White" : "Black";
            String t = !p.isWhite ? "White" : "Black";
            OnePlayerFrame var10000;
            switch(outcome) {
            case 1:
               this.preAppend(t + " checkmated");
               System.out.println(t + " checkmated");
               var10000 = OnePlayerFrame.this;
               var10000.umpireMessages = var10000.umpireMessages + t + " checkmated";
               var10000 = OnePlayerFrame.this;
               var10000.umpireMessages = var10000.umpireMessages + "\n";
               break;
            case 2:
               this.preAppend("Stalemate");
               System.out.println("Stalemate");
               var10000 = OnePlayerFrame.this;
               var10000.umpireMessages = var10000.umpireMessages + s + " checkmated";
               var10000 = OnePlayerFrame.this;
               var10000.umpireMessages = var10000.umpireMessages + "\n";
               break;
            case 3:
               this.preAppend(s + " checkmated");
               System.out.println(s + " checkmated");
               var10000 = OnePlayerFrame.this;
               var10000.umpireMessages = var10000.umpireMessages + s + " checkmated";
               var10000 = OnePlayerFrame.this;
               var10000.umpireMessages = var10000.umpireMessages + "\n";
               break;
            case 4:
               this.preAppend("Fifty moves draw");
               System.out.println("Fifty moves draw");
               var10000 = OnePlayerFrame.this;
               var10000.umpireMessages = var10000.umpireMessages + "Fifty moves draw";
               var10000 = OnePlayerFrame.this;
               var10000.umpireMessages = var10000.umpireMessages + "\n";
               break;
            case 5:
               this.preAppend("Insufficient Material Draw");
               System.out.println("Insufficient Material Draw");
               var10000 = OnePlayerFrame.this;
               var10000.umpireMessages = var10000.umpireMessages + "Insufficient Material Draw";
               var10000 = OnePlayerFrame.this;
               var10000.umpireMessages = var10000.umpireMessages + "\n";
               break;
            case 6:
               this.preAppend(t + " resigned");
               System.out.println(t + " resigned");
               var10000 = OnePlayerFrame.this;
               var10000.umpireMessages = var10000.umpireMessages + t + " resigned";
               var10000 = OnePlayerFrame.this;
               var10000.umpireMessages = var10000.umpireMessages + "\n";
               break;
            case 7:
               this.preAppend(s + " resigned");
               System.out.println(s + " resigned");
               var10000 = OnePlayerFrame.this;
               var10000.umpireMessages = var10000.umpireMessages + s + " resigned";
               var10000 = OnePlayerFrame.this;
               var10000.umpireMessages = var10000.umpireMessages + "\n";
               break;
            case 8:
               this.preAppend("Mutual agreement draw");
               System.out.println("Mutual agreement draw");
               var10000 = OnePlayerFrame.this;
               var10000.umpireMessages = var10000.umpireMessages + "Mutual agreement draw";
               var10000 = OnePlayerFrame.this;
               var10000.umpireMessages = var10000.umpireMessages + "\n";
            }

            this.finished = true;
         }
      }

      public String communicateUmpireMessage(Player p, int capX, int capY, int tries, int check, int check2, int capture) {
         if (p.isHuman() && !this.finished) {
            String s = this.currentMove() + (p.isWhite ? 1 : 0) + ". " + this.playerColor(p) + "; ";
            if (capture != 0) {
               if (capture == 1) {
                  s = s + "Pawn at ";
               } else {
                  s = s + "Piece at ";
               }

               s = s + Move.squareString(capX, capY) + " captured; ";
            }

            if (tries > 0) {
               s = s + this.triesString(tries);
            }

            s = s + this.checkString(check);
            s = s + this.checkString(check2);
            OnePlayerFrame var10000 = OnePlayerFrame.this;
            var10000.umpireMessages = var10000.umpireMessages + s;
            var10000 = OnePlayerFrame.this;
            var10000.umpireMessages = var10000.umpireMessages + "\n";
            this.preAppend(s);
            return s;
         } else {
            return "";
         }
      }

      public void updateTime(Player p, long newQty) {
      }

      public void communicateInfo(Player p, int code, int parameter) {
         switch(code) {
         case 1:
            OnePlayerFrame var10000 = OnePlayerFrame.this;
            var10000.umpireMessages = var10000.umpireMessages + "Draw offer was rejected.";
            var10000 = OnePlayerFrame.this;
            var10000.umpireMessages = var10000.umpireMessages + "\n";
         default:
         }
      }

      public void communicateObject(Player p, Object tag, Object value, int messageType) {
      }

      public boolean isInterestedInObject(Player p, Object tag, int messageType) {
         return false;
      }
   }
}
