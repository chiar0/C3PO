package ai.mc;

import core.uberposition.ZobristHash;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

public class MCSTSTask implements Callable<Object>, Runnable {
   public static int MAX_PLANS = 64;
   private static int[] planOccurrences;
   private static float[] planValues;
   private static float[] planMaxExpectedValues;
   private static ZobristHash<float[]>[] planTable;
   private static MCSTSNode[] planBestFits;
   private static float[] planBestFitness;
   private static int totalOccurrences;
   private static ReentrantLock sharedWriteLock;
   private static Vector<Short> nextMoves;
   private static float guaranteedFitness;
   private static int movesLeft;
   int setNumber;
   int time;
   float counter = 0.0F;
   Random r = new Random();
   public static Random r2;

   static {
      planOccurrences = new int[MAX_PLANS];
      planValues = new float[MAX_PLANS];
      planMaxExpectedValues = new float[MAX_PLANS];
      planBestFits = new MCSTSNode[MAX_PLANS];
      planBestFitness = new float[MAX_PLANS];
      totalOccurrences = 0;
      sharedWriteLock = new ReentrantLock();
      nextMoves = new Vector();
      guaranteedFitness = Float.NEGATIVE_INFINITY;
      movesLeft = 0;
      r2 = new Random();
   }

   public MCSTSTask(int id, int t) {
      this.setNumber = id;
      this.time = t;
   }

   public static void flushTables() {
      if (planTable == null) {
         planTable = new ZobristHash[MAX_PLANS];
      }

      for(int k = 0; k < MAX_PLANS; ++k) {
         if (planTable[k] == null) {
            planTable[k] = new ZobristHash(8);
         } else {
            planTable[k].clear();
         }
      }

   }

   public static void printPlanData() {
      for(int k = 0; k < MAX_PLANS; ++k) {
         System.out.println("Plan " + k + ", " + planOccurrences[k] + "x, " + planValues[k] + "/" + planMaxExpectedValues[k]);
      }

   }

   public static void initTaskData() {
      for(int k = 0; k < MAX_PLANS; ++k) {
         guaranteedFitness = Float.NEGATIVE_INFINITY;
         planOccurrences[k] = 0;
         planBestFits[k] = null;
         planBestFitness[k] = Float.NEGATIVE_INFINITY;
         planValues[k] = MCSTSNode.root.ownvalue - 0.5F;
         planMaxExpectedValues[k] = maxExpectedPlanValue(MCSTSNode.root, k);
         if (planProgress(MCSTSNode.root, (short)-1, k) == Float.POSITIVE_INFINITY) {
            MCSTSNode var10000 = MCSTSNode.root;
            var10000.successfulStrategies |= (long)(1 << k);
         }
      }

      totalOccurrences = 0;
   }

   private void updatePlanData(int plan, float value) {
      sharedWriteLock.lock();
      int var10002 = planOccurrences[plan]++;
      ++totalOccurrences;
      if (value > planValues[plan]) {
         planValues[plan] = value;
      }

      sharedWriteLock.unlock();
   }

   private int selectPlan(MCSTSNode node) {
      int bestPlan = 0;
      float bestValue = Float.NEGATIVE_INFINITY;
      float log = (float)Math.log((double)(totalOccurrences + 1));

      for(int k = 0; k < MAX_PLANS; ++k) {
         if (planMaxExpectedValues[k] != 0.0F && (node.successfulStrategies & (long)(1 << k)) == 0L) {
            float v = planValues[k];
            float val = (float)((double)v + 5.0D * Math.sqrt((double)log / ((double)planOccurrences[k] + 1.0D)));
            if (val > bestValue) {
               bestValue = val;
               bestPlan = k;
            }
         }
      }

      return bestPlan;
   }

   static float planProgress(MCSTSNode node, short move, int plan) {
      return !MCSTSNode.usePlans ? r2.nextFloat() : node.state.attackProgress(plan / 8, plan % 8, move < 0 ? null : MCSTSNode.short2Move(move));
   }

