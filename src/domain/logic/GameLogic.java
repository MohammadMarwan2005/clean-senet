package domain.logic;

import domain.board.Board;
import domain.board.EndSquare;
import domain.board.SquareType;
import domain.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameLogic {
    private static final int INIT_PAWN_COUNT = 7;

    private static boolean possibleBySquareType(SquareType squareType, Optional<EndSquare> endSquare, int destination) {
        if (squareType == SquareType.Spec && endSquare.isPresent()) {
            EndSquare es = endSquare.get();
            switch (es) {
                case Happy:
                case Water:
                case Horus:
                    return true;
                case Truths:
                case Reatoum:
                    // Can only leave with exact roll to 30
                    // meaning: Truths + 2 ( 28 + 2)
                    // or: Reatoum + 1 (29 + 1)
                    return destination == 30;
                default:
                    return true;
            }
        }
        return true;
    }


    public static boolean isPossible(Board board, Pawn pawn, int roll) {
        int n = pawn.getSquare();
        int m = n + roll;

        SquareType squareType = Board.squareType(n);
        Optional<EndSquare> endSquare = Board.getEndSquare(n);
        
        if (!possibleBySquareType(squareType, endSquare, m)) {
            return false;
        }

        // need to pass the happy house
        boolean skippedHappiness = n < Board.HOUSE_OF_HAPPINESS && m > Board.HOUSE_OF_HAPPINESS;
        if (skippedHappiness) {
            return false;
        }

        // Done,
        if (m >= 30) {
            return true;
        }

        Optional<SquareState> destState = board.getElem(m);

        // does not exist in our tree
        if (destState.isEmpty()) {
            return false;
        }

        SquareState dest = destState.get();

        // the square is free, you can go
        if (dest.isFree()) {
            return true;
        }


        Player destPlayer = dest.getOccupant();

        // it's for your enemy
        if (pawn.getColor() != destPlayer) {
            return true;
        }
        return false;

    }


    public static List<Pawn> possibleMoves(GameState gameState, int roll) {
        List<Pawn> playerPawns = gameState.getPawns(gameState.getTurn());
        List<Pawn> legals = new ArrayList<>();
        
        for (Pawn pawn : playerPawns) {
            if (isPossible(gameState.getBoard(), pawn, roll)) {
                legals.add(pawn);
            }
        }
        
        return legals;
    }

    public static GameState switchTurn(GameState gameState) {
        return gameState.withTurn(gameState.getTurn().opposite());
    }

    private static Optional<Integer> lastFreeBy(int square, GameState gameState) {
        Board board = gameState.getBoard();
        Optional<SquareState> state = board.getElem(square);
        
        if (state.isEmpty()) {
            return Optional.empty();
        }
        
        if (state.get().isFree()) {
            return Optional.of(square);
        }
        
        // cheeck previous by previous
        if (square > 0) {
            return lastFreeBy(square - 1, gameState);
        }
        
        return Optional.empty();
    }

    private static Optional<GameState> pawnSwap(int pn, int qn, GameState gameState) {
        Board board = gameState.getBoard();
        Optional<Board> newBoard = board.swap(pn, qn);
        
        if (newBoard.isEmpty()) {
            return Optional.empty();
        }

        // Update pawn positions in game state
        Player pColor = null;
        Optional<SquareState> pState = board.getElem(pn);
        if (pState.isPresent() && pState.get().isOccupied()) {
            pColor = pState.get().getOccupant();
        }

        Player qColor = null;
        Optional<SquareState> qState = board.getElem(qn);
        if (qState.isPresent() && qState.get().isOccupied()) {
            qColor = qState.get().getOccupant();
        }

        GameState newState = gameState.withBoard(newBoard.get());
        
        // Update pawn lists
        if (pColor != null) {
            newState = updatePawnPosition(newState, pColor, pn, qn);
        }
        if (qColor != null && qColor != pColor) {
            newState = updatePawnPosition(newState, qColor, qn, pn);
        }

        return Optional.of(newState);
    }

    private static GameState updatePawnPosition(GameState gameState, Player player, int oldSquare, int newSquare) {
        List<Pawn> pawns = gameState.getPawns(player);
        List<Pawn> newPawns = new ArrayList<>();
        
        for (Pawn pawn : pawns) {
            if (pawn.getSquare() == oldSquare) {
                newPawns.add(new Pawn(player, newSquare));
            } else {
                newPawns.add(pawn);
            }
        }
        
        return gameState.withPawns(player, newPawns);
    }

    private static Board clearSquare(Board board, int square) {
        Optional<Board> cleared = board.setElem(square, SquareState.free());
        return cleared.orElse(board);
    }


    private static GameState removePawn(Pawn pawn, GameState gameState) {
        Board newBoard = clearSquare(gameState.getBoard(), pawn.getSquare());
        List<Pawn> playerPawns = gameState.getPawns(pawn.getColor());
        List<Pawn> newPawns = new ArrayList<>();
        
        for (Pawn p : playerPawns) {
            if (!p.equals(pawn)) {
                newPawns.add(p);
            }
        }
        
        GameState newState = gameState.withBoard(newBoard);
        newState = newState.withPawns(pawn.getColor(), newPawns);
        newState = newState.withDecrementedPawnCount(pawn.getColor());
        
        return newState;
    }

    public static Optional<GameState> playPawn(Pawn pawn, int roll, GameState gameState) {
        // validate, it's your turn
        if (pawn.getColor() != gameState.getTurn()) {
            return Optional.empty();
        }

        int n = pawn.getSquare();
        int m = n + roll;

        Board board = gameState.getBoard();
        SquareType squareType = Board.squareType(n);
        Optional<EndSquare> endSquare = Board.getEndSquare(n);

        // Check legality
        boolean legalSqType = possibleBySquareType(squareType, endSquare, m);
        boolean skippedHappiness = n < Board.HOUSE_OF_HAPPINESS && m > Board.HOUSE_OF_HAPPINESS;
        boolean attemptedLeave = m >= 30;

        if (!legalSqType) {
            // go to Rebirth, or any previous empty one
            Optional<Integer> rebirthSquare = lastFreeBy(Board.HOUSE_OF_REBIRTH, gameState);
            if (rebirthSquare.isPresent()) {
                return pawnSwap(n, rebirthSquare.get(), gameState);
            }
            return Optional.empty();
        }

        if (skippedHappiness) {
            return Optional.empty();
        }

        // leaved? remove it
        if (attemptedLeave) {
            return Optional.of(removePawn(pawn, gameState));
        }

        // normall move
        Optional<SquareState> destState = board.getElem(m);
        if (destState.isEmpty()) {
            return Optional.empty();
        }

        SquareState dest = destState.get();
        SquareType destType = Board.squareType(m);

        // Water? => go back to reburrth
        if (destType == SquareType.Spec && Board.getEndSquare(m).orElse(null) == EndSquare.Water) {
            Optional<Integer> rebirthSquare = lastFreeBy(Board.HOUSE_OF_REBIRTH, gameState);
            if (rebirthSquare.isPresent()) {
                return pawnSwap(n, rebirthSquare.get(), gameState);
            }
            return Optional.empty();
        }

        // regulare move
        if (dest.isFree()) {
            return pawnSwap(n, m, gameState);
        } else {
            Player destPlayer = dest.getOccupant();
            if (pawn.getColor() != destPlayer) {
                // Capture: swap positions
                return pawnSwap(n, m, gameState);
            } else {
                // Same color - illegal
                return Optional.empty();
            }
        }
    }

    // promotions houses = 27 - 28 - 29
    public static Optional<GameState> makeMove(Pawn pawn, int roll, GameState gameState) {
        // playpawn
        Optional<GameState> afterMove = playPawn(pawn, roll, gameState);
        if (afterMove.isEmpty()) {
            return Optional.empty();
        }

        GameState newState = afterMove.get();

        // of pawns in houses (27-29) that didn't move -> go back to reburht
        newState = sendBackUnmovedPromotionPawns(gameState, newState);

        newState = switchTurn(newState);

        return Optional.of(newState);
    }

    /**
     * Send back pawns in promotion houses that didn't move
     */
    private static GameState sendBackUnmovedPromotionPawns(GameState originalState, GameState newState) {
        // Check squares 27, 28, 29
        for (int square : new int[]{Board.HOUSE_OF_THREE_TRUTHS, Board.HOUSE_OF_RE_ATOUM, Board.HOUSE_OF_HORUS}) {
            Optional<SquareState> originalStateOpt = originalState.getBoard().getElem(square);
            Optional<SquareState> newStateOpt = newState.getBoard().getElem(square);

            if (originalStateOpt.isPresent() && newStateOpt.isPresent()) {
                SquareState original = originalStateOpt.get();
                SquareState current = newStateOpt.get();

                if (original.isOccupied() && current.isOccupied()) {
                    Player originalPlayer = original.getOccupant();
                    Player currentPlayer = current.getOccupant();

                    // the pawn still in same square? - go back
                    if (originalPlayer == currentPlayer && originalPlayer == newState.getTurn()) {
                        Optional<Integer> rebirthSquare = lastFreeBy(Board.HOUSE_OF_REBIRTH, newState);
                        if (rebirthSquare.isPresent()) {
                            Optional<GameState> swapped = pawnSwap(square, rebirthSquare.get(), newState);
                            if (swapped.isPresent()) {
                                newState = swapped.get();
                            }
                        }
                    }
                }
            }
        }

        return newState;
    }

    public static GameState skipTurn(GameState gameState) {
        // no possible moves? skip turn
        GameState newState = gameState;
        
        for (int square : new int[]{Board.HOUSE_OF_THREE_TRUTHS, Board.HOUSE_OF_RE_ATOUM, Board.HOUSE_OF_HORUS}) {
            Optional<SquareState> state = gameState.getBoard().getElem(square);
            if (state.isPresent() && state.get().isOccupied()) {
                Player player = state.get().getOccupant();
                if (player == gameState.getTurn()) {
                    Optional<Integer> rebirthSquare = lastFreeBy(Board.HOUSE_OF_REBIRTH, newState);
                    if (rebirthSquare.isPresent()) {
                        Optional<GameState> swapped = pawnSwap(square, rebirthSquare.get(), newState);
                        if (swapped.isPresent()) {
                            newState = swapped.get();
                        }
                    }
                }
            }
        }

        return switchTurn(newState);
    }


    public static GameOutcome isOver(GameState gameState) {
        if (gameState.getWhitePawnCnt() == 0) {
            return GameOutcome.won(Player.White);
        } else if (gameState.getBlackPawnCnt() == 0) {
            return GameOutcome.won(Player.Black);
        } else {
            return GameOutcome.notDone();
        }
    }

    public static GameState initGame() {
        Board board = Board.initBoard(INIT_PAWN_COUNT);
        List<Pawn> whitePawns = new ArrayList<>();
        List<Pawn> blackPawns = new ArrayList<>();

        // White (0, 2, 4, ...)
        for (int i = 0; i < INIT_PAWN_COUNT; i++) {
            whitePawns.add(new Pawn(Player.White, 2 * i));
        }

        // Black 1, 3, 5, ...)
        for (int i = 0; i < INIT_PAWN_COUNT; i++) {
            blackPawns.add(new Pawn(Player.Black, 2 * i + 1));
        }

        return new GameState(Player.Black, whitePawns, blackPawns, 
                           INIT_PAWN_COUNT, INIT_PAWN_COUNT, board);
    }
}
