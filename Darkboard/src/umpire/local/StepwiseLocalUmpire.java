package umpire.local;

import ai.player.Player;
import core.Move;
import java.util.Hashtable;
import pgn.ExtendedPGNGame;

public class StepwiseLocalUmpire extends LocalUmpire {
   public int c1;
   public int c2;
   public int pt;
   public int cx;
   public int cy;
   public int turn;

   public StepwiseLocalUmpire(Player p1, Player p2) {
      super(p1, p2);
      this.transcript = new ExtendedPGNGame();
   }

   public void resetBoard() {
      super.resetBoard();
      this.c1 = this.c2 = 0;
      this.cx = this.cy = -1;
      this.pt = 0;
      this.turn = 0;
   }

   public void stepwiseInit(FENData fen, ExtendedPGNGame partial) {
      Hashtable d1 = new Hashtable();
      Hashtable d2 = new Hashtable();
      d1.put("isWhite", new Boolean(true));
      d2.put("isWhite", new Boolean(false));
      if (this.getP1() != null && this.getP1().playerName != null) {
         d1.put("white", this.getP1().playerName);
      }

      if (this.getP2() != null && this.getP2().playerName != null) {
         d1.put("black", this.getP2().playerName);
      }

      d1.put("where", "local");
      d2.put("where", "local");
      if (fen == null) {
         this.resetBoard();
      }

      if (this.getP1() != null) {
         this.getP1().startMatch(d1);
      }

      if (this.getP2() != null) {
         this.getP2().startMatch(d2);
      }

      int k;
      int t;
      if (fen != null) {
         for(k = 0; k < 8; ++k) {
            for(t = 0; t < 8; ++t) {
               this.board[k][t] = fen.getBoard()[k][t];
               if (this.board[k][t] == 6) {
                  this.kingX[0] = k;
                  this.kingY[0] = t;
               }

               if (this.board[k][t] == 12) {
                  this.kingX[1] = k;
                  this.kingY[1] = t;
               }
            }
         }

         this.fiftyMoves = fen.getHalfMove();
         this.moveCount = fen.getFullMove();
         this.turn = fen.isWhiteTurn() ? 0 : 1;
         this.tries = 0;
         this.transcript.addTag("FEN", fen.getFEN());
         if (this.turn == 1) {
            this.transcript.addMove(true, (Move)null, -1, -1, 0, 0, 0, 0);
            if (this.getP1() != null) {
               this.getP1().setHasTurn(false);
            }

            if (this.getP2() != null) {
               this.getP2().setHasTurn(true);
            }
         }

         if (this.getP1() != null) {
            this.getP1().startFromAlternatePosition(this.exportPerfectInformationChessboard(0));
         }

         if (this.getP2() != null) {
            this.getP2().startFromAlternatePosition(this.exportPerfectInformationChessboard(1));
         }

         this.fireChessboardChangeNotification();
      }

      if (partial != null) {
         if (this.verbose) {
            System.out.println("::: " + partial.getMoveNumber());
         }

         for(k = 0; k < partial.getMoveNumber(); ++k) {
            for(t = 0; t < 2; ++t) {
               boolean w = t == 0;
               ExtendedPGNGame.PGNMoveData data = partial.getMove(w, k);
               if (data == null) {
                  break;
               }

               Move m = data.finalMove;
               if (m == null) {
                  break;
               }

               if (this.verbose) {
                  System.out.println("UMPIRING " + m);
               }

               if (!this.isLegalMove(m, t)) {
                  break;
               }

               Player p = w ? this.getP1() : this.getP2();
               if (p != null) {
                  p.emulateNextMove(m);
                  this.stepwiseArbitrate(m);
               }
            }
         }
      }

      if (partial != null) {
         this.transcript = partial;
      }

      if (this.getP1() != null) {
         this.transcript.setWhite(this.getP1().getPlayerName());
         if (this.getP1().simplifiedBoard != null) {
            this.getP1().simplifiedBoard.setOwner(this.getP1());
         }
      }

      if (this.getP2() != null) {
         this.transcript.setBlack(this.getP2().getPlayerName());
         if (this.getP2().simplifiedBoard != null) {
            this.getP2().simplifiedBoard.setOwner(this.getP2());
         }
      }

   }

   public void stepwiseInit(ExtendedPGNGame g) throws FENData.MalformedFENException {
      if (g == null) {
         this.stepwiseInit((FENData)null, (ExtendedPGNGame)null);
      } else {
         String fen = g.getTag("FEN");
         if (fen == null) {
            this.stepwiseInit((FENData)null, g);
         } else {
            this.stepwiseInit(new FENData(fen), g);
         }
      }
   }

