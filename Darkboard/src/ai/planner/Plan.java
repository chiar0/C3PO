package ai.planner;

import ai.player.Darkboard;
import core.Metaposition;
import core.Move;
import java.util.Vector;

public class Plan {
   public int priority = 3;
   public boolean active = true;
   public int age = 0;
   public int ttl = -1;
   float falloff = 1.0F;
   public float modifier = 1.0F;
   protected Vector subplans = new Vector();
   public Darkboard owner;
   public static String description = "";

   public Plan(Darkboard own) {
      this.owner = own;
   }

   public void isAdded() {
   }

   public float evaluate(Metaposition start, Metaposition end, Move m, Vector history) {
      float v = this.fitness(start, end, m, history);
      return v * this.modifier;
   }

   protected float fitness(Metaposition start, Metaposition end, Move m, Vector history) {
      return 0.0F;
   }

   public String toString() {
      return "Generic plan";
   }

   public float isApplicable(Metaposition start, Move m) {
      return 0.0F;
   }

   public void evolveAfterMove(Metaposition root, Metaposition ev, Move m, int cap, int capx, int capy, int check1, int check2, int tries) {
   }

   public void evolveAfterOpponentMove(Metaposition root, Metaposition ev, int capx, int capy, int check1, int check2, int tries) {
   }

   public void evolveAfterIllegalMove(Metaposition root, Metaposition ev, Move m) {
   }
}
