package connect4bot;

import java.io.IOException;
import java.util.*;

/**
 * Engine class responsible for calculating the AI's moves
 */
public class Engine {
    /**
     * The worst minimax value possible
     */
    static final int WORST_EVAL = -18;
    /**
     * The best minimax value possible
     */
    static final int BEST_EVAL = 18;
    /**
     * The size of the cache of positions
     */
    static final int SIZE = 30_000_001;
    /**
     * Caches containing the positions
     */
    static long[] lowerBoundCache = new long[SIZE], upperBoundCache = new long[SIZE];
    /**
     * Caches containing the upper and lower bounds of their respective positions
     */
    static int[] lowerBoundValues = new int[SIZE], upperBoundValues = new int[SIZE];
    /**
     * The default order to search moves in
     */
    private static final int MOVE_ORDER = 3 + (2 << 4) + (4 << 8) + (5 << 12) + (1 << 16) + (6 << 20);
    /**
     * Random number generator
     */
    private static final Random RNG = new Random();

    /**
     * Finds the optimal move to play
     *
     * @param state     Current Position
     * @param piece     Denotes what piece the computer is playing with (1 if it is going first, 0 if second)
     * @param movesMade The number of moves made in the game so far
     * @return The optimal column to play in
     */
    static int makeOptimalMove(long state, int piece, int movesMade) {
        ArrayList<Integer> bestMoves = new ArrayList<>();
        int maxEval = WORST_EVAL, i = 0;
        for (int j = 0; j < 7; j++) {
            int col = MOVE_ORDER >> j * 4 & 0b1111;
            int height = (int) (state >>> 42 + col * 3 & 0b111);
            if (height == 6) continue;
            long move = nextState(state, piece, col, height);
            if (isWin(move, piece)) return col;
            int eval;
            if (i == 0) eval = -evaluatePosition(move, piece ^ 1, WORST_EVAL, BEST_EVAL, movesMade + 1);
            else {
                if (maxEval < 0) {
                    eval = -evaluatePosition(move, piece ^ 1, -maxEval - 1, -maxEval + 1, movesMade + 1);
                    if (eval > maxEval) eval = -evaluatePosition(move, piece ^ 1, WORST_EVAL, BEST_EVAL, movesMade + 1);
                    else if (eval < maxEval) continue;
                } else {
                    eval = -evaluatePosition(move, piece ^ 1, -maxEval - 1, -maxEval, movesMade + 1);
                    if (eval > maxEval) eval = -evaluatePosition(move, piece ^ 1, WORST_EVAL, -maxEval, movesMade + 1);
                    else continue;
                }
            }
            if (eval > maxEval) {
                bestMoves.clear();
                bestMoves.add(col);
                maxEval = eval;
            } else if (eval == maxEval) bestMoves.add(col);
            i++;
        }
        return bestMoves.get(RNG.nextInt(bestMoves.size()));
    }

    /**
     * Finds the minimax value of a position, within the window [alpha, beta]
     *
     * @param state     The current position
     * @param piece     The piece of the current player
     * @param alpha     The lower bound of the search
     * @param beta      The upper bound of the search
     * @param movesMade The number of moves made so far
     * @return Minimax value of the position within [alpha, beta]
     */
    static int evaluatePosition(long state, int piece, int alpha, int beta, int movesMade) {
        if (movesMade == 42) return 0;
        int index = (int) (state % SIZE);
        beta = Math.min(beta, 21 - (movesMade >>> 1));
        alpha = Math.max(alpha, (movesMade + 1 >>> 1) - 21);
        if (lowerBoundCache[index] == state) alpha = Math.max(alpha, lowerBoundValues[index]);
        if (upperBoundCache[index] == state) beta = Math.min(beta, upperBoundValues[index]);
        if (alpha >= beta) return alpha;
        int threats = 0, order = MOVE_ORDER, forcedMoves = 0;
        long forcedMove = -1;
        for (int i = 0; i < 7; i++) {
            int col = MOVE_ORDER >> i * 4 & 0b1111;
            int height = (int) ((state >> 42 + col * 3) & 0b111);
            if (height != 6) {
                long move = nextState(state, piece, col, height), pieceLocations = getPieceLocations(move, piece);
                if (isWin(nextState(state, piece ^ 1, col, height), piece ^ 1)) {
                    forcedMoves++;
                    forcedMove = move;
                }
                if (isWin(pieceLocations)) return 21 - (movesMade >>> 1);
                int moveIndex = (int) (move % SIZE);
                if (upperBoundCache[moveIndex] == move) alpha = Math.max(alpha, -upperBoundValues[moveIndex]);
                if (alpha >= beta) return alpha;
                threats += countThreats(move, pieceLocations, piece) << col * 4;
            }
        }
        if (forcedMoves > 0)
            return forcedMoves > 1 ? (movesMade + 1 >>> 1) - 21 : -evaluatePosition(forcedMove, piece ^ 1, -beta, -alpha, movesMade + 1);
        order = sortByThreats(order, threats);
        int i = 0;
        for (int j = 0; j < 28; j += 4) {
            int col = order >>> j & 0b1111;
            int height = (int) (state >>> 42 + col * 3 & 0b111);
            if (height != 6) {
                long move = nextState(state, piece, col, height);
                int eval;
                if (i++ == 0) eval = -evaluatePosition(move, piece ^ 1, -beta, -alpha, movesMade + 1);
                else {
                    eval = -evaluatePosition(move, piece ^ 1, -alpha - 1, -alpha, movesMade + 1);
                    if (eval > alpha && eval < beta)
                        eval = -evaluatePosition(move, piece ^ 1, -beta, -alpha, movesMade + 1);
                }
                alpha = Math.max(alpha, eval);
                if (alpha >= beta) {
                    lowerBoundCache[index] = state;
                    lowerBoundValues[index] = alpha;
                    return alpha;
                }
            }
        }
        upperBoundCache[index] = state;
        upperBoundValues[index] = alpha;
        return alpha;
    }

