package ai.planner;

import core.Metaposition;
import java.util.Vector;

public interface Area {
   boolean isIncluded(int var1, int var2, Metaposition var3);

   Vector<int[]> getAreaSquares(Metaposition var1);

   int getSquareNumber(Metaposition var1);
}
