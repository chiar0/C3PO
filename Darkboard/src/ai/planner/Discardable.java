package ai.planner;

import core.Move;

public interface Discardable {
   int YES = 0;
   int NO = 1;
   int MAYBE = 2;

   int canKeep(Move var1);
}