    /**
     * Sorts moves based on the number of threats they create
     *
     * @param order   The order of moves
     * @param threats Stores the number of threats corresponding to each move
     * @return The order of moves
     */
    static int sortByThreats(int order, int threats) {
        for (int i = 4; i < 28; i += 4) {
            int j = i, currThreats = (threats >>> (order >>> i & 0b1111) * 4 & 0b1111);
            while (j > 0 && currThreats > (threats >>> (order >>> j - 4 & 0b1111) * 4 & 0b1111)) {
                j -= 4;
            }
            order = (order & (1 << j) - 1) + ((order >>> i & 0b1111) << j) + ((order >>> j & (1 << i - j) - 1) << j + 4) + (order >>> i + 4 << i + 4);
        }
        return order;
    }

    /**
     * Gets the next state that results after playing a move
     *
     * @param state  The current state
     * @param piece  The piece of the player making the move
     * @param col    The col that is being played in
     * @param height The height of the column being played in
     * @return The resulting state after the move is made
     */
    static long nextState(long state, int piece, int col, int height) {
        if (piece == 1) state += 1L << col * 6 + height;
        return state + (1L << 42 + col * 3);
    }

    /**
     * Counts the number of winning threats for a player in the current position
     *
     * @param state          The current state
     * @param pieceLocations The locations of all pieces the current player has played
     * @param piece          The piece of the current player
     * @return Number of winning threats
     */
    static int countThreats(long state, long pieceLocations, int piece) {
        int threatCount = 0;
        for (int col = 0; col < 7; col++) {
            for (int row = (int) (state >>> 42 + col * 3 & 0b111); row < 6; row++) {
                if (isWin(pieceLocations + (1L << col * 7 + row))) threatCount += 1 + ((row & 1) ^ piece);
            }
        }
        return threatCount;
    }

    /**
     * Finds all spots where the current player has played
     *
     * @param state The current state
     * @param piece The piece of the current player
     * @return All spots where the current player has played
     */
    static long getPieceLocations(long state, int piece) {
        long board = 0;
        if (piece == 1) {
            for (int i = 0; i < 7; i++) {
                board += (state >>> (6 * i) & 0b111111) << (7 * i);
            }
        } else {
            for (int i = 0; i < 7; i++) {
                board += ((state >>> (6 * i) & 0b111111) ^ ((1 << (state >> (42 + i * 3) & 0b111)) - 1)) << (7 * i);
            }
        }
        return board;
    }

    /**
     * Determines if the current player has won the game
     *
     * @param pieceLocations The spots where the current player has played
     * @return true if the current player has won, false otherwise
     */
    static boolean isWin(long pieceLocations) {
        for (int i = 1; i < 9; i += 1 / i * 4 + 1) {
            long connections = pieceLocations;
            for (int j = 0; j < 3; j++) connections = connections & (connections >>> i);
            if (connections != 0) return true;
        }
        return false;
    }

    /**
     * Determines if the current player has won the game
     *
     * @param state The current state
     * @param piece The piece of the current player
     * @return true if the current player has won, false otherwise
     */
    static boolean isWin(long state, int piece) {
        return isWin(getPieceLocations(state, piece));
    }

    /**
     * Gets the number of moves played in the current state
     *
     * @param state The current state
     * @return Number of moves played
     */
    static int depth(long state) {
        int depth = 0;
        for (int i = 42; i < 63; i += 3) {
            depth += (int) (state >>> i & 0b111);
        }
        return depth;
    }

    /**
     * Gets the state that is equivalent to the current state, except reflected horizontally
     *
     * @param state The current state
     * @return The horizontally reflected state
     */
    static long reflectState(long state) {
        long reflected = 0;
        for (int col = 0; col < 7; col++) {
            reflected += ((state >>> col * 6 & 0b111111) << (6 - col) * 6) + ((state >>> 42 + col * 3 & 0b111) << 42 + (6 - col) * 3);
        }
        return reflected;
    }
}
