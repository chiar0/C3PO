package com.ai.ludii;

import dev.langchain4j.model.chat.ChatLanguageModel;
import game.Game;
import main.collections.FastArrayList;
import other.AI;
import utils.AIRegistry;
import other.context.Context;
import other.move.Move;
import other.state.container.ContainerState;

public class C3POAgent extends AI {
    static {
        AIRegistry.registerAI("C-3PO LLM Agent", () -> new C3POAgent());
    }

    private final ChatLanguageModel model;
    private final GameStateTranslator translator;
    private final MoveParser moveParser;
    private int player = -1;

    public C3POAgent() {
        this.model = LLMProvider.createModel();
        this.translator = new GameStateTranslator();
        this.moveParser = new MoveParser();
        this.friendlyName = "C-3PO LLM Agent";
    }

    @Override
    public Move selectAction(
            final Game game,
            final Context context,
            final double maxSeconds,
            final int maxIterations,
            final int maxDepth) {
        try {
            // 1. Get current game state
            String gameState = translator.describeGameState(context); // filter out player1 positions

            // 2. Get legal moves
            FastArrayList<Move> legalMoves = game.moves(context).moves();
            // filter out illegal moves
            

            String movesList = translator.formatMoves(legalMoves, context);

            // 3. Build LLM prompt
            String prompt = String.format(
                    "You are an expert game AI playing '%s'. Current state:\n%s\n\n" +
                            "Your role: Player %d. Choose the best move and respond EXACTLY in this format : " +
                            "\"MOVE: <move description>\" /no_think",
                    context.game().name(), gameState, player);
            System.out.println("LLM Prompt: " + prompt);

            // 4. Get LLM response
            String llmResponse = model.generate(prompt);
            System.out.println("LLM Response: " + llmResponse);

            // 5. Parse and validate move
            Move chosenMove = moveParser.parseMove(llmResponse, legalMoves, context);

            // 6. Fallback to random move if parsing fails
            return chosenMove != null ? chosenMove : legalMoves.get(0);

        } catch (Exception e) {
            System.err.println("Error in LLM communication: " + e.getMessage());
            return game.moves(context).moves().get(0);
        }
    }

    @Override
    public void initAI(final Game game, final int playerID) {
        this.player = playerID;
    }
}