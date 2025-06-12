package core;

import core.algorithm.EvaluationTableAlgorithm;
import core.algorithm.SelectionAlgorithm;
import java.util.Vector;

public class EvaluationFunction {
   Vector componentVector = new Vector();
   SelectionAlgorithm algorithm = new EvaluationTableAlgorithm();
   public static int index;
   public static int opponentTotalAge;
   public static ExtendedEvaluationTree currentNode;

   public boolean canHandleSituation(Metaposition root, int capx, int capy, int check1, int check2, int tries) {
      return true;
   }

   public String getName() {
      return "Generic Evaluation Function";
   }

   public SelectionAlgorithm getSelectionAlgorithm() {
      return this.algorithm;
   }

   public int getComponentNumber() {
      return this.componentVector.size();
   }

   public EvaluationFunctionComponent getComponent(int k) {
      if (k >= 0 && k < this.getComponentNumber()) {
         return (EvaluationFunctionComponent)this.componentVector.get(k);
      } else {
         System.out.println("Exceeded component number: " + k);
         return null;
      }
   }

   public void addComponent(EvaluationFunctionComponent efc) {
      this.componentVector.add(efc);
   }

   public float evaluate(Metaposition start, Metaposition dest, Move m, Vector history) {
      float result = 0.0F;
      if (currentNode != null && currentNode.componentNumber() > 0) {
         currentNode = null;
      }

      boolean extended = currentNode != null;
      int number = this.getComponentNumber();
      float value = 0.0F;
      if (m != null) {
         int var10000 = m.toX * 8 + m.toY;
      } else {
         boolean var14 = true;
      }

      opponentTotalAge = 0;
      start.owner.globals.materialDelta = start.owner.globals.totalMaterial - dest.piecesLeft - dest.pawnsLeft;
      start.owner.globals.materialRatio = 1.0F * (float)start.owner.globals.totalMaterial / (float)(dest.piecesLeft + dest.pawnsLeft);
      dest.calculateFriendlyMaterial();
      dest.computeKingLocation();
      dest.computeProtectionMatrix(true);

      int k;
      for(k = 0; k < 8; ++k) {
         start.owner.globals.pawnSquaresInFiles[k] = 0;
      }

      for(k = 0; k < 8; ++k) {
         for(int j = 0; j < 8; ++j) {
            if (dest.canContainEnemyPawn(k, j)) {
               int var10002 = start.owner.globals.pawnSquaresInFiles[k]++;
            }
         }
      }

      for(k = 0; k < number; ++k) {
         EvaluationFunctionComponent efc = this.getComponent(k);
         float subresult = efc.evaluate(start, dest, m, history);
         result += subresult;
         if (extended && !efc.generatesCustomReport()) {
            currentNode.addInformation(new Float(subresult));
         }
      }

      return result;
   }

   public Move getAutomaticMove(Metaposition in) {
      return null;
   }

   public Metaposition generateMostLikelyEvolution(Metaposition source, Move m) {
      boolean pawnTry = m.piece == 0 && m.fromX != m.toX;
      boolean pMove = source.getAge() == 0 && source.isPowerMove(m);
      int captureType = !pawnTry && source.mayBeEmpty(m.toX, m.toY) && !pMove ? 0 : (source.canContainEnemyPawn(m.toX, m.toY) ? 1 : 2);
      if (captureType != 0) {
         m.capture = true;
      }

      int opponentTries = 0;
      if (m.toX > 0 && m.toY != source.getLastRank() && m.toY != source.getSecondLastRank() && source.canContainEnemyPawn(m.toX - 1, m.toY + (source.isWhite() ? 1 : -1))) {
         ++opponentTries;
      }

      if (m.toX < 7 && m.toY != source.getLastRank() && m.toY != source.getSecondLastRank() && source.canContainEnemyPawn(m.toX + 1, m.toY + (source.isWhite() ? 1 : -1))) {
         ++opponentTries;
      }

      Metaposition s = Metaposition.evolveAfterMove(source, m, captureType, m.toX, m.toY, 0, 0, opponentTries);
      return s;
   }

   public float getMinValueForExpansion() {
      return -150.0F;
   }

   public float getMaxValueForExpansion() {
      return 400.0F;
   }

   public boolean useLoopDetector() {
      return false;
   }

   public boolean useKillerPruning() {
      return true;
   }

   public float getExplorationAlpha(Metaposition m) {
      return 0.9F;
   }

   public float getNodeMultFactor(Metaposition m) {
      return 1.0F;
   }

   public Move getAlgorithmicMove(Metaposition m) {
      return null;
   }

   public void algorithmicMoveAccepted(Metaposition m, Move move, Metaposition m2) {
   }

   public void algorithmicMoveRejected(Metaposition m, Move move, Metaposition m2) {
   }
}
