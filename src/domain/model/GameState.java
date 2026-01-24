package domain.model;

import domain.board.Board;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private final Player turn;
    private final List<Pawn> whitePawns;
    private final List<Pawn> blackPawns;
    private final int whitePawnCnt;
    private final int blackPawnCnt;
    private final Board board;

    public GameState(Player turn, List<Pawn> whitePawns, List<Pawn> blackPawns,
                     int whitePawnCnt, int blackPawnCnt, Board board) {
        this.turn = turn;
        this.whitePawns = new ArrayList<>(whitePawns);
        this.blackPawns = new ArrayList<>(blackPawns);
        this.whitePawnCnt = whitePawnCnt;
        this.blackPawnCnt = blackPawnCnt;
        this.board = board;
    }

    public Player getTurn() {
        return turn;
    }

    public List<Pawn> getWhitePawns() {
        return new ArrayList<>(whitePawns);
    }

    public List<Pawn> getBlackPawns() {
        return new ArrayList<>(blackPawns);
    }

    public List<Pawn> getPawns(Player player) {
        return player == Player.White ? getWhitePawns() : getBlackPawns();
    }

    public int getWhitePawnCnt() {
        return whitePawnCnt;
    }

    public int getBlackPawnCnt() {
        return blackPawnCnt;
    }

    public Board getBoard() {
        return board;
    }

    public GameState withTurn(Player newTurn) {
        return new GameState(newTurn, whitePawns, blackPawns, whitePawnCnt, blackPawnCnt, board);
    }

    public GameState withPawns(Player player, List<Pawn> newPawns) {
        if (player == Player.White) {
            return new GameState(turn, newPawns, blackPawns, newPawns.size(), blackPawnCnt, board);
        } else {
            return new GameState(turn, whitePawns, newPawns, whitePawnCnt, newPawns.size(), board);
        }
    }

    public GameState withDecrementedPawnCount(Player player) {
        if (player == Player.White) {
            return new GameState(turn, whitePawns, blackPawns, Math.max(0, whitePawnCnt - 1), blackPawnCnt, board);
        } else {
            return new GameState(turn, whitePawns, blackPawns, whitePawnCnt, Math.max(0, blackPawnCnt - 1), board);
        }
    }

    public GameState withBoard(Board newBoard) {
        return new GameState(turn, whitePawns, blackPawns, whitePawnCnt, blackPawnCnt, newBoard);
    }
}
