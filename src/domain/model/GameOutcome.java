package domain.model;

public class GameOutcome {
    private final boolean isDone;
    private final Player winner;

    private GameOutcome(boolean isDone, Player winner) {
        this.isDone = isDone;
        this.winner = winner;
    }

    public static GameOutcome notDone() {
        return new GameOutcome(false, null);
    }

    public static GameOutcome won(Player player) {
        return new GameOutcome(true, player);
    }

    public boolean isDone() {
        return isDone;
    }

    public Player getWinner() {
        if (!isDone) {
            throw new IllegalStateException("Game is not done yet");
        }
        return winner;
    }

    @Override
    public String toString() {
        if (isDone) {
            return "Won(" + winner + ")";
        } else {
            return "NotDone";
        }
    }
}
