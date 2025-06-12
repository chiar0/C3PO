package ai.tools.search;

import core.Chessboard;
import core.Metaposition;
import java.util.Vector;

public class PiecePathfinder {
   public static int[][] matrix = new int[8][8];
   public static PiecePathfinder.PathfindingOptions defaultOptions;

   public static void init() {
      for(int k = 0; k < 8; ++k) {
         for(int j = 0; j < 8; ++j) {
            matrix[k][j] = 1000;
         }
      }

   }

   protected static boolean iterate(Metaposition m, int x, int y, int[] movementX, int[] movementY, boolean once, int level) {
      boolean progress = false;
      int dirs = movementX.length;
      int x1 = level == 0 ? x : 0;
      int y1 = level == 0 ? y : 0;
      int x2 = level == 0 ? x + 1 : 8;
      int y2 = level == 0 ? y + 1 : 8;

      for(int k = x1; k < x2; ++k) {
         for(int j = y1; j < y2; ++j) {
            if (matrix[k][j] == level) {
               for(int dir = 0; dir < dirs; ++dir) {
                  int dx = movementX[dir];
                  int dy = movementY[dir];
                  int tx = k + dx;
                  int ty = j + dy;

                  while(tx >= 0 && ty >= 0 && tx < 8 && ty < 8 && matrix[tx][ty] == 1000 && m.mayBeEmpty(tx, ty)) {
                     matrix[tx][ty] = level + 1;
                     progress = true;
                     tx += dx;
                     ty += dy;
                     if (once) {
                        break;
                     }
                  }
               }
            }
         }
      }

      return progress;
   }

   public static int pathfind(Metaposition m, int x, int y, int x2, int y2, int maxLevel) {
      int[][] board = new int[8][8];

      for(int k = 0; k < 8; ++k) {
         for(int j = 0; j < 8; ++j) {
            matrix[k][j] = 1000;
         }
      }

      return 0;
   }

   public static int distance(Metaposition m, int x, int y, Vector<int[]> destinations, int maxLevel, PiecePathfinder.PathfindingOptions opt) {
      for(int k = 0; k < 8; ++k) {
         for(int j = 0; j < 8; ++j) {
            matrix[k][j] = 1000;
         }
      }

      boolean target = opt != null && opt.targetX >= 0;
      int piece = m.getFriendlyPiece(x, y);
      if (piece == 6) {
         return 0;
      } else {
         int[] mx = Chessboard.getPieceMovementVectorX(piece, m.isWhite());
         int[] my = Chessboard.getPieceMovementVectorY(piece, m.isWhite());
         boolean once = piece == 5 || piece == 1 || piece == 0;

         int level;
         for(level = 0; level < destinations.size(); ++level) {
            matrix[((int[])destinations.get(level))[0]][((int[])destinations.get(level))[1]] = -1000;
         }

         if (matrix[x][y] == -1000) {
            return 0;
         } else {
            matrix[x][y] = 0;

            for(level = 0; level < maxLevel; ++level) {
               for(int k = 0; k < 8; ++k) {
                  for(int j = 0; j < 8; ++j) {
                     if (matrix[k][j] == level) {
                        for(int dir = 0; dir < mx.length; ++dir) {
                           int heuristicLevel = level + 1;
                           int dx = mx[dir];
                           int dy = my[dir];
                           boolean pawnBonus = piece == 0 && j == m.getSecondRank();
                           int x1 = k + dx;
                           int y1 = j + dy;
                           if (x1 >= 0 && x1 < 8 && y1 >= 0 && y1 < 8) {
                              do {
                                 int pc = m.getFriendlyPiece(x1, y1);
                                 if (pc != 6) {
                                    if (target && !once) {
                                       if ((dx != 0 || dy == 0 || opt.targetX != k || pc != 3 && pc != 4) && (dx == 0 || dy != 0 || opt.targetY != j || pc != 3 && pc != 4) && (dx * dy != 1 || opt.targetX - opt.targetY != k - j || pc != 2 && pc != 4) && (dx * dy != -1 || opt.targetX + opt.targetY != k + j || pc != 2 && pc != 4)) {
                                          ++heuristicLevel;
                                       }
                                    } else if (dx == 0 && pc == 0) {
                                       heuristicLevel += 8;
                                    } else {
                                       ++heuristicLevel;
                                    }
                                 }

                                 if (matrix[x1][y1] == -1000) {
                                    if (target && heuristicLevel == 1) {
                                       return 0;
                                    }

                                    return heuristicLevel;
                                 }

                                 if (matrix[x1][y1] <= heuristicLevel) {
                                    break;
                                 }

                                 matrix[x1][y1] = heuristicLevel;
                                 if (once) {
                                    if (!pawnBonus) {
                                       break;
                                    }

                                    pawnBonus = false;
                                 }

                                 x1 += dx;
                                 y1 += dy;
                              } while(heuristicLevel == level + 1 && x1 >= 0 && y1 >= 0 && x1 < 8 && y1 < 8);
                           }
                        }
                     }
                  }
               }
            }

            return -1;
         }
      }
   }

   public static int distance(Metaposition m, int x, int y, Vector<int[]> destinations, int maxLevel) {
      return distance(m, x, y, destinations, maxLevel, (PiecePathfinder.PathfindingOptions)null);
   }

   public static int distance(Metaposition m, int x, int y, Vector<int[]> destinations, int maxLevel, int targx, int targy) {
      if (defaultOptions == null) {
         defaultOptions = new PiecePathfinder().new PathfindingOptions();
      }

      defaultOptions.targetX = targx;
      defaultOptions.targetY = targy;
      return distance(m, x, y, destinations, maxLevel, defaultOptions);
   }

   public class PathfindingOptions {
      int targetX = -1;
      int targetY = -1;
   }
}
