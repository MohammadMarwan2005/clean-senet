package domain.console;

import domain.model.GameOutcome;
import domain.model.GameState;
import domain.model.Pawn;

import java.util.List;

public class ConsoleDisplay {
    public void displayBoard(GameState gameState) {
        System.out.println("\nBoard:");
        System.out.println(gameState.getBoard().boardToString());
        System.out.println();
    }

    public void displayGameInfo(GameState gameState) {
        System.out.println("Current Player: " + gameState.getTurn());
        System.out.println("White Pawns: " + gameState.getWhitePawnCnt());
        System.out.println("Black Pawns: " + gameState.getBlackPawnCnt());
    }

    public void displayMoves(GameState gameState, int roll, List<Pawn> legalMoves) {
        System.out.println("Dice Roll: " + roll);
        System.out.println("Legal moves:");
        
        if (legalMoves.isEmpty()) {
            System.out.println("  No legal moves available. Turn will be skipped.");
        } else {
            for (int i = 0; i < legalMoves.size(); i++) {
                Pawn pawn = legalMoves.get(i);
                System.out.println("  " + i + ": Pawn at square " + pawn.getSquare());
            }
        }
    }

    public void displayMove(Pawn pawn, int roll, int from, int to) {
        System.out.println("Move: " + pawn.getColor() + " pawn from square " + from + 
                         " to square " + to + " (roll: " + roll + ")");
    }

    public void displayGameOver(GameOutcome outcome) {
        if (outcome.isDone()) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("Game Over! Winner: " + outcome.getWinner());
            System.out.println("=".repeat(50));
        }
    }

    public void displayTurnHeader(int turnNumber) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("Turn " + turnNumber);
        System.out.println("-".repeat(50));
    }
}
