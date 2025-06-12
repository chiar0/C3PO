package ai.player.perfectinfo;

import ai.player.AIPlayer;
import core.Globals;
import core.Move;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public abstract class PerfectInformationPlayer extends AIPlayer {
   String[] command;
   double trueRatio = 0.0D;
   boolean white;
   Random generator = new Random();
   public Process engine = null;
   public InputStream engineOutputStream = null;
   public InputStream engineErrorStream = null;
   public OutputStream engineInputStream = null;

   public void destroy() {
      Globals.processTable.remove(this.command[0]);
      this.engine.destroy();
   }

   public void init() {
      this.engine = (Process)Globals.processTable.get(this.command[0]);
      if (this.engine == null) {
         Runtime rt = Runtime.getRuntime();

         try {
            this.engine = rt.exec(this.command);
            this.engineOutputStream = this.engine.getInputStream();
            this.engineInputStream = this.engine.getOutputStream();
            this.engineErrorStream = this.engine.getErrorStream();
            Globals.processTable.put(this.command[0], this.engine);
            Thread.sleep(2000L);
            String command = null;

            do {
               command = this.readCommand(false);
            } while(command != null);

            this.sendInitCommands();
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      } else {
         this.engineOutputStream = this.engine.getInputStream();
         this.engineInputStream = this.engine.getOutputStream();
         this.engineErrorStream = this.engine.getErrorStream();
      }

   }

   public void sendInitCommands() {
   }

   protected void sendCommand(String c) {
      String comm = c + "\n";

      try {
         this.engineInputStream.write(comm.getBytes());
         this.engineInputStream.flush();
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   protected String readCommand(boolean block) {
      String comm = "";
      char[] i = new char[]{'\u0000'};

      try {
         if (this.engineOutputStream.available() == 0 && !block) {
            return null;
         }

         while(i[0] != '\n' || this.engineOutputStream.available() != 0) {
            i[0] = (char)this.engineOutputStream.read();
            if (i[0] != '\n') {
               comm = comm + new String(i);
            }

            if (this.engineOutputStream.available() == 0 && comm.endsWith(": ")) {
               break;
            }
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      return comm;
   }

   protected Move representsMove(String comm) {
      return null;
   }

   public void communicateIllegalMove(Move m) {
      super.communicateIllegalMove(m);
   }
}
