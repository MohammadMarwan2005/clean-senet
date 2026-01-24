package domain.dice;

import java.util.Random;

public class Dice {
    private final Random random;

    public Dice() {
        this.random = new Random();
    }

    /**
     * Probs: 1/16, 1/4, 3/8, 1/4, 1/16
     */
    public int roll() {
        double r = random.nextDouble();
        
        // Cumulative probabilities
        // 1: 0.0 - 0.0625 (1/16)
        // 2: 0.0625 - 0.3125 (1/16 + 1/4 = 5/16)
        // 3: 0.3125 - 0.6875 (5/16 + 3/8 = 11/16)
        // 4: 0.6875 - 0.9375 (11/16 + 1/4 = 15/16)
        // 5: 0.9375 - 1.0 (15/16 + 1/16 = 1.0)
        
        if (r < 1.0 / 16.0) {
            return 1;
        } else if (r < 1.0 / 16.0 + 1.0 / 4.0) {
            return 2;
        } else if (r < 1.0 / 16.0 + 1.0 / 4.0 + 3.0 / 8.0) {
            return 3;
        } else if (r < 1.0 / 16.0 + 1.0 / 4.0 + 3.0 / 8.0 + 1.0 / 4.0) {
            return 4;
        } else {
            return 5;
        }
    }
}
