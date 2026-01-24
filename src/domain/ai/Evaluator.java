package domain.ai;

import domain.board.Board;
import domain.board.EndSquare;
import domain.board.SquareType;
import domain.logic.GameLogic;
import domain.model.*;

import java.util.Optional;


public class Evaluator {
    private static final double PAWN_PRICE = 20.0;

    public double evaluate(GameState gameState, Player player) {
        GameOutcome outcome = GameLogic.isOver(gameState);
        if (outcome.isDone()) {
            if (outcome.getWinner() == player) {
                return 1000.0;
            } else {
                return -1000.0;
            }
        }


        double positiveOrNegative = player == Player.Black ? 1.0 : -1.0;

        // reburth or it's previous free

        int rebirthSquare = findLastFreeBeforeRebirth(gameState);

        double pawnScore = PAWN_PRICE * (gameState.getWhitePawnCnt() - gameState.getBlackPawnCnt());

        // Sum over all squares
        double sum = 0.0;
        Board board = gameState.getBoard();
        
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            Optional<SquareState> state = board.getElem(i);
            if (state.isPresent()) {
                double val = valueFunction(i, state.get(), rebirthSquare, pawnScore);
                sum += val;
            }
        }

        return positiveOrNegative * sum;
    }


    // ready from the game documentation
    private double valueFunction(int square, SquareState state, int rebirthSquare, double pawnScore) {
        double sign = checkPositiveOrNeg(state);
        SquareType squareType = Board.squareType(square);
        Optional<EndSquare> endSquare = Board.getEndSquare(square);

        if (squareType == SquareType.Spec && endSquare.isPresent()) {
            EndSquare es = endSquare.get();
            switch (es) {
                case Happy:
                    return sign * -3.0 + pawnScore;
                case Horus:
                    return sign * -PAWN_PRICE + pawnScore;
                case Reatoum:
                    return sign * (0.75 * (30.0 - rebirthSquare) - 0.25 * PAWN_PRICE) + pawnScore;
                case Truths:
                    return sign * (0.625 * (30.0 - rebirthSquare) - 0.375 * PAWN_PRICE) + pawnScore;
                case Water:
                    return sign * (30.0 - square) + pawnScore;
                default:
                    return sign * (30.0 - square) + pawnScore;
            }
        } else {
            if (square != 24) {
                return sign * (30.0 - square) + pawnScore;
            } else {
                // Square 24 is worse than 22
                return sign * 8.5 + pawnScore;
            }
        }
    }

    private double checkPositiveOrNeg(SquareState state) {
        // -1 for Black, +1 for White, 0 for Free
        if (state.isFree()) {
            return 0.0;
        } else {
            return state.getOccupant() == Player.Black ? -1.0 : 1.0;
        }
    }

    private int findLastFreeBeforeRebirth(GameState gameState) {
        Board board = gameState.getBoard();
        for (int i = Board.HOUSE_OF_REBIRTH; i >= 0; i--) {
            Optional<SquareState> state = board.getElem(i);
            if (state.isPresent() && state.get().isFree()) {
                return i;
            }
        }
        return 0;
    }
}
