package pgn;

import ai.player.Player;
import core.Move;
import database.GameDatabase;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import umpire.local.FENData;
import umpire.local.StepwiseLocalUmpire;

public class PGNParser {
   Reader source;
   StepwiseLocalUmpire umpire;
   private char[] out = new char[16384];
   private char[] buffer = new char[16384];
   public String accumulator = null;
   public boolean recordGameString = false;
   private FENData fen;
   int length;
   private String extern;
   static byte[] a = new byte[1];

   public PGNParser(Reader s) {
      this.source = s;
      this.umpire = new StepwiseLocalUmpire((Player)null, (Player)null);
   }

   public PGNParser(InputStream stream) {
      this.source = new InputStreamReader(stream);
      this.umpire = new StepwiseLocalUmpire((Player)null, (Player)null);
   }

   public PGNParser(String s) {
      this.source = new StringReader(s);
      this.umpire = new StepwiseLocalUmpire((Player)null, (Player)null);
   }

   private int readNext() {
      if (this.source == null) {
         return -1;
      } else {
         try {
            ++this.length;
            int data = this.source.read();
            if (this.extern != null) {
               a[0] = (byte)data;
               this.extern = this.extern + new String(a);
            }

            return data;
         } catch (IOException var2) {
            var2.printStackTrace();
            return -1;
         }
      }
   }

   private String readNextToken() {
      int index = 0;

      int result;
      char c;
      do {
         result = this.readNext();
         if (result == -1) {
            return null;
         }

         c = (char)result;
      } while(c <= ' ');

      if (c == '"') {
         do {
            result = this.readNext();
            if (result == -1) {
               return null;
            }

            c = (char)result;
            if (c != '"') {
               this.out[index++] = c;
            }
         } while(c != '"');

         return new String(this.out, 0, index);
      } else if (c == '{') {
         do {
            result = this.readNext();
            if (result == -1) {
               return null;
            }

            c = (char)result;
            this.out[index++] = c;
         } while(c != '}');

         return new String(this.out, 0, index);
      } else {
         if (c == '[' || c == ']' || c == '{' || c == '}') {
            if (c == '[') {
               return "[";
            }

            if (c == ']') {
               return "]";
            }

            if (c == '{') {
               return "{";
            }

            if (c == '}') {
               return "}";
            }
         }

         do {
            this.out[index++] = c;
            result = this.readNext();
            if (result != -1) {
               c = (char)result;
            }
         } while(result != -1 && c > ' ');

         return new String(this.out, 0, index);
      }
   }

