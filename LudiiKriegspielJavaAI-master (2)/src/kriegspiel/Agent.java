package kriegspiel;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import game.Game;
import main.collections.FastArrayList;
import other.AI;
import other.context.Context;
import other.move.Move;

/** 
 * This code is a short example demonstrating the usage of Kriegspiel-specific Ludii functions.
 * For more information regarding Ludii AI development, please refer to the following: 
 * https://www.ludii.games/index.php
 * https://ludiitutorials.readthedocs.io/en/latest/basic_ai_api.html
 * https://ludiitutorials.readthedocs.io/en/latest/ludii_terminology.html
 * https://ludiitutorials.readthedocs.io/en/latest/cheat_sheet.html
*/

public class Agent extends AI
{
	
	//-------------------------------------------------------------------------
	
	/** Our player index */
	protected int player = -1;
	
	/** Opponent player index **/
	protected int opponent = -1;
	
	/** Number of players **/
	private final int players = 2;
	
	//-------------------------------------------------------------------------
	
	/**
	 * Constructor
	*/
	public Agent()
	{
		this.friendlyName = "Kriegspiel Agent Example";
	}
	
	//-------------------------------------------------------------------------
	
	/**
	 * The function returns True if the Agent plays with white pieces
	*/
	private boolean aiPlayerWhite() {
		return (player == 1);
	}
	
	private Move chooseRandomMove(FastArrayList<Move> pseudoLegalMoves) {
		final int r = ThreadLocalRandom.current().nextInt(pseudoLegalMoves.size());
		return pseudoLegalMoves.get(r); 
	}
	
	@Override
	public Move selectAction
	(
		final Game game, 
		final Context context, 
		final double maxSeconds,
		final int maxIterations,
		final int maxDepth
	)
	{
		/**
		 * A function that returns a set of pseudo-legal moves
		 * As a player, the agent has to try a move selected among the given set
		 * The referee, who knows the list of legal moves for both sides, answers whether the move was legal or not
		*/
		FastArrayList<Move> pseudoLegalMoves = game.moves(context).moves();
		
		/**
		 * Messages received from the referee after the last try
		 * It contains messages announcing the position of all captures, checks, and check directions,
		 * or simply no messages if the move is legal
		 * Otherwise, if the selected try is impossible to play, the only message announced is "Illegal move"  
		*/
		List <String> lastTryMessages = context.getNotes(player);
		
		if(lastTryMessages.size() != 0) {
			if(lastTryMessages.get(0).equals("Illegal move")) {
				/** 
				 * The previously selected try is illegal on the referee's board
				 * The Agent is asked for another try
				 * For the simplicity terms, we would again try a random move
				*/
				return chooseRandomMove(pseudoLegalMoves);
			}
		}
	
		if(pseudoLegalMoves.get(0).actionDescriptionStringShort().contentEquals("Promote")) {
			/**
			 * A situation in which the agent's pawn needs to be promoted after the last move
			 * In this case, pseudoLegalMoves would contain only promotion moves with different pieces
			 * Use pseudoLegalMove.what() to select a move containing the desired promotion piece
			 * We will play a random promotion move
			*/
			return chooseRandomMove(pseudoLegalMoves);
		}
		
		/** 
		 * The opponent score represents the number of pawn captures
		 * available to the opponent after our last legal move
		*/
		int oppTries = context.score(opponent);
		
		/**
		 * Referee messages that are announced after the opponent's legal move
		 * If the capture happened in the last turn, one of the messages would 
		 * specify where the capture took place and whether the captured piece is the pawn or another piece
		 * If the agent's King is in check, one message would reveal the check type (rank, file, long/short diagonal, or knight) 
		*/
		List <String> refereeMessages = context.getNotes(opponent);
		
		/**
		 * The player score denotes the number of legal capturing moves using pawns 
		 * for the current turn
		*/
		int pawnTries = context.score(player);
		
		/**
		 * After receiving all the available information, the agent is asked to provide the next try
		 * The next try needs to be selected among the set of pseudo-legal moves based on the agent's strategy
		 * The Move class provides `from()` and `to()` functions that can be useful in searching pseudo-legal 
		 * moves for the index of the desired move
		*/
		return chooseRandomMove(pseudoLegalMoves);
	}
	
	@Override
	public void initAI(final Game game, final int playerID)
	{
		this.player = playerID;
		this.opponent = this.players - playerID + 1;
	}
	
	//-------------------------------------------------------------------------

}