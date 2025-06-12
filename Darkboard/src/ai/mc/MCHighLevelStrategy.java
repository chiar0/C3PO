package ai.mc;

public class MCHighLevelStrategy extends MCStrategy {
   protected MCStrategy[] messages;
   protected String[] stratStrings;
   protected int[][] transitions;
   protected MCStrategy[][] continuations;

   public MCHighLevelStrategy(String n, int p0, int p1, int p2, int p3) {
      this.name = n;
      this.parameters[0] = p0;
      this.parameters[1] = p1;
      this.parameters[2] = p2;
      this.parameters[3] = p3;
      this.init();
   }

   protected void init() {
      this.messages = new MCStrategy[0];
      this.transitions = new int[0][0];
      this.continuations = new MCStrategy[0][];
      this.stratStrings = new String[0];

      for(int k = 0; k < this.stratStrings.length; ++k) {
         this.stratStrings[k] = this.messages[k].toString();
      }

   }

   public MCStrategy[] continuations(MCStrategy[] sofar) {
      int state = 0;

      for(int k = 0; k < sofar.length; ++k) {
         MCStrategy st = sofar[k];
         if (st != null) {
            int nu = this.strategyToNumber(st);
            if (nu < 0) {
               return null;
            }

            state = this.transitions[state][nu];
            if (state < 0) {
               return null;
            }
         }
      }

      MCStrategy[] out = new MCStrategy[this.continuations[state].length];
      System.arraycopy(this.continuations[state], 0, out, 0, out.length);
      return out;
   }

   protected int strategyToNumber(MCStrategy s) {
      String st = s.toString();

      for(int k = 0; k < this.stratStrings.length; ++k) {
         if (st.equals(this.stratStrings[k])) {
            return k;
         }
      }

      return -1;
   }

   public double[] progressInformation(MCState m, MCLink[] moves) {
      return null;
   }

   public boolean equals(Object o) {
      return this == o ? true : this.toString().equals(o.toString());
   }
}
