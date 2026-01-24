package domain.board;

import domain.model.Player;
import domain.model.SquareState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Board {
    public static final int BOARD_SIZE = 30;
    public static final int HOUSE_OF_REBIRTH = 14;
    public static final int HOUSE_OF_HAPPINESS = 25;
    public static final int HOUSE_OF_WATER = 26;
    public static final int HOUSE_OF_THREE_TRUTHS = 27;
    public static final int HOUSE_OF_RE_ATOUM = 28;
    public static final int HOUSE_OF_HORUS = 29;

    private final BoardTree tree;

    public Board(BoardTree tree) {
        this.tree = tree;
    }

    public static SquareType squareType(int n) {
        if (n == HOUSE_OF_REBIRTH) {
            return SquareType.Rebirth;
        } else if (n == HOUSE_OF_HAPPINESS) {
            return SquareType.Spec;
        } else if (n == HOUSE_OF_WATER || n == HOUSE_OF_THREE_TRUTHS ||
                   n == HOUSE_OF_RE_ATOUM || n == HOUSE_OF_HORUS) {
            return SquareType.Spec;
        } else {
            return SquareType.Reg;
        }
    }

    public static Optional<EndSquare> getEndSquare(int n) {
        switch (n) {
            case HOUSE_OF_HAPPINESS:
                return Optional.of(EndSquare.Happy);
            case HOUSE_OF_WATER:
                return Optional.of(EndSquare.Water);
            case HOUSE_OF_THREE_TRUTHS:
                return Optional.of(EndSquare.Truths);
            case HOUSE_OF_RE_ATOUM:
                return Optional.of(EndSquare.Reatoum);
            case HOUSE_OF_HORUS:
                return Optional.of(EndSquare.Horus);
            default:
                return Optional.empty();
        }
    }

    public static boolean isSafe(int square) {
        return square == HOUSE_OF_REBIRTH || square == HOUSE_OF_HAPPINESS ||
               square == HOUSE_OF_THREE_TRUTHS || square == HOUSE_OF_RE_ATOUM ||
               square == HOUSE_OF_HORUS;
    }

   public static Board initBoard(int pawnCount) {
        List<SquareState> squares = new ArrayList<>();
        
        for (int i = 0; i < 2 * pawnCount; i++) {
            if (i % 2 == 0) {
                squares.add(SquareState.occupied(Player.White));
            } else {
                squares.add(SquareState.occupied(Player.Black));
            }
        }
        
        for (int i = 2 * pawnCount; i < BOARD_SIZE; i++) {
            squares.add(SquareState.free());
        }
        
        return new Board(BoardTree.fromList(squares));
    }

    public Optional<SquareState> getElem(int i) {
        return tree.getElem(i);
    }

    public Optional<Board> setElem(int i, SquareState state) {
        return tree.setElem(i, state).map(Board::new);
    }

    public Optional<Board> swap(int i, int j) {
        return tree.swap(i, j).map(Board::new);
    }

    public String boardToString() {
        StringBuilder sb = new StringBuilder();
        
        // Row 1: 0-9
        for (int i = 0; i <= 9; i++) {
            sb.append(squareToChar(i, tree.getElem(i).orElse(null)));
            if (i < 9) sb.append(" ");
        }
        sb.append("\n");
        
        // Row 2: 19-10 (reversed)
        for (int i = 19; i >= 10; i--) {
            sb.append(squareToChar(i, tree.getElem(i).orElse(null)));
            if (i > 10) sb.append(" ");
        }
        sb.append("\n");
        
        // Row 3: 20-29
        for (int i = 20; i <= 29; i++) {
            sb.append(squareToChar(i, tree.getElem(i).orElse(null)));
            if (i < 29) sb.append(" ");
        }
        
        return sb.toString();
    }

    private char squareToChar(int i, SquareState state) {
        if (state == null) {
            return '.';
        }
        
        if (state.isOccupied()) {
            Player player = state.getOccupant();
            return player == Player.White ? 'W' : 'B';
        }
        
        // Free square - show special square markers
        SquareType type = squareType(i);
        switch (type) {
            case Rebirth:
                return ',';
            case Spec:
                Optional<EndSquare> endSquare = getEndSquare(i);
                if (endSquare.isPresent()) {
                    switch (endSquare.get()) {
                        case Happy:
                            return '&';
                        case Water:
                            return '~';
                        case Truths:
                            return '3';
                        case Reatoum:
                            return '2';
                        case Horus:
                            return '.';
                    }
                }
                return '.';
            default:
                return '.';
        }
    }

    public BoardTree getTree() {
        return tree;
    }
}
