package umpire;

import ai.player.Player;
import java.util.Timer;
import pgn.ExtendedPGNGame;

public class Umpire {
   private Player p2;
   private Player p1;
   protected int fiftyMoves = 0;
   public boolean timedGame = false;
   protected int startTime = 0;
   protected int timeIncrement = 0;
   protected int timeLeft = 0;
   protected int opponentTimeLeft = 0;
   protected Timer timer;
   public ExtendedPGNGame transcript;

   public int getFiftyMoves() {
      return this.fiftyMoves;
   }

   public void setFiftyMoves(int i) {
      this.fiftyMoves = i;
   }

   public synchronized void decreaseTime() {
      --this.timeLeft;
   }

   public synchronized void setTime(int sec) {
      this.timeLeft = sec;
   }

   public synchronized int getTime() {
      return this.timeLeft;
   }

   public void setOpponentTime(int t) {
      this.opponentTimeLeft = t;
   }

   public int getOpponentTime() {
      return this.opponentTimeLeft;
   }

   public int getStartTime() {
      return this.startTime;
   }

   public int getTimeIncrement() {
      return this.timeIncrement;
   }

   public void setStartTime(int i) {
      this.startTime = i;
   }

   public void setTimeIncrement(int i) {
      this.timeIncrement = i;
   }

   public String offerDraw(Player offerer) {
      return "";
   }

   public void resign(Player offerer) {
   }

   public void setP1(Player p1) {
      this.p1 = p1;
   }

   public Player getP1() {
      return this.p1;
   }

   public void setP2(Player p2) {
      this.p2 = p2;
   }

   public Player getP2() {
      return this.p2;
   }
}
