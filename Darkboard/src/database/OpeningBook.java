package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

public class OpeningBook implements Serializable {
   Vector openings = new Vector();
   private static final long serialVersionUID = -8105882071227579591L;

   public static OpeningBook load(File f) {
      try {
         FileInputStream fis = new FileInputStream(f);
         ObjectInputStream ois = new ObjectInputStream(fis);
         OpeningBook ob = (OpeningBook)ois.readObject();
         ois.close();
         fis.close();
         return ob;
      } catch (Exception var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public void save(File f) {
      try {
         FileOutputStream fos = new FileOutputStream(f);
         ObjectOutputStream oos = new ObjectOutputStream(fos);
         oos.writeObject(this);
         oos.close();
         fos.close();
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   public int size() {
      return this.openings.size();
   }

   public Opening getOpening(int k) {
      return k >= 0 && k < this.size() ? (Opening)this.openings.get(k) : null;
   }

   public void addOpening(Opening o) {
      for(int k = 0; k < this.size(); ++k) {
         Opening t = this.getOpening(k);
         if (t.isOpeningSubset(o, true)) {
            return;
         }

         if (t.isOpeningSubset(o, false)) {
            this.openings.setElementAt(o, k);
            return;
         }
      }

      this.openings.add(o);
   }

   public String toString() {
      String result = "Openings in book: " + this.size() + "\n";

      for(int k = 0; k < this.size(); ++k) {
         result = result + "*************\n";
         result = result + this.getOpening(k).toString() + "\n";
      }

      return result;
   }
}
