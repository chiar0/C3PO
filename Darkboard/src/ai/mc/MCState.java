package ai.mc;

import core.Move;

public interface MCState {
   int MSG_SILENT = 0;
   int MSG_ILLEGAL = 1;
   int MSG_CAPTURE = 2;
   int MSG_CHECK = 3;
   int MSG_PAWN = 4;
   int MSG_CAP_CH = 5;
   int MSG_CAP_PAWN = 6;
   int MSG_CH_PAWN = 7;
   int MSG_CAP_CH_PAWN = 8;

   float eval(int var1);

   float eval(int var1, Move var2, MCState var3);

   float chanceOfMessage(int var1, Move var2, int var3, int var4);

   float getHeuristicBias(int var1, Move var2);

   Move[] getMoves(int var1);

   MCState[] getStates(int var1, Move[] var2);

   MCState getState(int var1, Move var2);

   boolean isBroken();
}
