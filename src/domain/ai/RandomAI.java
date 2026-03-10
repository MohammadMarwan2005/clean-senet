package domain.ai;

import domain.logic.GameLogic;
import domain.model.*;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RandomAI implements AIPlayer {
    private final Random random;

    public RandomAI() {
        this.random = new Random();
    }

    public RandomAI(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public Optional<Pawn> chooseMove(GameState gameState, int roll) {
        List<Pawn> legalMoves = GameLogic.possibleMoves(gameState, roll);
        
        if (legalMoves.isEmpty()) {
            return Optional.empty();
        }

        int index = random.nextInt(legalMoves.size());
        return Optional.of(legalMoves.get(index));
    }
}
