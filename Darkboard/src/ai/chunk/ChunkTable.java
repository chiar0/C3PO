package ai.chunk;

import database.GameDatabase;
import java.util.Vector;

public class ChunkTable {
   Vector<ChunkTable.ChunkTableEntry> entries = new Vector();

   public int size() {
      return this.entries.size();
   }

   public ChunkTable.ChunkTableEntry get(int k) {
      return (ChunkTable.ChunkTableEntry)this.entries.get(k);
   }

   public ChunkTable.ChunkTableEntry lookup(String player) {
      for(int k = 0; k < this.size(); ++k) {
         if (player.equals(this.get(k).player)) {
            return this.get(k);
         }
      }

      return null;
   }

   public void add(String s, Vector<Chunk> c) {
      if (this.lookup(s) == null) {
         ChunkTable.ChunkTableEntry t = new ChunkTable.ChunkTableEntry();
         t.player = s;
         t.chunks = c;
         this.entries.add(t);
      }
   }

   public void loadFromDatabase(String s, GameDatabase gd) {
      if (this.lookup(s) == null) {
         Vector<Chunk> c = ChunkMaker.makeChunks(s, gd);
         this.add(s, c);
      }
   }

   public class ChunkTableEntry {
      public String player;
      public Vector<Chunk> chunks;
   }
}