   public static float[] planProgress(long z, int plan, MCSTSNode n, short[] moves) {
      float[] out = (float[])planTable[plan].get(z);
      if (out != null) {
         return out;
      } else if (moves == null) {
         return null;
      } else {
         out = new float[moves.length];

         for(int k = 0; k < moves.length; ++k) {
            out[k] = planProgress(n, moves[k], plan);
         }

         planTable[plan].put(z, out);
         return out;
      }
   }

   public static float maxExpectedPlanValue(MCSTSNode node, int plan) {
      int x = plan / 8;
      int y = plan % 8;
      if ((double)(node.state.piece[x][y] + node.state.pawn[x][y]) > 0.1D) {
         return 1.0F;
      } else {
         int y2 = node.state.white ? y - 1 : y + 1;
         return y2 >= 0 && y2 < 8 && node.state.allied[x][y2] == 0 ? 1.0F : 0.0F;
      }
   }

   public static float maxInvestment(int plan, float progress) {
      float out = planMaxExpectedValues[plan] * progress / 12.0F;
      if (out > planMaxExpectedValues[plan]) {
         out = planMaxExpectedValues[plan];
      }

      return out;
   }

   public static void updatePlanFitness(MCSTSNode n, int plan, float progress) {
      if (progress != Float.POSITIVE_INFINITY) {
         float fitness = n.treeValue() + maxInvestment(plan, progress);
         if (!((double)(progress - planProgress(MCSTSNode.root, (short)-1, plan)) < 0.0D)) {
            if (guaranteedFitness != Float.NEGATIVE_INFINITY) {
               int d = n.depth();
               MCSTSNode n2 = n;
               if (d > movesLeft) {
                  for(int k = 0; k < d - movesLeft; ++k) {
                     n2 = n2.parent;
                  }
               }

               if (n2.treeValue() + maxInvestment(plan, planProgress(n2, (short)-1, plan)) <= guaranteedFitness) {
                  return;
               }
            }

            if (fitness > planBestFitness[plan]) {
               sharedWriteLock.lock();
               if (fitness > planBestFitness[plan]) {
                  planBestFitness[plan] = fitness;
                  planBestFits[plan] = n;
               }

               sharedWriteLock.unlock();
            }

         }
      }
   }

   public static void printPlanFitness() {
      for(int k = 0; k < MAX_PLANS; ++k) {
         if (planBestFits[k] != null) {
            System.out.println("Best fit for plan " + k / 8 + " " + k % 8 + ": " + planBestFitness[k]);
            System.out.println("o:" + planBestFits[k].ownvalue + " v:" + planBestFits[k].value + " tree:" + planBestFits[k].treeValue());
            System.out.println(planBestFits[k].state);
         }
      }

   }

   public static short getPlanMove(int plan) {
      MCSTSNode node;
      for(node = planBestFits[plan]; node.parent != null && node.parent.parent != null; node = node.parent) {
      }

      return node.move;
   }

   public static short getPlanMove(MCSTSNode n) {
      while(n.parent != null && n.parent.parent != null) {
         n = n.parent;
      }

      return n.move;
   }

