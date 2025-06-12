package ai.mc;

import core.Move;

public abstract class MCNode {
   MCNode parent;
   MCLink parentLink;
   boolean chanceNode;
   double value = 0.0D;
   double ownvalue = Double.NEGATIVE_INFINITY;
   double visits = 0.0D;
   boolean expanded = false;
   MCLink[] links;
   MCStrategyNode currentUpperStrategy;
   MCNode beginningOfCurrentUpperStrategy;
   MCStrategyNode nextStrategy;

   public MCNode select() {
      return null;
   }

   public boolean needsExpansion() {
      return false;
   }

   public MCNode expand() {
      return null;
   }

   public double simulate() {
      return 0.0D;
   }

   public void backpropagate(double d, double weight) {
   }

   public Move findMostVisited() {
      return null;
   }

   public MCNode findRoot() {
      return this.parent == null ? this : this.parent.findRoot();
   }

   public MCNode findMostVisitedChild() {
      MCNode m = null;
      double visits = 0.0D;
      if (this.links == null) {
         return null;
      } else {
         for(int k = 0; k < this.links.length; ++k) {
            if (this.links[k].child != null && this.links[k].child.visits > visits) {
               visits = this.links[k].child.visits;
               m = this.links[k].child;
            }
         }

         return m;
      }
   }
}
