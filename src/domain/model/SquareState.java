package domain.model;

public class SquareState {
    private final boolean isFree;
    private final Player occupant;

    private SquareState(boolean isFree, Player occupant) {
        this.isFree = isFree;
        this.occupant = occupant;
    }

    public static SquareState free() {
        return new SquareState(true, null);
    }

    public static SquareState occupied(Player player) {
        return new SquareState(false, player);
    }

    public boolean isFree() {
        return isFree;
    }

    public boolean isOccupied() {
        return !isFree;
    }

    public Player getOccupant() {
        if (isFree) {
            throw new IllegalStateException("Square is free, no occupant");
        }
        return occupant;
    }

    @Override
    public String toString() {
        if (isFree) {
            return "Free";
        } else {
            return "Occ(" + occupant + ")";
        }
    }
}