   public static short getBestMove(MCSTSNode node) {
      float v = node.treeValue();
      float bestC = Float.NEGATIVE_INFINITY;
      MCSTSNode m = null;

      int bestPlan;
      for(bestPlan = 0; bestPlan < node.childNumber; ++bestPlan) {
         float val = node.children[bestPlan].treeValue();
         if (val > bestC) {
            bestC = val;
            m = node.children[bestPlan];
         }
      }

      if (bestC > v + MCSTSNode.STRATEGIC_THRESHOLD) {
         System.out.println("Tactical decision: " + MCSTSNode.short2Move(m.move));
         nextMoves = null;
         guaranteedFitness = Float.NEGATIVE_INFINITY;
         movesLeft = 0;
         return m.move;
      } else {
         bestPlan = -1;
         bestC = Float.NEGATIVE_INFINITY;

         for(int k = 0; k < MAX_PLANS; ++k) {
            if (planBestFitness[k] > bestC) {
               bestC = planBestFitness[k];
               bestPlan = k;
            }
         }

         short out;
         if (bestPlan == -1) {
            if (nextMoves != null && nextMoves.size() > 0) {
               out = (Short)nextMoves.remove(0);
               --movesLeft;
               return out;
            } else {
               return node.bestChild.move;
            }
         } else {
            out = getPlanMove(bestPlan);
            System.out.println("Strategic decision: " + MCSTSNode.short2Move(out));
            System.out.println("Best fit for plan " + bestPlan / 8 + " " + bestPlan % 8 + ": " + planBestFitness[bestPlan]);
            System.out.println("o:" + planBestFits[bestPlan].ownvalue + " v:" + planBestFits[bestPlan].value + " tree:" + planBestFits[bestPlan].treeValue());
            System.out.println(planBestFits[bestPlan].state);
            nextMoves = planBestFits[bestPlan].movesSoFar();
            nextMoves.remove(0);
            movesLeft = nextMoves.size();
            guaranteedFitness = planBestFitness[bestPlan];
            return out;
         }
      }
   }

   public static short getBestPlanFulfillingMove() {
      MCSTSNode bestNode = null;
      float bestValue = Float.NEGATIVE_INFINITY;
      Iterator var3 = MCSTSNode.transpositionTable.iterator();

      while(var3.hasNext()) {
         MCSTSTransposition t = (MCSTSTransposition)var3.next();
         if (t.node.successfulStrategies != 0L && t.treeValue > bestValue && t.node.move != 0) {
            bestNode = t.node;
            bestValue = t.treeValue;
         }
      }

      if (bestNode == null) {
         return -1;
      } else {
         return getPlanMove(bestNode);
      }
   }

   public static void flushPlans() {
      guaranteedFitness = Float.NEGATIVE_INFINITY;
      movesLeft = 0;
      nextMoves = null;
   }

   public Object call() {
      int k = 0;
      int effective = 0;

      while(k < MCSTSNode.nodeNumber) {
         int plan = -1;
         MCSTSNode node = MCSTSNode.roots[this.setNumber - 1];
         MCSTSNode node2 = null;

         while(true) {
            if (plan == -1) {
               plan = this.selectPlan(node);
            }

            node2 = node.newStrategicSelect(this.setNumber, plan);
            if (node2 == null || node2.tacticalVisits == 0 || (node2.successfulStrategies & (long)(1 << plan)) != 0L) {
               ++k;
               if (node2 != null) {
                  node2.tacticalEval(this.setNumber);
                  this.updatePlanData(plan, node2.value);
                  updatePlanFitness(node2, plan, planProgress(node2, (short)-1, plan));
                  MCSTSTransposition trans = (MCSTSTransposition)MCSTSNode.transpositionTable.get(node2.zobrist);
                  float tv = node2.treeValue();

                  try {
                     sharedWriteLock.lock();
                     if (trans != null) {
                        if (trans.treeValue < tv) {
                           MCSTSNode var10000 = trans.node;
                           var10000.flags |= 2;
                           trans.treeValue = tv;
                           trans.node = node2;
                        } else {
                           node2.flags |= 2;
                        }
                     } else {
                        trans = new MCSTSTransposition();
                        trans.treeValue = tv;
                        trans.node = node2;
                        MCSTSNode.transpositionTable.put(node2.zobrist, trans);
                     }
                  } finally {
                     sharedWriteLock.unlock();
                  }

                  ++effective;
               }
               break;
            }

            node = node2;
         }
      }

      if (MCSTSNode.verbose) {
         System.out.println("Thread " + this.setNumber + ", " + k + " nodes (" + effective + " effective)");
      }

      return null;
   }

   public void run() {
      this.call();
   }
}
