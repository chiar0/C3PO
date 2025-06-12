package core;

import java.io.Serializable;

public class Move implements Cloneable, Serializable {
   public byte piece;
   public byte fromX;
   public byte fromY;
   public byte toX;
   public byte toY;
   public byte promotionPiece = 4;
   public boolean capture = false;
   public boolean check = false;
   public boolean doublecheck = false;
   public boolean checkmate = false;
   public boolean showFromX = true;
   public boolean showFromY = true;
   public boolean showSeparator = true;
   public static String[] ranks = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
   public static String[] files = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};

   public Object clone() {
      try {
         return super.clone();
      } catch (Exception var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public String pieceLetter(int p) {
      switch(p) {
      case 0:
         return "";
      case 1:
         return "N";
      case 2:
         return "B";
      case 3:
         return "R";
      case 4:
         return "Q";
      case 5:
         return "K";
      default:
         return "";
      }
   }

   public String toString() {
      if (this.piece == 5 && this.toX == this.fromX + 2) {
         return "O-O";
      } else if (this.piece == 5 && this.toX == this.fromX - 2) {
         return "O-O-O";
      } else {
         String result = this.pieceLetter(this.piece) + (this.showFromX ? files[this.fromX] : "") + (this.showFromY ? ranks[this.fromY] : "") + (!this.showSeparator && !this.capture ? "" : (this.capture ? "x" : "-")) + files[this.toX] + ranks[this.toY];
         if (this.piece == 0 && (this.toY == 0 || this.toY == 7)) {
            result = result + "=" + this.pieceLetter(this.promotionPiece);
         }

         if (this.check && !this.checkmate) {
            result = result + "+";
         }

         if (this.checkmate) {
            result = result + "#";
         }

         return result;
      }
   }

   public String sourceSquare() {
      return files[this.fromX] + ranks[this.fromY];
   }

   public String destinationSquare() {
      return files[this.toX] + ranks[this.toY];
   }

   public static String squareString(int x, int y) {
      return files[x] + ranks[y];
   }

   public static boolean pieceCompatible(int piece, int checkCode) {
      switch(checkCode) {
      case 1:
      case 2:
         if (piece != 4 && piece != 3) {
            return false;
         }

         return true;
      case 3:
      case 4:
         if (piece != 4 && piece != 2 && piece != 0) {
            return false;
         }

         return true;
      case 5:
         if (piece == 1) {
            return true;
         }

         return false;
      default:
         return false;
      }
   }

   public boolean equals(Object o) {
      if (o.getClass() != this.getClass()) {
         return false;
      } else {
         Move m = (Move)o;
         return m.fromX == this.fromX && m.fromY == this.fromY && m.toX == this.toX && m.toY == this.toY && m.piece == this.piece;
      }
   }

   public int hashCode() {
      return (this.toX << 3) + this.toY | (this.fromX << 3) + this.fromY << 6 | this.piece << 12;
   }
}
