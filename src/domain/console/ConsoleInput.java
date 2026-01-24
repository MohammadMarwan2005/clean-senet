package domain.console;

import domain.model.GameState;
import domain.model.Pawn;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;


public class ConsoleInput {
    private final Scanner scanner;

    public ConsoleInput() {
        this.scanner = new Scanner(System.in);
    }

    public Optional<Pawn> getMove(GameState gameState, int roll, List<Pawn> legalMoves) {
        if (legalMoves.isEmpty()) {
            System.out.println("No legal moves available. Press Enter to skip turn...");
            scanner.nextLine();
            return Optional.empty();
        }

        System.out.println("\nSelect a move:");
        for (int i = 0; i < legalMoves.size(); i++) {
            Pawn pawn = legalMoves.get(i);
            System.out.println("  " + i + ": Move pawn at square " + pawn.getSquare());
        }
        System.out.print("Enter move number (or -1 to skip): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice == -1) {
                return Optional.empty();
            }
            
            if (choice >= 0 && choice < legalMoves.size()) {
                return Optional.of(legalMoves.get(choice));
            } else {
                System.out.println("Invalid choice. Please try again.");
                return getMove(gameState, roll, legalMoves);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return getMove(gameState, roll, legalMoves);
        }
    }

    public PlayerType selectPlayerType(String playerName) {
        System.out.println("\nSelect type for " + playerName + ":");
        System.out.println("  1: Human");
        System.out.println("  2: Random AI");
        System.out.println("  3: Minimax AI (Fast - depth 2)");
        System.out.println("  4: Minimax AI (Medium - depth 3)");
        System.out.println("  5: Minimax AI (Slow - depth 4)");
        System.out.println("  6: Last Pawn AI");
        System.out.print("Enter choice (1-6): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1:
                    return PlayerType.Human;
                case 2:
                    return PlayerType.RandomAI;
                case 3:
                    return PlayerType.MinimaxFast;
                case 4:
                    return PlayerType.MinimaxMedium;
                case 5:
                    return PlayerType.MinimaxSlow;
                case 6:
                    return PlayerType.LastPawnAI;
                default:
                    System.out.println("Invalid choice. Defaulting to Human.");
                    return PlayerType.Human;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Defaulting to Human.");
            return PlayerType.Human;
        }
    }

    public void close() {
        scanner.close();
    }
}
