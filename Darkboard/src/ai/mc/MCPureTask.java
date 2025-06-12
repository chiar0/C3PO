package ai.mc;

import java.util.concurrent.Callable;

public class MCPureTask implements Callable<Object>, Runnable {
   MCGameSimulator sim = new MCGameSimulator();
   int set;
   int depth;

   public MCPureTask(int s, int d) {
      this.set = s;
      this.depth = d;
   }

   public Object call() throws Exception {
      this.run();
      return null;
   }

   public void run() {
      this.sim.init(MCSTSNode.roots[this.set - 1].state);

      while(!MCSTSNode.stop) {
         MCSTSNode n = MCSTSNode.roots[this.set - 1].pureMCSelect(this.sim, this.set, this.depth);
         if (n != null && n.parent != null) {
            n.pureMCEval(this.sim, this.set, this.depth);
         }
      }

   }
}
