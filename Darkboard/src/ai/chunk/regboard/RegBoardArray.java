package ai.chunk.regboard;

import ai.player.Darkboard;
import core.Metaposition;
import core.Move;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Vector;
import reader.MiniReader;

public class RegBoardArray {
   Vector<RegBoard> boards = new Vector();

   public RegBoardArray(String filepath) {
      try {
         FileInputStream fis = new FileInputStream(Darkboard.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("Darkboard.jar", "resources/") + filepath);
         MiniReader mr = new MiniReader(fis);
         MiniReader.ReaderTag rt = mr.parse();
         if (!rt.tag.equals("regboard-array")) {
            return;
         }

         for(int k = 0; k < rt.subtags.size(); ++k) {
            this.boards.add(new RegBoard((MiniReader.ReaderTag)rt.subtags.get(k)));
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }

   public RegBoardArray(InputStream is) {
      try {
         MiniReader mr = new MiniReader(is);
         MiniReader.ReaderTag rt = mr.parse();
         if (!rt.tag.equals("regboard-array")) {
            return;
         }

         for(int k = 0; k < rt.subtags.size(); ++k) {
            this.boards.add(new RegBoard((MiniReader.ReaderTag)rt.subtags.get(k)));
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public RegBoard getBoard(int k) {
      return (RegBoard)this.boards.get(k);
   }

   public int getBoardNumber() {
      return this.boards.size();
   }

   public String toString() {
      String s = "<regboard-array>\n";

      for(int k = 0; k < this.boards.size(); ++k) {
         s = s + ((RegBoard)this.boards.get(k)).toString();
      }

      s = s + "\n</regboard-array>";
      return s;
   }

   public Move getMoveSuggestion(Metaposition m, Vector<Move> bannedMoves) {
      RegBoard.RegBoardPOV pov = new RegBoard(1, 1).new RegBoardPOV(false, false, false, false, false);

      for(int k = 0; k < this.boards.size(); ++k) {
         RegBoard rb = (RegBoard)this.boards.get(k);
         int[] offset = rb.match2(m, pov);
         if (offset[0] >= 0) {
            for(int j = 0; j < rb.getMoveNumber(); ++j) {
               Move move = rb.getMove(m, j, offset, pov);
               boolean banned = false;

               for(int tr = 0; tr < bannedMoves.size(); ++tr) {
                  if (((Move)bannedMoves.get(tr)).equals(move)) {
                     banned = true;
                  }
               }

               if (!banned && move.fromX >= 0 && move.fromX < 8 && move.fromY >= 0 && move.fromY < 8 && move.toX >= 0 && move.toX < 8 && move.toY >= 0 && move.toY < 8) {
                  return move;
               }
            }
         }
      }

      return null;
   }

   public static void main(String[] args) {
      Darkboard.initialize(args[0]);
   }
}
