package domain.ai;

import domain.model.GameState;
import domain.model.Pawn;

import java.util.Optional;

public interface AIPlayer {
    Optional<Pawn> chooseMove(GameState gameState, int roll);
}
