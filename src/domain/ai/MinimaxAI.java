package domain.ai;

import domain.logic.GameLogic;
import domain.model.*;

import java.util.List;
import java.util.Optional;

public class MinimaxAI implements AIPlayer {
    private final Evaluator evaluator;
    private final int depth;
    private final Player player;

    // Dice probabilities: 1/16, 1/4, 3/8, 1/4, 1/16
    private static final double[] ROLL_PROBABILITIES = {
        1.0 / 16.0,  // roll 1
        1.0 / 4.0,   // roll 2
        3.0 / 8.0,   // roll 3
        1.0 / 4.0,   // roll 4
        1.0 / 16.0   // roll 5
    };

    public MinimaxAI(Evaluator evaluator, int depth, Player player) {
        this.evaluator = evaluator;
        this.depth = depth;
        this.player = player;
    }

    @Override
    public Optional<Pawn> chooseMove(GameState gameState, int roll) {
        if (depth == 0) {
            // choose the first possible move
            List<Pawn> legalMoves = GameLogic.possibleMoves(gameState, roll);
            return legalMoves.isEmpty() ? Optional.empty() : Optional.of(legalMoves.get(0));
        }

        List<Pawn> legalMoves = GameLogic.possibleMoves(gameState, roll);
        if (legalMoves.isEmpty()) {
            return Optional.empty();
        }

        double bestValue = Double.NEGATIVE_INFINITY;
        Optional<Pawn> bestMove = Optional.empty();

        for (Pawn pawn : legalMoves) {
            Optional<GameState> nextState = GameLogic.makeMove(pawn, roll, gameState);
            if (nextState.isPresent()) {
                double value = expectiminimax(nextState.get(), depth - 1, !isMaximizing(gameState));
                if (value > bestValue) {
                    bestValue = value;
                    bestMove = Optional.of(pawn);
                }
            }
        }

        return bestMove;
    }

    /**
     * Expectiminimax algorithm
     */
    private double expectiminimax(GameState gameState, int remainingDepth, boolean maximizing) {
        GameOutcome outcome = GameLogic.isOver(gameState);
        if (outcome.isDone()) {
            if (outcome.getWinner() == player) {
                return 1000.0;
            } else {
                return -1000.0;
            }
        }

        if (remainingDepth == 0) {
            return evaluator.evaluate(gameState, player);
        }

        // expect over all
        double expectedValue = 0.0;

        for (int roll = 1; roll <= 5; roll++) {
            List<Pawn> legalMoves = GameLogic.possibleMoves(gameState, roll);
            double rollValue;

            if (legalMoves.isEmpty()) {
                // No moves ? skip turn
                GameState skippedState = GameLogic.skipTurn(gameState);
                rollValue = expectiminimax(skippedState, remainingDepth - 1, !maximizing);
            } else {
                if (maximizing) {
                    // Maximize
                    rollValue = Double.NEGATIVE_INFINITY;
                    for (Pawn pawn : legalMoves) {
                        Optional<GameState> nextState = GameLogic.makeMove(pawn, roll, gameState);
                        if (nextState.isPresent()) {
                            double moveValue = expectiminimax(nextState.get(), remainingDepth - 1, false);
                            rollValue = Math.max(rollValue, moveValue);
                        }
                    }
                } else {
                    // Minimize
                    rollValue = Double.POSITIVE_INFINITY;
                    for (Pawn pawn : legalMoves) {
                        Optional<GameState> nextState = GameLogic.makeMove(pawn, roll, gameState);
                        if (nextState.isPresent()) {
                            double moveValue = expectiminimax(nextState.get(), remainingDepth - 1, true);
                            rollValue = Math.min(rollValue, moveValue);
                        }
                    }
                }
            }

            expectedValue += ROLL_PROBABILITIES[roll - 1] * rollValue;
        }

        return expectedValue;
    }

    private boolean isMaximizing(GameState gameState) {
        return gameState.getTurn() == player;
    }
}
