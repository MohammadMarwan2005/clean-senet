package domain.ai;

import domain.logic.GameLogic;
import domain.model.*;

import java.util.List;
import java.util.Optional;

/**
 * Base interface for AI players.
 */
public interface AIPlayer {
    /**
     * Choose a move given the current game state and dice roll
     */
    Optional<Pawn> chooseMove(GameState gameState, int roll);
}