   private boolean parsePGNTag(ExtendedPGNGame game) {
      String tagName = this.readNextToken();
      String tagValue = this.readNextToken();
      String bracket = this.readNextToken();
      if (tagName != null && tagValue != null && bracket != null && bracket.equals("]")) {
         if (tagName.equals("White")) {
            game.setWhite(tagValue);
         } else if (tagName.equals("Black")) {
            game.setBlack(tagValue);
         } else if (tagName.equals("Date")) {
            game.setDate(tagValue);
         } else if (tagName.equals("Event")) {
            game.setEvent(tagValue);
            if (tagValue.indexOf("w16") == -1) {
               return false;
            }
         } else if (tagName.equals("Result")) {
            game.setResult(tagValue);
         } else if (tagName.equals("Site")) {
            game.setSite(tagValue);
         } else if (tagName.equals("Time")) {
            game.setTime(tagValue);
         } else if (tagName.equals("Variant")) {
            game.setVariant(tagValue);
         } else if (tagName.equals("ICCResult")) {
            game.setICCResult(tagValue);
         } else if (tagName.equals("WhiteElo")) {
            game.setWhiteElo(Integer.parseInt(tagValue));
         } else if (tagName.equals("BlackElo")) {
            game.setBlackElo(Integer.parseInt(tagValue));
         } else {
            game.addTag(tagName, tagValue);
         }

         if (tagName.equals("FEN")) {
            try {
               this.fen = new FENData(tagValue);
            } catch (Exception var6) {
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public ExtendedPGNGame parseNextGame() {
      ExtendedPGNGame game = new ExtendedPGNGame();
      ExtendedPGNGame.PGNMoveData last = null;
      boolean parsingIntro = true;
      boolean finished = false;
      boolean whiteTurn = true;
      boolean interpret = true;
      boolean umpireInitialized = false;
      this.umpire.resetBoard();
      this.length = 0;
      if (this.recordGameString) {
         try {
            this.extern = new String();
         } catch (Exception var13) {
         }
      } else {
         this.extern = null;
      }

      try {
         while(true) {
            while(!finished) {
               String token = this.readNextToken();
               if (token != null) {
                  game.associatedString = game.associatedString + token;
               }

               if (token == null) {
                  return null;
               }

               if (token.equals("[")) {
                  if (!this.parsePGNTag(game)) {
                     interpret = false;
                  }
               } else if (!token.endsWith("}")) {
                  if (token.equals("]")) {
                     interpret = false;
                  } else if (!token.equals("1-0") && !token.equals("0-1") && !token.equals("1/2-1/2") && !token.equals("*")) {
                     if (!token.endsWith(".") && interpret) {
                        if (!umpireInitialized) {
                           this.umpire.stepwiseInit(this.fen, (ExtendedPGNGame)null);
                           umpireInitialized = true;
                        }

                        Move m = this.umpire.getMoveFromPGN(token, whiteTurn);
                        game.addMove(whiteTurn, m, this.umpire.capX, this.umpire.capY, this.umpire.capture, this.umpire.check[0], this.umpire.check[1], this.umpire.tries);
                        last = game.getLatestMove(whiteTurn);
                        whiteTurn = !whiteTurn;
                     }
                  } else {
                     finished = true;
                  }
               } else if (token.startsWith("(") && token.endsWith(")}") && token.indexOf(":") != -1) {
                  String reducedToken = token.substring(token.indexOf(":") + 1, token.length() - 2);
                  if (reducedToken.length() > 0) {
                     System.out.println(reducedToken);
                  }

                  int number = reducedToken.length() > 0 ? 1 : 0;

                  int k;
                  for(k = 0; k < reducedToken.length(); ++k) {
                     if (reducedToken.charAt(k) == ',') {
                        ++number;
                     }
                  }

                  for(k = 0; k < number; ++k) {
                     last.addFailedMove(new Move());
                  }
               }
            }

            if (this.recordGameString) {
               game.associatedString = this.extern;
            }

            return game;
         }
      } catch (Exception var14) {
         var14.printStackTrace();
         if (game != null) {
            System.out.println("Game Date: " + game.getDate());
            System.out.println("Game Time: " + game.getTime());
            System.out.println("White: " + game.getWhite());
            System.out.println("Black: " + game.getBlack());
         }

         return null;
      }
   }

   public static double getTryPerMoveRatio(GameDatabase gd, String who) {
      int tries = 0;
      int moves = 0;

      for(int k = 0; k < gd.gameNumber(); ++k) {
         ExtendedPGNGame g = gd.getGame(k);
         boolean getWhite = g.getWhite().equals(who);
         boolean getBlack = g.getBlack().equals(who);

         for(int j = 0; j < g.getMoveNumber(); ++j) {
            ExtendedPGNGame.PGNMoveData d = g.getMove(true, j);
            if (getWhite && d != null) {
               ++moves;
               tries += (d.failedMoves != null ? d.failedMoves.size() : 0) + 1;
            }

            d = g.getMove(false, j);
            if (getBlack && d != null) {
               ++moves;
               tries += (d.failedMoves != null ? d.failedMoves.size() : 0) + 1;
            }
         }
      }

      return 1.0D * (double)tries / (double)moves;
   }
}
