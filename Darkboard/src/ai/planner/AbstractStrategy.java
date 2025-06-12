package ai.planner;

import ai.chunk.regboard.RegBoardArray;
import ai.player.Darkboard;
import ai.tools.search.Path;
import core.Metaposition;
import core.Move;
import java.util.StringTokenizer;
import java.util.Vector;
import reader.MiniReader;

public class AbstractStrategy {
   boolean[] acceptablePiece = new boolean[7];
   boolean aimed;
   Area sourceArea;
   Area destinationArea;
   int maxMoves;

   public AbstractStrategy(MiniReader.ReaderTag tags, RegBoardArray boards) {
      if (tags.tag.equals("plan")) {
         String s = ((MiniReader.ReaderTag)tags.subtags.get(0)).value;
         StringTokenizer st = new StringTokenizer(s);

         while(true) {
            String t1;
            do {
               if (st.countTokens() <= 0) {
                  return;
               }

               t1 = st.nextToken();
            } while(!t1.equals("FROM"));

            int from = Integer.parseInt(st.nextToken());
            if (!st.nextToken().equals("TO")) {
               return;
            }

            int to = Integer.parseInt(st.nextToken());
            String allowed = st.nextToken();
            this.sourceArea = new RegBoardArea(boards.getBoard(from));
            this.destinationArea = new RegBoardArea(boards.getBoard(to));

            for(int k = 0; k < 7; ++k) {
               this.acceptablePiece[k] = false;
            }

            if (allowed.indexOf(80) != -1) {
               this.acceptablePiece[0] = true;
            }

            if (allowed.indexOf(78) != -1) {
               this.acceptablePiece[1] = true;
            }

            if (allowed.indexOf(66) != -1) {
               this.acceptablePiece[2] = true;
            }

            if (allowed.indexOf(82) != -1) {
               this.acceptablePiece[3] = true;
            }

            if (allowed.indexOf(81) != -1) {
               this.acceptablePiece[4] = true;
            }

            if (allowed.indexOf(75) != -1) {
               this.acceptablePiece[5] = true;
            }

            if (allowed.indexOf(45) != -1) {
               this.acceptablePiece[6] = true;
            }
         }
      }
   }

   public Strategy makeStrategy(Darkboard db, Vector<int[]> constraints, Metaposition m, Path outPath) {
      Vector<int[]> s = this.sourceArea.getAreaSquares(m);
      Vector<int[]> d = this.destinationArea.getAreaSquares(m);
      int bestwhat = 100000000;
      int bestx = -1;
      int besty = -1;
      Strategy bestStrategy = null;
      Path bestPath = null;

      int k;
      int[] a;
      for(k = 0; k < s.size(); ++k) {
         a = (int[])s.get(k);
         int piece = m.getFriendlyPiece(a[0], a[1]);
         if (this.acceptablePiece[piece]) {
            Strategy concrete = new Strategy(db);
            concrete.setStartingContext(m);
            Strategy.FlightPlan fp = concrete.new FlightPlan(a[0], a[1], d);
            fp.aimed = this.aimed;
            concrete.orders.add(fp);
            int dist = concrete.distance(new Vector(), m, (Move)null, m);
            if (dist <= this.maxMoves) {
               if (constraints != null) {
                  for(int j = 0; j < constraints.size(); ++j) {
                     int[] con = (int[])constraints.get(j);
                     if (con[0] != a[0] || con[1] != a[1]) {
                        Strategy.FlightPlan fp2 = concrete.new FlightPlan(con[0], con[1], con[0], con[1]);
                        concrete.orders.add(fp2);
                     }
                  }
               }

               Path p = concrete.getBestPath(m);
               if (p != null && dist < bestwhat) {
                  bestStrategy = concrete;
                  bestwhat = dist;
                  bestPath = p;
                  bestx = a[0];
                  besty = a[1];
               }
            }
         }
      }

      if (bestStrategy != null && constraints != null) {
         for(k = 0; k < constraints.size(); ++k) {
            a = (int[])constraints.get(k);
            if (a[0] == bestx && a[1] == besty) {
               constraints.remove(a);
               break;
            }
         }

         int[] cn = new int[]{bestx, besty};
         constraints.add(cn);
      }

      if (outPath != null && bestPath != null) {
         outPath.copy(bestPath);
      }

      return bestStrategy;
   }
}
