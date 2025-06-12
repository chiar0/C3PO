package database;

import java.util.Vector;
import pgn.ExtendedPGNGame;

public class DatabasePlayer {
   public static final int PLAYER_WIN = 0;
   public static final int PLAYER_DRAW = 1;
   public static final int PLAYER_LOSS = 2;
   String name = "";
   int gamesPlayed = 0;
   int wins = 0;
   int draws = 0;
   int losses = 0;
   int white = 0;
   int black = 0;
   Vector games;

   public DatabasePlayer(String s) {
      this.name = s;
      this.games = new Vector();
   }

   public double internalRating() {
      return this.gamesPlayed > 0 ? 1.0D * (double)this.wins / (double)this.gamesPlayed * (double)this.wins : 0.0D;
   }

   public void addGame(ExtendedPGNGame pgn) {
      this.games.add(pgn);
      ++this.gamesPlayed;
      if (pgn.getWhite().equals(this.name)) {
         ++this.white;
         if (pgn.getResult().equals("1-0")) {
            ++this.wins;
         }

         if (pgn.getResult().equals("0-1")) {
            ++this.losses;
         }
      } else {
         ++this.black;
         if (pgn.getResult().equals("1-0")) {
            ++this.losses;
         }

         if (pgn.getResult().equals("0-1")) {
            ++this.wins;
         }
      }

      if (pgn.getResult().equals("1/2-1/2")) {
         ++this.draws;
      }

   }

   public String toString() {
      return this.name + "\t\t\t" + this.gamesPlayed + " games, " + this.white + "W " + this.black + "B, " + this.wins + "/" + this.draws + "/" + this.losses + " [Rat " + (int)this.internalRating() + "]";
   }

   public MoveDatabase generateFirstMoveMap(int moves) {
      MoveDatabase m = new MoveDatabase();

      for(int k = 0; k < this.games.size(); ++k) {
         ExtendedPGNGame game = (ExtendedPGNGame)this.games.get(k);
         if (!game.getBlack().equals(this.name)) {
            ++m.gameSize;

            for(int i = 0; i < moves; ++i) {
               ExtendedPGNGame.PGNMoveData mov = game.getMove(true, i);
               if (mov != null) {
                  m.addMove(mov.finalMove);
                  m.getData(mov.finalMove).addCount(i + 1);
               }
            }
         }
      }

      m.sortDatabase();
      return m;
   }

   public OpeningBook generateOpeningBook(boolean white, int minLength, OpeningBook start) {
      OpeningBook ob = start != null ? start : new OpeningBook();

      for(int k = 0; k < this.games.size(); ++k) {
         ExtendedPGNGame game = (ExtendedPGNGame)this.games.get(k);
         if (game.isPlayerWhite(this.name) && white || !game.isPlayerWhite(this.name) && !white) {
            Opening o = new Opening(white);
            o.extractFromGame(game);
            if (o.getMoveNumber() >= minLength) {
               ob.addOpening(o);
            }
         }
      }

      return ob;
   }
}
