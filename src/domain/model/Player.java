package domain.model;

public enum Player {
    Black,
    White;

    public Player opposite() {
        return this == Black ? White : Black;
    }
}
