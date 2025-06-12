package ai.tools.search;

import core.Move;
import java.util.Vector;

public class Path {
   public Vector<Move> moves = new Vector();

   public Path() {
   }

   public Path(Move m) {
      if (m != null) {
         this.moves.add(m);
      }

   }

   public Path(Path p, Move m) {
      for(int k = 0; k < p.moves.size(); ++k) {
         this.moves.add((Move)p.moves.get(k));
      }

      if (m != null) {
         this.moves.add(m);
      }

   }

   public Path(Path p) {
      for(int k = 0; k < p.moves.size(); ++k) {
         this.moves.add((Move)p.moves.get(k));
      }

   }

   public Path(Path p1, Path p2) {
      int k;
      for(k = 0; k < p1.moves.size(); ++k) {
         this.moves.add((Move)p1.moves.get(k));
      }

      for(k = 0; k < p2.moves.size(); ++k) {
         this.moves.add((Move)p2.moves.get(k));
      }

   }

   public void copy(Path p) {
      this.moves.clear();

      for(int k = 0; k < p.moves.size(); ++k) {
         this.moves.add((Move)p.moves.get(k));
      }

   }

   public String toString() {
      String s = "";

      for(int k = 0; k < this.moves.size(); ++k) {
         s = s + this.moves.get(k) + "; ";
      }

      return s;
   }
}
