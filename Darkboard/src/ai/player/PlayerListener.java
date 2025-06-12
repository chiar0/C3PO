package ai.player;

import core.Move;

public interface PlayerListener {
   void communicateIllegalMove(Player var1, Move var2);

   String communicateLegalMove(Player var1, int var2, int var3, int var4, int var5);

   String communicateUmpireMessage(Player var1, int var2, int var3, int var4, int var5, int var6, int var7);

   void updateTime(Player var1, long var2);

   void communicateOutcome(Player var1, int var2);

   void communicateInfo(Player var1, int var2, int var3);

   boolean isInterestedInObject(Player var1, Object var2, int var3);

   void communicateObject(Player var1, Object var2, Object var3, int var4);
}
