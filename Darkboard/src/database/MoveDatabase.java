package database;

import core.Move;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class MoveDatabase {
   Vector moveData = new Vector();
   public int gameSize = 0;
   public static MoveDatabase.MoveComparator mc;

   public int getMoveNumber() {
      return this.moveData.size();
   }

   public Move getMove(int index) {
      return index >= 0 && index < this.getMoveNumber() ? this.getData(index).m : null;
   }

   public boolean isThereMove(Move m) {
      for(int k = 0; k < this.moveData.size(); ++k) {
         if (this.getMove(k).equals(m)) {
            return true;
         }
      }

      return false;
   }

   public void addMove(Move mv) {
      if (!this.isThereMove(mv)) {
         MoveDatabase.MoveData md = new MoveDatabase.MoveData();
         md.m = mv;
         md.owner = this;
         this.moveData.add(md);
      }

   }

   public MoveDatabase.MoveData getData(int index) {
      return (MoveDatabase.MoveData)this.moveData.get(index);
   }

   public MoveDatabase.MoveData getData(Move m) {
      for(int k = 0; k < this.moveData.size(); ++k) {
         if (this.getMove(k).equals(m)) {
            return this.getData(k);
         }
      }

      return null;
   }

   public void sortDatabase() {
      Collections.sort(this.moveData, mc);
   }

   public MoveDatabase() {
      if (mc == null) {
         mc = new MoveDatabase.MoveComparator();
      }

   }

   public String toString() {
      String s = "";

      for(int k = 0; k < this.moveData.size(); ++k) {
         s = s + this.getData(k).toString() + "\n";
      }

      return s;
   }

   public class MoveComparator implements Comparator, Serializable {
      public int compare(Object o1, Object o2) {
         MoveDatabase.MoveData dp1 = (MoveDatabase.MoveData)o1;
         MoveDatabase.MoveData dp2 = (MoveDatabase.MoveData)o2;
         if (dp1.count != dp2.count) {
            return dp2.count - dp1.count;
         } else {
            return dp1.avgAppearance != dp2.avgAppearance ? (int)(dp1.avgAppearance - dp2.avgAppearance) : 0;
         }
      }

      public boolean equals(Object obj) {
         return false;
      }
   }

   public class MoveData implements Serializable {
      public Move m;
      public int count = 0;
      public double avgAppearance = 0.0D;
      public MoveDatabase owner;

      public void addCount() {
         ++this.count;
      }

      public void addCount(int moveCount) {
         this.avgAppearance = (this.avgAppearance * (double)this.count + (double)moveCount) / (double)(this.count + 1);
         ++this.count;
      }

      public String toString() {
         String perc = this.owner != null && this.owner.gameSize != 0 ? 100.0D * (double)this.count / (double)this.owner.gameSize + "%" : "";
         return (this.m != null ? this.m.toString() : "") + " x" + this.count + " (" + this.avgAppearance + " avg) " + perc;
      }
   }
}
