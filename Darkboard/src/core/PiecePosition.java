package core;

import java.util.Vector;

public class PiecePosition {
   boolean[] piece = new boolean[7];
   int age = 0;
   Vector possibleSquares = new Vector();
   private static PiecePosition.Square[][] squares = null;

   public PiecePosition() {
      this.clearPieces();
      if (squares == null) {
         squares = new PiecePosition.Square[8][8];

         for(byte k = 0; k < 8; ++k) {
            for(byte j = 0; j < 8; squares[k][j].y = j++) {
               squares[k][j] = new PiecePosition.Square();
               squares[k][j].x = k;
            }
         }
      }

   }

   public void setPiece(int p, boolean value) {
      if (p >= 0 && p < this.piece.length) {
         this.piece[p] = value;
      }
   }

   public int getSquareNumber() {
      return this.possibleSquares.size();
   }

   public PiecePosition.Square getSquare(int k) {
      return k >= 0 && k < this.possibleSquares.size() ? (PiecePosition.Square)this.possibleSquares.get(k) : null;
   }

   public boolean isThereSquare(byte x, byte y) {
      for(int k = 0; k < this.possibleSquares.size(); ++k) {
         PiecePosition.Square s = this.getSquare(k);
         if (s.x == x && s.y == y) {
            return true;
         }
      }

      return false;
   }

   public void addSquare(byte x, byte y) {
      if (x >= 0 && y >= 0 && x < 8 && y < 8) {
         if (!this.isThereSquare(x, y)) {
            this.possibleSquares.add(squares[x][y]);
         }
      }
   }

   public void clear() {
      this.possibleSquares.clear();
   }

   public void clearPieces() {
      for(int k = 0; k < 7; ++k) {
         this.piece[k] = false;
      }

   }

   public void doAge() {
      ++this.age;
   }

   public void setAge(int k) {
      this.age = k;
   }

   public int getAge() {
      return this.age;
   }

   public class Square {
      public byte x;
      public byte y;
   }
}
