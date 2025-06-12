package core;

import java.util.Vector;

public class ExtendedEvaluationTree extends EvaluationTree {
   Vector componentVector = new Vector();
   public EvaluationFunction func = null;
   public Vector history = null;

   public void addInformation(Object a) {
      this.componentVector.add(a);
   }

   public int componentNumber() {
      return this.componentVector.size();
   }

   public Object getComponent(int k) {
      return this.componentVector.get(k);
   }

   public String getExtendedRepresentation() {
      String result = "";
      int k;
      if (this.func != null) {
         for(k = 0; k < this.componentNumber(); ++k) {
            EvaluationFunctionComponent efc = this.func.getComponent(k);
            if (efc == null) {
               break;
            }

            String name = efc.name;
            String value = this.getComponent(k).toString();
            result = result + name + ": " + value + "\n";
         }
      }

      if (this.history != null) {
         result = result + "History: ";

         for(k = 0; k < this.history.size(); ++k) {
            result = result + this.history.get(k) + " ";
         }

         result = result + "\n";
      }

      return result;
   }
}