   public void stepwiseInit(String fen) throws FENData.MalformedFENException {
      if (fen == null) {
         this.stepwiseInit((FENData)null, (ExtendedPGNGame)null);
      } else {
         this.stepwiseInit(new FENData(fen), (ExtendedPGNGame)null);
      }
   }

   public Player turn() {
      return this.turn == 0 ? this.getP1() : this.getP2();
   }

   public synchronized String offerDraw(Player p) {
      if (this.gameOutcome != 0) {
         return "";
      } else {
         Player opponent;
         if (p == this.getP1()) {
            opponent = this.getP2();
         } else {
            if (p != this.getP2()) {
               return "";
            }

            opponent = this.getP1();
         }

         if (opponent.shouldAcceptDraw()) {
            this.gameOutcome = 5;
            this.winner = null;
            p.communicateOutcome(8);
            opponent.communicateOutcome(8);
            this.transcript.setResult("1/2-1/2");
            this.getP1().receiveAftermath(this.transcript);
            this.getP2().receiveAftermath(this.transcript);
            return "Mutual agreement draw.";
         } else {
            p.communicateUmpireInfo(1, 0);
            return "Draw offer was rejected.";
         }
      }
   }

   public synchronized void resign(Player p) {
      if (this.gameOutcome == 0) {
         Player opponent;
         if (p == this.getP1()) {
            opponent = this.getP2();
         } else {
            if (p != this.getP2()) {
               return;
            }

            opponent = this.getP1();
         }

         this.gameOutcome = 6;
         this.winner = null;
         p.communicateOutcome(7);
         opponent.communicateOutcome(6);
         this.transcript.setResult(p.isWhite ? "0-1" : "1-0");
         this.getP1().receiveAftermath(this.transcript);
         this.getP2().receiveAftermath(this.transcript);
      }
   }

   public synchronized String stepwiseArbitrate(Move m) {
      if (this.gameOutcome != 0) {
         return "";
      } else {
         int playerNumber = this.turn;
         Player whoseTurn = this.turn == 0 ? this.getP1() : this.getP2();
         Player opponent = this.turn == 0 ? this.getP2() : this.getP1();
         if (m == null) {
            return "";
         } else {
            String umpireMsg = "";
            int cx;
            if (this.isLegalMove(m, playerNumber)) {
               if (this.turn == 0) {
                  ++this.moveCount;
               }

               this.doMove(m, playerNumber);
               int cap = this.capture;
               cx = this.capX;
               int cy = this.capY;
               int chk = 0;
               int chk2 = 0;
               if (this.isKingThreatened((Move)null, 1 - playerNumber)) {
                  chk = this.check[0];
                  chk2 = this.check[1];
               }

               int pawnTries = this.legalMoveNumber(1 - playerNumber, true);
               this.tries = pawnTries;
               int captureType = 0;
               if (cap != 0) {
                  if (this.umpire2ChessboardPieceCode(cap) == 0) {
                     captureType = 1;
                  } else {
                     captureType = 2;
                  }
               } else {
                  cy = -1;
                  cx = -1;
               }

               if (captureType == 0 && this.umpire2ChessboardPieceCode(this.board[m.toX][m.toY]) != 0) {
                  ++this.fiftyMoves;
               } else {
                  this.fiftyMoves = 0;
               }

               this.transcript.addMove(playerNumber == 0, m, cx, cy, captureType, chk, chk2, pawnTries);

               int outcome;
               for(outcome = 0; outcome < this.illegalMoveVector.size(); ++outcome) {
                  this.transcript.getLatestMove(playerNumber == 0).addFailedMove((Move)this.illegalMoveVector.get(outcome));
               }

               this.illegalMoveVector.clear();
               this.transcript.firePGNChange();
               outcome = this.gameOutcome(1 - playerNumber);
               this.gameOutcome = outcome;
               if (outcome == 1) {
                  this.winner = whoseTurn;
                  whoseTurn.communicateOutcome(1);
                  opponent.communicateOutcome(3);
                  this.transcript.setResult(playerNumber == 0 ? "1-0" : "0-1");
                  if (this.verbose) {
                     System.out.println("Player " + playerNumber + " wins!");
                  }

                  String opponentColor = "White";
                  if (playerNumber == 0) {
                     opponentColor = "Black";
                  }

                  return opponentColor + " checkmated";
               }

               if (outcome == 2) {
                  this.winner = null;
                  whoseTurn.communicateOutcome(2);
                  opponent.communicateOutcome(2);
                  this.transcript.setResult("1/2-1/2");
                  if (this.verbose) {
                     System.out.println("Stalemate!");
                  }

                  return "Stalemate!";
               }

               if (outcome == 3) {
                  this.winner = null;
                  whoseTurn.communicateOutcome(4);
                  opponent.communicateOutcome(4);
                  this.transcript.setResult("1/2-1/2");
                  if (this.verbose) {
                     System.out.println("Fifty Moves Draw!");
                  }

                  return "Fifty Moves Draw!";
               }

               if (outcome == 4) {
                  this.winner = null;
                  whoseTurn.communicateOutcome(5);
                  opponent.communicateOutcome(5);
                  this.transcript.setResult("1/2-1/2");
                  if (this.verbose) {
                     System.out.println("Insufficient Material Draw!");
                  }

                  return "Insufficient Material Draw!";
               }

               umpireMsg = umpireMsg + whoseTurn.communicateLegalMove(captureType, pawnTries, chk, chk2);
               umpireMsg = umpireMsg + opponent.communicateUmpireMessage(cx, cy, pawnTries, chk, chk2, captureType);
               this.fireChessboardChangeNotification();
               if (outcome != 0) {
                  this.getP1().receiveAftermath(this.transcript);
                  this.getP2().receiveAftermath(this.transcript);
                  return "";
               }

               this.turn = 1 - this.turn;
            } else {
               if (this.verbose) {
                  System.out.println("Move " + m + " by Player " + playerNumber + " is NOT legal (reason: " + this.illegalMoveReason + ").");
               }

               if (this.illegalMoveReason == 10 || this.illegalMoveReason == 11 || this.illegalMoveReason == 5) {
                  boolean add = true;

                  for(cx = 0; cx < this.illegalMoveVector.size(); ++cx) {
                     if (this.illegalMoveVector.get(cx).equals(m)) {
                        add = false;
                     }
                  }

                  if (add) {
                     this.illegalMoveVector.add(m);
                  }
               }

               whoseTurn.communicateIllegalMove(m);
               this.fireChessboardChangeNotification();
            }

            return umpireMsg;
         }
      }
   }

