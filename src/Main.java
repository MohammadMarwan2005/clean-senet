import domain.ai.*;
import domain.console.*;
import domain.dice.Dice;
import domain.logic.GameLogic;
import domain.model.*;

import java.util.List;
import java.util.Optional;


public class Main {
    private static final int MAX_TURNS = 10000;

    public static void main(String[] args) {
        System.out.println("=== Senet Game ===");
        System.out.println("Ancient Egyptian board game");
        System.out.println();

        // Initialize game
        GameState gameState = GameLogic.initGame();
        Dice dice = new Dice();
        ConsoleDisplay display = new ConsoleDisplay();
        ConsoleInput input = new ConsoleInput();

        // Select players
        PlayerType blackType = PlayerType.Human;
        PlayerType whiteType = PlayerType.MinimaxFast;
//        PlayerType whiteType = input.selectPlayerType("Player 2 (White)");

        AIPlayer blackAI = createAI(Player.Black, blackType);
        AIPlayer whiteAI = createAI(Player.White, whiteType);

        System.out.println("\n=== Game Starting ===");
        System.out.println("Player 1 (Black): " + blackType);
        System.out.println("Player 2 (White): " + whiteType);
        System.out.println();

        // Game loop
        int turnCount = 0;
        while (turnCount < MAX_TURNS) {
            turnCount++;
            display.displayTurnHeader(turnCount);
            display.displayGameInfo(gameState);
            display.displayBoard(gameState);

            // Check if game is over
            GameOutcome outcome = GameLogic.isOver(gameState);
            if (outcome.isDone()) {
                display.displayGameOver(outcome);
                break;
            }

            // Roll dice
            int roll = dice.roll();
            System.out.println("Dice Roll: " + roll);

            // Get legal moves
            List<Pawn> legalMoves = GameLogic.possibleMoves(gameState, roll);
            display.displayMoves(gameState, roll, legalMoves);

            // Get move from player
            Optional<Pawn> chosenPawn;
            Player currentPlayer = gameState.getTurn();
            AIPlayer currentAI = currentPlayer == Player.Black ? blackAI : whiteAI;
            PlayerType currentType = currentPlayer == Player.Black ? blackType : whiteType;

            if (currentType == PlayerType.Human) {
                chosenPawn = input.getMove(gameState, roll, legalMoves);
            } else {
                // AI player
                chosenPawn = currentAI.chooseMove(gameState, roll);
                if (chosenPawn.isPresent()) {
                    System.out.println("AI chooses: Pawn at square " + chosenPawn.get().getSquare());
                } else {
                    System.out.println("AI has no move (skipping turn)");
                }
            }

            // Apply move or skip turn
            if (chosenPawn.isPresent()) {
                Pawn pawn = chosenPawn.get();
                Optional<GameState> newState = GameLogic.makeMove(pawn, roll, gameState);
                if (newState.isPresent()) {
                    gameState = newState.get();
                    display.displayMove(pawn, roll, pawn.getSquare(), pawn.getSquare() + roll);
                } else {
                    System.out.println("Invalid move! Skipping turn.");
                    gameState = GameLogic.skipTurn(gameState);
                }
            } else {
                // No move - skip turn
                gameState = GameLogic.skipTurn(gameState);
            }

            // Small delay for readability
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // Ignore
            }
        }

        if (turnCount >= MAX_TURNS) {
            System.out.println("\nMaximum turn limit reached!");
        }

        input.close();
    }

    /**
     * Create an AI player based on type
     */
    private static AIPlayer createAI(Player player, PlayerType type) {
        Evaluator evaluator = new Evaluator();
        
        switch (type) {
            case Human:
                return null; // Not used for human
            case RandomAI:
                return new RandomAI();
            case MinimaxFast:
                return new MinimaxAI(evaluator, 2, player);
            case MinimaxMedium:
                return new MinimaxAI(evaluator, 3, player);
            case MinimaxSlow:
                return new MinimaxAI(evaluator, 4, player);
            case LastPawnAI:
                return new LastPawnAI();
            default:
                return new RandomAI();
        }
    }
}
