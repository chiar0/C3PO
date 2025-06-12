package core.eval;

import core.EvaluationFunctionComponent;
import core.Metaposition;
import core.Move;
import java.util.Vector;

public class KRKMainComponent extends EvaluationFunctionComponent {
   private static int[][] matrix = new int[][]{{1, 1, 0, 0, 0, 0, 1, 1}, {1, 0, 0, 0, 0, 0, 0, 1}, {0, 0, -2, -4, -4, -2, 0, 0}, {0, 0, -4, -4, -4, -4, 0, 0}, {0, 0, -4, -4, -4, -4, 0, 0}, {0, 0, -2, -4, -4, -2, 0, 0}, {1, 0, 0, 0, 0, 0, 0, 1}, {1, 1, 0, 0, 0, 0, 1, 1}};

   public float evaluate(Metaposition start, Metaposition dest, Move m, Vector history) {
      float result = 0.0F;
      int rook = dest.getSquareWithPiece(3);
      int rx = rook / 8;
      int ry = rook % 8;
      int king = dest.getSquareWithPiece(5);
      int kx = king / 8;
      int ky = king % 8;
      int mateDetector = stalemateAlert(start, m, dest);
      if (mateDetector < 0) {
         return (float)(mateDetector * 10000);
      } else {
         Metaposition meta = Metaposition.evolveAfterOpponentMove(dest, -1, -1, 0, 0, 0);
         int f1 = this.rookUnderAttack(dest, rx, ry);
         int f2 = this.evalKingDistance(meta, kx, ky);
         int f3 = this.evalAreas(meta, rx, ry, kx, ky);
         int f4 = this.adjacentRook(rx, ry, kx, ky);
         int f5 = this.kingMatrix(meta);
         result = (float)(-420 + f1 * 840 - f2 - f3 + f4 + f5);
         if (mateDetector > 0 && f1 > 0) {
            result += 10000.0F;
         }

         if (this.canBeIllegal(start, dest, m) && history.size() > 0) {
            result -= 10.0F;
            m.checkmate = true;
         }

         for(int ind = 0; ind < history.size(); ++ind) {
            Move h = (Move)history.get(ind);
            if (h.checkmate) {
               result -= 10.0F;
            }
         }

         return result;
      }
   }

   private int rookUnderAttack(Metaposition m, int rookx, int rooky) {
      if (m.owner.globals.protectionMatrix[rookx][rooky] > 0) {
         return 1;
      } else {
         for(int k = rookx - 1; k <= rookx + 1; ++k) {
            for(int j = rooky - 1; j <= rooky + 1; ++j) {
               if (k >= 0 && k <= 7 && j >= 0 && j <= 7 && (k != rookx || j != rooky) && m.canContain((byte)k, (byte)j, (byte)5)) {
                  return 0;
               }
            }
         }

         return 1;
      }
   }

   private int evalKingDistance(Metaposition m, int kingx, int kingy) {
      int maxDistance = 0;

      for(int k = 0; k < 8; ++k) {
         for(int j = 0; j < 8; ++j) {
            if (m.canContainEnemyKing((byte)k, (byte)j)) {
               int distx = k - kingx;
               int disty = j - kingy;
               if (distx < 0) {
                  distx = -distx;
               }

               if (disty < 0) {
                  disty = -disty;
               }

               int dist = distx + disty;
               if (dist > maxDistance) {
                  maxDistance = dist;
               }
            }
         }
      }

      return maxDistance;
   }

   private int evalAreas(Metaposition m, int rx, int ry, int kx, int ky) {
      int areas = 0;
      int squares = 0;
      boolean[] quad = new boolean[4];

      for(int k = 0; k < 4; ++k) {
         boolean found = false;
         int x1;
         int x2;
         if (k < 2) {
            x1 = 0;
            x2 = rx - 1;
         } else {
            x1 = rx + 1;
            x2 = 7;
         }

         int y1;
         int y2;
         if (k % 2 == 0) {
            y1 = 0;
            y2 = ry - 1;
         } else {
            y1 = ry + 1;
            y2 = 7;
         }

         if (rx == 0 || ry == 0 || rx == 7 || ry == 7) {
            found = true;
         }

         for(int x = x1; x <= x2; ++x) {
            for(int y = y1; y <= y2; ++y) {
               if (m.canContainEnemyKing((byte)x, (byte)y)) {
                  found = true;
                  quad[k] = true;
                  ++squares;
               }
            }
         }

         if (found) {
            ++areas;
         }
      }

      int bonus = 0;
      return areas * squares + bonus;
   }

   private int adjacentRook(int rx, int ry, int kx, int ky) {
      int dx = kx - rx;
      int dy = ky - ry;
      if (dx < 0) {
         dx = -dx;
      }

      if (dy < 0) {
         dy = -dy;
      }

      int dist = dx > dy ? dx : dy;
      return dist == 1 ? 10 : 0;
   }

   private int kingMatrix(Metaposition m) {
      int total = 0;

      for(int k = 0; k < 8; ++k) {
         for(int j = 0; j < 8; ++j) {
            if (m.canContainEnemyKing((byte)k, (byte)j)) {
               total += matrix[k][j];
            }
         }
      }

      return total;
   }

   public static int stalemateAlert(Metaposition start, Move m, Metaposition dest) {
      boolean[][] targets = new boolean[8][8];
      boolean definiteCheckmate = true;

      for(byte x = 0; x < 8; ++x) {
         for(byte y = 0; y < 8; ++y) {
            if (dest.canContainEnemyKing(x, y)) {
               boolean escapeRoute = false;

               for(byte x2 = (byte)(x - 1); x2 <= x + 1; ++x2) {
                  for(byte y2 = (byte)(y - 1); y2 <= y + 1; ++y2) {
                     if (x2 >= 0 && x2 <= 7 && y2 >= 0 && y2 <= 7 && (x2 != x || y2 != y) && start.owner.globals.protectionMatrix[x2][y2] < 1) {
                        escapeRoute = true;
                     }
                  }
               }

               if (!escapeRoute) {
                  if (start.owner.globals.protectionMatrix[x][y] < 1) {
                     return -1;
                  }
               } else {
                  definiteCheckmate = false;
               }
            }
         }
      }

      if (definiteCheckmate) {
         return 1;
      } else {
         return 0;
      }
   }

   public boolean canBeIllegal(Metaposition s, Metaposition d, Move m) {
      if (m.piece != 5) {
         return false;
      } else {
         for(int x = m.toX - 1; x <= m.toX + 1; ++x) {
            for(int y = m.toY - 1; y <= m.toY + 1; ++y) {
               if (x >= 0 && y >= 0 && x < 8 && y < 8 && s.canContainEnemyKing((byte)x, (byte)y)) {
                  return true;
               }
            }
         }

         return false;
      }
   }
}
