package com.ai.ludii;

import game.types.board.SiteType;
import main.collections.FastArrayList;
import other.context.Context;
import other.move.Move;
import other.state.container.ContainerState;
import game.equipment.component.Component;

public class GameStateTranslator {

    public String describeGameState(Context context) {
        StringBuilder sb = new StringBuilder();
        
        // Basic game info
        sb.append("Game: ").append(context.game().name()).append("\n");
        sb.append("Turn: ").append(context.trial().numMoves()).append("\n");
        sb.append("Current Player: ").append(context.state().playerToAgent(context.state().mover())).append("\n\n");
        
        // Board state
        sb.append("Board State:\n");
        SiteType siteType = context.board().defaultSite();
        
        for (int i = 0; i < context.board().numSites(); i++) {
            ContainerState cs = context.state().containerStates()[0];
            int componentId = cs.what(i, siteType);
            
            if (componentId != 0) {
                Component comp = context.components()[componentId];
                if (comp != null) {
                    String piece = comp.name();
                    int owner = cs.who(i, siteType);
                    
                    sb.append("- Site ").append(i).append(": ")
                      .append(piece).append(" (P").append(owner).append(")\n");
                }
            }
        }
        
        return sb.toString();
    }

    public String formatMoves(FastArrayList<Move> moves, Context context) {
        StringBuilder sb = new StringBuilder("Legal Moves:\n");
        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            // Pass context to toTrialFormat
            sb.append(i + 1).append(". ").append(move.toTrialFormat(context)).append("\n");
        }
        return sb.toString();
    }
}