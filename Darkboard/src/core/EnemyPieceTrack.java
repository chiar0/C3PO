package core;

public class EnemyPieceTrack {
   public byte x1;
   public byte y1;
   public byte x2;
   public byte y2;
   public byte dx;
   public byte dy;

   public EnemyPieceTrack(Move illegal) {
      this.dx = (byte)(illegal.toX > illegal.fromX ? 1 : (illegal.toX == illegal.fromX ? 0 : -1));
      this.dy = (byte)(illegal.toY > illegal.fromY ? 1 : (illegal.toY == illegal.fromY ? 0 : -1));
      this.x1 = (byte)(illegal.fromX + this.dx);
      this.x2 = (byte)(illegal.toX - this.dx);
      this.y1 = (byte)(illegal.fromY + this.dy);
      this.y2 = (byte)(illegal.toY - this.dy);
   }

   public int getSquareNumber() {
      int x = this.x2 - this.x1;
      if (x < 0) {
         x *= -1;
      }

      int y = this.y2 - this.y1;
      if (y < 0) {
         y *= -1;
      }

      int result = (x > y ? x : y) + 1;
      return result;
   }

   public int getSquareX(int index) {
      return this.x1 + this.dx * index;
   }

   public int getSquareY(int index) {
      return this.y1 + this.dy * index;
   }

   public boolean containsSquare(int x, int y) {
      for(int index = 0; index < this.getSquareNumber(); ++index) {
         if (x == this.getSquareX(index) && y == this.getSquareY(index)) {
            return true;
         }
      }

      return false;
   }

   public boolean isSubsetOf(EnemyPieceTrack ept) {
      for(int k = 0; k < this.getSquareNumber(); ++k) {
         if (!ept.containsSquare(this.getSquareX(k), this.getSquareY(k))) {
            return false;
         }
      }

      return true;
   }

   public boolean isSupersetOf(EnemyPieceTrack ept) {
      return ept.isSubsetOf(this);
   }
}
