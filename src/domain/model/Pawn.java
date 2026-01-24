package domain.model;

public class Pawn {
    private final Player color;
    private final int square;

    public Pawn(Player color, int square) {
        this.color = color;
        this.square = square;
    }

    public Player getColor() {
        return color;
    }

    public int getSquare() {
        return square;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pawn pawn = (Pawn) obj;
        return square == pawn.square && color == pawn.color;
    }

    @Override
    public int hashCode() {
        return 31 * color.hashCode() + square;
    }

    @Override
    public String toString() {
        return "Pawn(" + color + ", " + square + ")";
    }
}
