package ai.mc;

import core.Move;

public class MCStateNode extends MCNode {
   public static double UCT_CONSTANT_C = 0.5D;
   public static double CONSERVATION_CONSTANT = 0.5D;
   public MCState state = null;
   public int activeChildren = 0;
   public boolean active = true;
   MCStateNode bestChild = null;
   public double bestChildValue = Double.NEGATIVE_INFINITY;

   public MCStateNode() {
      this.chanceNode = false;
   }

   public MCStateNode(MCState s) {
      this.chanceNode = false;
      this.state = s;
   }

   public MCNode select() {
      int best = -1;
      double bestval = Double.NEGATIVE_INFINITY;
      double log = Math.log(this.visits + 1.0D);
      double[] strategyBias = null;
      if (this.state != null && this.state.isBroken()) {
         return null;
      } else if (this.expanded && this.links.length != 0) {
         if (this.currentUpperStrategy != null) {
            strategyBias = this.currentUpperStrategy.strategy.progressInformation(this.state, this.links);
         }

         for(int k = 0; k < this.links.length; ++k) {
            MCNode n = this.links[k].child;
            double visits = n == null ? 1.0D : n.visits + 1.0D;
            double v = n == null ? this.value : n.value;
            double strat = strategyBias != null ? strategyBias[k] * 0.5D : 0.0D;
            double val = this.links[k].heuristicBias + v + strat + UCT_CONSTANT_C * Math.sqrt(log / visits);
            if (val > bestval) {
               bestval = val;
               best = k;
            }
         }

         MCNode bestNode = this.links[best].child;
         if (bestNode == null) {
            MCStateNode mcs = new MCStateNode();
            mcs.state = this.state.getState(0, this.links[best].move);
            this.links[best].setChild(mcs);
            bestNode = mcs;
         }

         if (!this.links[best].refined()) {
            this.links[best].refine();
         }

         boolean somethingElse = this.links[best].somethingElseHappens();
         if (somethingElse) {
            return this.links[best].child;
         } else {
            if (strategyBias != null && strategyBias[best] == 1.0D) {
               this.currentUpperStrategy.upPropagation((MCNode)bestNode, true, false, ((MCNode)bestNode).value);
               ((MCNode)bestNode).currentUpperStrategy = this.currentUpperStrategy.nextStrategy;
               ((MCNode)bestNode).beginningOfCurrentUpperStrategy = (MCNode)bestNode;
            } else {
               ((MCNode)bestNode).currentUpperStrategy = this.currentUpperStrategy;
               ((MCNode)bestNode).beginningOfCurrentUpperStrategy = this.beginningOfCurrentUpperStrategy;
            }

            return ((MCNode)bestNode).select();
         }
      } else {
         return this;
      }
   }

   public boolean needsExpansion() {
      return !this.expanded && this.ownvalue != Double.NEGATIVE_INFINITY;
   }

   public MCNode expand() {
      if (this.state == null) {
         return null;
      } else {
         Move[] mov = this.state.getMoves(0);
         this.links = new MCLink[mov.length];

         for(int k = 0; k < mov.length; ++k) {
            this.links[k] = new MCLink(this);
            this.links[k].setMove(mov[k]);
            this.links[k].heuristicBias = (double)this.state.getHeuristicBias(0, mov[k]);
         }

         this.expanded = true;
         return this.select();
      }
   }

   public double simulate() {
      if (this.state == null) {
         return 0.0D;
      } else {
         this.ownvalue = (double)this.state.eval(0);
         return this.ownvalue;
      }
   }

   public void backpropagate(double d, double weight) {
      double vis = this.visits > 0.0D ? this.visits + 20.0D : this.visits;
      this.value = (this.value * vis + d * weight) / (vis + weight);
      ++this.visits;
      if (this.parent != null) {
         this.parent.backpropagate(d, weight * 0.5D);
      }

   }

   public Move findMostVisited() {
      Move m = null;
      double visits = 0.0D;
      if (this.links == null) {
         return null;
      } else {
         for(int k = 0; k < this.links.length; ++k) {
            if (this.links[k].child != null && this.links[k].child.visits > visits) {
               visits = this.links[k].child.visits;
               m = this.links[k].move;
            }
         }

         return m;
      }
   }

   public MCNode select2() {
      if (!this.expanded) {
         return this;
      } else if (!this.active) {
         return null;
      } else {
         int best = -1;
         double bestval = Double.NEGATIVE_INFINITY;
         double log = Math.log(this.visits + 1.0D);

         for(int k = 0; k < this.links.length; ++k) {
            if (this.links[k] != null) {
               MCNode n = this.links[k].child;
               if (n != null) {
                  double visits = n.visits + 1.0D;
                  double v = n.value;
                  double val = this.links[k].heuristicBias + v + UCT_CONSTANT_C * Math.sqrt(log / visits);
                  if (val > bestval) {
                     bestval = val;
                     best = k;
                  }
               }
            }
         }

         if (best != -1) {
            return this.links[best].child.select();
         } else {
            return null;
         }
      }
   }

   public void expand2() {
      if (!this.expanded && this.active && this.state != null) {
         this.expanded = true;
         Move[] moves = this.state.getMoves(0);
         this.links = new MCLink[moves.length];

         for(int k = 0; k < this.links.length; ++k) {
            this.links[k] = new MCLink(this);
            this.links[k].setMove(moves[k]);
            MCStateNode mcs = new MCStateNode();
            mcs.state = this.state.getState(0, moves[k]);
            this.links[k].setChild(mcs);
            this.links[k].refine();
            mcs.ownvalue = (double)mcs.state.eval(0);
            mcs.value = mcs.ownvalue;
            if (mcs.ownvalue > this.bestChildValue) {
               this.bestChild = mcs;
               this.bestChildValue = mcs.ownvalue;
            }
         }

         if (this.bestChild != null) {
            this.determineValue();
            if (this.parent != null) {
               ((MCStateNode)this.parent).recalculate(this);
            }
         }

         this.backprop2();
      }
   }

   public void determineValue() {
      double nonSilent = this.parentLink != null ? 1.0D - this.parentLink.silentChance : 0.0D;
      double best = this.bestChild != null ? (1.0D - nonSilent) * (1.0D - CONSERVATION_CONSTANT) : 0.0D;
      double currentNode = 1.0D - nonSilent - best;
      double nonSilValue = this.parentLink != null ? this.parentLink.theRestValue : 0.0D;
      double bestVal = this.bestChild != null ? this.bestChildValue : 0.0D;
      this.value = nonSilent * nonSilValue + best * bestVal + currentNode * this.ownvalue;
   }

   public void recalculate(MCNode child) {
      boolean propagate = true;
      if (child != null) {
         if (child == this.bestChild) {
            if (child.value < this.bestChildValue) {
               this.bestChildValue = child.value;

               for(int k = 0; k < this.links.length; ++k) {
                  if (this.links[k] != null && this.links[k].child != null && this.links[k].child.value > this.bestChildValue) {
                     this.bestChild = (MCStateNode)this.links[k].child;
                     this.bestChildValue = this.links[k].child.value;
                  }
               }
            }
         } else if (child.value > this.bestChildValue) {
            this.bestChild = (MCStateNode)child;
            this.bestChildValue = child.value;
         } else {
            propagate = false;
         }

         this.determineValue();
         if (propagate && this.parent != null) {
            ((MCStateNode)this.parent).recalculate(this);
         }

      }
   }

   public void backprop2() {
      ++this.visits;
      if (this.parent != null) {
         ((MCStateNode)this.parent).backprop2();
      }

   }
}