   public String getPGNMoveString(Move m, boolean white, boolean omniscient) {
      boolean trivial = false;
      String result = "";
      if (m == null) {
         return "??";
      } else {
         if (m.toString().equals("O-O") || m.toString().equals("O-O-O")) {
            trivial = true;
            result = m.toString();
         }

         int movx = m.fromX;
         int movy = m.fromY;
         boolean needsX = false;
         boolean needsY = false;
         if (!trivial) {
            for(int k = 0; k < 8; ++k) {
               for(int j = 0; j < 8; ++j) {
                  if (k != movx || j != movy && this.umpire2ChessboardPieceCode(this.getBoard(k, j)) == m.piece) {
                     m.fromX = (byte)k;
                     m.fromY = (byte)j;
                     boolean legal = this.isLegalMove(m, white ? 0 : 1);
                     if (legal || !legal && !omniscient && this.illegalMoveReason >= 10) {
                        if (movx != k && movy == j) {
                           needsX = true;
                           needsY = false;
                        }

                        if (movx == k && movy != j) {
                           needsY = true;
                           needsX = false;
                        }

                        if (movx != k && movy != j) {
                           needsX = true;
                           needsY = false;
                        }
                     }
                  }
               }
            }
         }

         m.showFromX = needsX;
         m.showFromY = needsY;
         m.showSeparator = false;
         m.fromX = (byte)movx;
         m.fromY = (byte)movy;
         if (this.isLegalMove(m, white ? 0 : 1)) {
            this.stepwiseArbitrate(m);
            if (this.capture != 0) {
               m.capture = true;
            }

            if (this.isKingThreatened((Move)null, 1 - (white ? 0 : 1))) {
               if (this.check[0] != 0) {
                  m.check = true;
               }

               if (this.gameOutcome(1 - (white ? 0 : 1)) == 1) {
                  m.checkmate = true;
               }
            }
         } else if (m.piece == 0 && m.fromX != m.toX) {
            m.capture = true;
         }

         if (m.piece == 0 && m.capture) {
            m.showFromX = true;
         }

         return trivial ? result : m.toString();
      }
   }
}
