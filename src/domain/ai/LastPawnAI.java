package domain.ai;

import domain.logic.GameLogic;
import domain.model.*;

import java.util.List;
import java.util.Optional;

public class LastPawnAI implements AIPlayer {
    @Override
    public Optional<Pawn> chooseMove(GameState gameState, int roll) {
        List<Pawn> legalMoves = GameLogic.possibleMoves(gameState, roll);
        
        if (legalMoves.isEmpty()) {
            return Optional.empty();
        }

        // bigger square number
        Pawn lastPawn = legalMoves.get(0);
        for (Pawn pawn : legalMoves) {
            if (pawn.getSquare() > lastPawn.getSquare()) {
                lastPawn = pawn;
            }
        }

        return Optional.of(lastPawn);
    }
}
