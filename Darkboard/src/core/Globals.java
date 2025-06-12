package core;

import ai.chunk.ChunkTable;
import database.OpeningBook;
import database.PieceDensityInfo;
import database.PlayerModel;
import java.util.Hashtable;
import java.util.Vector;
import tester.Sampler;

public class Globals {
   public static String PGNPath = Globals.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("Darkboard.jar", "resources/darkboard_data");
   public static OpeningBook whiteOpeningBook = null;
   public static OpeningBook blackOpeningBook = null;
   public static PieceDensityInfo density = null;
   public static PlayerModel[] whiteModel = new PlayerModel[3];
   public static PlayerModel[] blackModel = new PlayerModel[3];
   public static boolean shouldSeekMatch = true;
   public static boolean hasGui = true;
   public static boolean usePools = false;
   public static boolean debug = false;
   public static Vector evaluationFunctionVector = new Vector();
   public static ChunkTable chunks = new ChunkTable();
   public static Hashtable<String, Process> processTable = new Hashtable();
   public static Sampler sampler = new Sampler(80);
   public static int threadNumber = 2;
}
