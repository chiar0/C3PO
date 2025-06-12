package core;

import java.io.File;
import java.io.FileWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class EvaluationTree {
   private static EvaluationTree.ChildSorter cs = new EvaluationTree.ChildSorter();
   public Metaposition sc = null;
   public Move m = null;
   public Move bestChild = null;
   public float value;
   public float minvalue;
   public float staticvalue;
   Vector children = new Vector();
   public static File associatedFile = null;
   private static FileWriter writer = null;

   public EvaluationTree() {
      if (associatedFile == null) {
         associatedFile = new File(Globals.PGNPath + "/tree.log");

         try {
            writer = new FileWriter(associatedFile, true);
         } catch (Exception var2) {
            var2.printStackTrace();
            return;
         }
      }

   }

   public void saveToFile() {
      if (writer != null) {
         try {
            writer.write(this.toString());
         } catch (Exception var2) {
            var2.printStackTrace();
         }

      }
   }

   public int getChildNumber() {
      return this.children.size();
   }

   public EvaluationTree getChild(int k) {
      return k >= 0 && k < this.getChildNumber() ? (EvaluationTree)this.children.get(k) : null;
   }

   public EvaluationTree getChild(Move mov) {
      for(int k = 0; k < this.getChildNumber(); ++k) {
         EvaluationTree et = this.getChild(k);
         if (et.m.equals(mov)) {
            return et;
         }
      }

      return null;
   }

   public void addChild(EvaluationTree eval) {
      this.children.add(eval);
   }

   public void sortChildren(boolean recursively) {
      Collections.sort(this.children, cs);
      if (recursively) {
         for(int k = 0; k < this.getChildNumber(); ++k) {
            this.getChild(k).sortChildren(true);
         }
      }

   }

   public String toString() {
      return this.m != null ? this.m.toString() : "root";
   }

   public String getString(int depth) {
      String result = "";

      int k;
      for(k = 0; k < depth; ++k) {
         result = result + "-";
      }

      result = result + (this.m != null ? this.m.toString() : "null") + " ";
      result = result + "v=" + this.value + " min=" + this.minvalue + " st=" + this.staticvalue + "\n";

      for(k = 0; k < this.getChildNumber(); ++k) {
         result = result + this.getChild(k).getString(depth + 1);
      }

      return result;
   }

   public String getBestSequence() {
      return this.getBestSequence(0);
   }

   public String getBestSequence(int k) {
      String result = "";
      if (this.m != null) {
         result = result + this.m.toString() + ";";
      }

      if (this.bestChild != null) {
         EvaluationTree et = this.getChild(this.bestChild);
         if (et != null) {
            result = result + et.getBestSequence(k + 1);
         }
      }

      return result;
   }

   public String getExtendedRepresentation() {
      String result = "";
      result = result + this.toString() + "\n";
      result = result + "Value: " + this.value + "\n";
      result = result + "Static: " + this.staticvalue + "\n";
      return result;
   }

   public void addTreeModelListener() {
   }

   public Object getChild(Object parent, int index) {
      return ((EvaluationTree)parent).getChild(index);
   }

   public int getChildCount(Object parent) {
      return ((EvaluationTree)parent).getChildNumber();
   }

   public void removeChild(int k) {
      this.children.remove(k);
   }

   public int getIndexOfChild(Object parent, Object child) {
      for(int k = 0; k < this.getChildCount(parent); ++k) {
         if (this.getChild(parent, k) == child) {
            return k;
         }
      }

      return -1;
   }

   public Object getRoot() {
      return this;
   }

   public boolean isLeaf(Object node) {
      return this.getChildCount(node) == 0;
   }

   public void removeTreeModelListener() {
   }

   public void valueForPathChanged() {
   }

   public static class ChildSorter implements Comparator {
      public int compare(Object o1, Object o2) {
         EvaluationTree dp1 = (EvaluationTree)o1;
         EvaluationTree dp2 = (EvaluationTree)o2;
         float v1 = dp1.value + dp1.minvalue;
         float v2 = dp2.value + dp2.minvalue;
         if (v2 > v1) {
            return 1;
         } else {
            return v2 < v1 ? -1 : 0;
         }
      }

      public boolean equals(Object obj) {
         return false;
      }
   }
}
