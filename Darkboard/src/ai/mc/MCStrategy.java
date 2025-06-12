package ai.mc;

public abstract class MCStrategy {
   public String name;
   public int[] parameters = new int[4];

   public abstract MCStrategy[] continuations(MCStrategy[] var1);

   public abstract double[] progressInformation(MCState var1, MCLink[] var2);

   public String toString() {
      return this.name + " " + this.parameters[0] + " " + this.parameters[1] + " " + this.parameters[2] + " " + this.parameters[3];
   }
}
