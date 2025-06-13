package com.ai.ludii;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import main.collections.FastArrayList;
import other.context.Context;
import other.move.Move;

public class MoveParser {
    private static final Pattern MOVE_PATTERN = Pattern.compile("MOVE:\\s*(.+)", Pattern.CASE_INSENSITIVE);

    private String convertFromAlgebraicNotationToNumericNotation(String algebraicNotation) {
        // convert algebraic notation to numeric notation (square numbers)
        if (algebraicNotation == null || algebraicNotation.trim().isEmpty()) {
            return null;
        }

        String notation = algebraicNotation.trim();

        // Handle castling - these don't have simple numeric equivalents
        if ("O-O".equals(notation) || "0-0".equals(notation)) {
            return "O-O";
        }
        if ("O-O-O".equals(notation) || "0-0-0".equals(notation)) {
            return "O-O-O";
        }

        // Parse standard algebraic notation to extract destination square
        Pattern sanPattern = Pattern.compile("([NBRQK])?([a-h])?([1-8])?(x)?([a-h][1-8])(=[NBRQK])?[+#]?");
        Matcher matcher = sanPattern.matcher(notation);

        if (matcher.matches()) {
            String toSquare = matcher.group(5);
            if (toSquare != null && toSquare.length() == 2) {
                return String.valueOf(squareToNumber(toSquare));
            }
        }

        // If no match, try to parse as direct square notation (e.g., "e4")
        if (notation.matches("[a-h][1-8]")) {
            return String.valueOf(squareToNumber(notation));
        }

        return notation;
    }

    private int squareToNumber(String square) {
        // Convert chess square (e.g., "e4") to number
        // Standard chess board: a1=0, b1=1, ..., h1=7, a2=8, ..., h8=63
        if (square.length() != 2)
            return -1;

        char file = square.charAt(0);
        char rank = square.charAt(1);

        int fileNum = file - 'a'; // a=0, b=1, ..., h=7
        int rankNum = rank - '1'; // 1=0, 2=1, ..., 8=7

        return rankNum * 8 + fileNum;
    }

    public Move parseMove(String llmResponse, FastArrayList<Move> legalMoves, Context context) {
        // 1. Extract move description from response
        Matcher matcher = MOVE_PATTERN.matcher(llmResponse);
        if (!matcher.find())
            return null;
        String moveDescription = matcher.group(1).trim().toLowerCase();

        System.out.println("Parsed Move Description: " + moveDescription);

        String numericNotation = convertFromAlgebraicNotationToNumericNotation(moveDescription);
        if (numericNotation == null || numericNotation.isEmpty()) {
            System.out.println("No valid move description found.");
            return null;
        }
        System.out.println("Converted Numeric Notation: " + numericNotation);

        // 2. Find best matching move
        Move bestMatch = null;
        double bestScore = 0;

        for (Move move : legalMoves) {
            // Pass context to toTrialFormat
            String moveString = move.toTrialFormat(context).toLowerCase();
            double similarity = computeSimilarity(moveString, numericNotation);

            if (similarity > bestScore) {
                bestScore = similarity;
                bestMatch = move;
            }
        }
        System.out.println("Best Match: " + (bestMatch != null ? bestMatch.toTrialFormat(context) : "None")
                + " (Score: " + bestScore + ")");
        return bestMatch;
    }

    private double computeSimilarity(String a, String b) {
        if (a.equals(b))
            return 1.0;
        if (a.contains(b) || b.contains(a))
            return 0.8;
        return 0;
    }
}