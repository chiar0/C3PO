package umpire.local;

import java.util.StringTokenizer;

public class FENData {
   int[][] board;
   boolean whiteTurn;
   boolean castlingwk = false;
   boolean castlingwq = false;
   boolean castlingbk = false;
   boolean castlingbq = false;
   int enPassantX = -1;
   int enPassantY = -1;
   int halfMove = 0;
   int fullMove = 1;
   String FEN = "";

   public FENData(String fen) throws FENData.MalformedFENException {
      this.FEN = fen;
      StringTokenizer st = new StringTokenizer(fen);
      if (st.countTokens() != 6) {
         throw new FENData.MalformedFENException(fen, "incorrect field count");
      } else {
         for(int k = 0; k < 6; ++k) {
            String token = st.nextToken();
            switch(k) {
            case 0:
               this.board = this.boardLayoutFromFEN(token);
               break;
            case 1:
               if (token.equals("w")) {
                  this.whiteTurn = true;
               } else {
                  if (!token.equals("b")) {
                     throw new FENData.MalformedFENException(fen, "field only accepts 'w' or 'b'");
                  }

                  this.whiteTurn = false;
               }
               break;
            case 2:
               if (!token.equals("-")) {
                  for(int j = 0; j < token.length(); ++j) {
                     char c = token.charAt(j);
                     switch(c) {
                     case 'K':
                        this.castlingwk = true;
                        break;
                     case 'Q':
                        this.castlingwq = true;
                        break;
                     case 'k':
                        this.castlingbk = true;
                        break;
                     case 'q':
                        this.castlingbq = true;
                        break;
                     default:
                        throw new FENData.MalformedFENException(fen, "illegal castling character");
                     }
                  }
               }
               break;
            case 3:
               if (!token.equals("-")) {
                  if (token.length() != 2) {
                     throw new FENData.MalformedFENException(fen, "illegal en passant string");
                  }

                  this.enPassantX = token.charAt(0) - 97;
                  this.enPassantY = token.charAt(1) - 49;
                  if (this.enPassantX > 7 || this.enPassantY > 7 || this.enPassantX < 0 || this.enPassantY < 0) {
                     throw new FENData.MalformedFENException(fen, "illegal en passant square");
                  }
               }
               break;
            case 4:
               this.halfMove = Integer.parseInt(token);
               break;
            case 5:
               this.fullMove = Integer.parseInt(token);
            }
         }

      }
   }

   public int[][] boardLayoutFromFEN(String fen) throws FENData.MalformedFENException {
      int[][] result = new int[8][8];

      for(int k = 0; k < 8; ++k) {
         for(int j = 0; j < 8; ++j) {
            result[k][j] = 0;
         }
      }

      byte file = 0;
      byte rank = 7;

      for(int k = 0; k < fen.length(); ++k) {
         char c = fen.charAt(k);
         char piece = 0;
         switch(c) {
         case '/':
            if (rank == 0) {
               throw new FENData.MalformedFENException(fen, "too many ranks");
            }

            if (file != 8) {
               throw new FENData.MalformedFENException(fen, "incorrect rank width");
            }

            file = 0;
            --rank;
            break;
         case '1':
            ++file;
            break;
         case '2':
            file = (byte)(file + 2);
            break;
         case '3':
            file = (byte)(file + 3);
            break;
         case '4':
            file = (byte)(file + 4);
            break;
         case '5':
            file = (byte)(file + 5);
            break;
         case '6':
            file = (byte)(file + 6);
            break;
         case '7':
            file = (byte)(file + 7);
            break;
         case '8':
            file = (byte)(file + 8);
            break;
         case 'B':
            piece = 3;
            break;
         case 'K':
            piece = 6;
            break;
         case 'N':
            piece = 2;
            break;
         case 'P':
            piece = 1;
            break;
         case 'Q':
            piece = 5;
            break;
         case 'R':
            piece = 4;
            break;
         case 'b':
            piece = 9;
            break;
         case 'k':
            piece = 12;
            break;
         case 'n':
            piece = 8;
            break;
         case 'p':
            piece = 7;
            break;
         case 'q':
            piece = 11;
            break;
         case 'r':
            piece = 10;
         }

         if (piece != 0) {
            result[file++][rank] = piece;
            boolean var10 = false;
         }
      }

      if (rank == 0 && file == 8) {
         return result;
      } else {
         throw new FENData.MalformedFENException(fen, "unexpected end of string");
      }
   }

   public String toString() {
      return this.FEN;
   }

   public int[][] getBoard() {
      return this.board;
   }

   public boolean isCastlingbk() {
      return this.castlingbk;
   }

   public boolean isCastlingbq() {
      return this.castlingbq;
   }

   public boolean isCastlingwk() {
      return this.castlingwk;
   }

   public boolean isCastlingwq() {
      return this.castlingwq;
   }

   public int getEnPassantX() {
      return this.enPassantX;
   }

   public int getEnPassantY() {
      return this.enPassantY;
   }

   public int getFullMove() {
      return this.fullMove;
   }

   public int getHalfMove() {
      return this.halfMove;
   }

   public boolean isWhiteTurn() {
      return this.whiteTurn;
   }

   public String getFEN() {
      return this.FEN;
   }

   public class MalformedFENException extends Exception {
      String f;
      String r;

      public MalformedFENException(String fen, String reason) {
         this.f = fen;
         this.r = reason;
      }

      public String getMessage() {
         return "Bad FEN: " + this.r + ".";
      }
   }
}
