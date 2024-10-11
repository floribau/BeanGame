import java.util.PriorityQueue;
import java.util.Queue;

public class AI {


    private static final int MAX_DEPTH = 12;

    public static MiniMaxEvaluationRecord alphaBetaSearch(State state, boolean redPlayer) {
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        MiniMaxEvaluationRecord minimax = minimax(state, 0, alpha, beta, true, redPlayer);
        System.out.println("Alpha-Beta-Search: " + minimax.field() + " / " + minimax.score());
        return minimax;
    }

    public static MiniMaxEvaluationRecord minimax(State state, int depth, double alpha, double beta, boolean maximizingPlayer,
        boolean redPlayer) {
        Queue<StateMove> moves = generatePossibleStates(state);
        if (moves.isEmpty()) {
            starvation(state);
        }

        if (depth == MAX_DEPTH || moves.isEmpty() || (state.getTreasuryRed() > 36 && state.isRedTurn()) || (state.getTreasuryBlue() > 36
            && !state.isRedTurn())) {
            // negate score if blue player
            int sign = redPlayer ? 1 : -1;
            return new MiniMaxEvaluationRecord(sign * scoreState(state), -1);
        }

        if (maximizingPlayer) {
            MiniMaxEvaluationRecord maxEval = new MiniMaxEvaluationRecord(Double.NEGATIVE_INFINITY, -1);
            for (StateMove child : moves) {
                MiniMaxEvaluationRecord value = minimax(child.state(), depth + 1, alpha, beta, false, redPlayer);
                if (maxEval.score() > value.score()) {
                    maxEval = new MiniMaxEvaluationRecord(maxEval.score(), maxEval.field());
                } else {
                    maxEval = new MiniMaxEvaluationRecord(value.score(), child.move());
                }
                alpha = Double.max(alpha, maxEval.score());
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            MiniMaxEvaluationRecord minEval = new MiniMaxEvaluationRecord(Double.POSITIVE_INFINITY, -1);
            for (StateMove child : moves) {
                MiniMaxEvaluationRecord value = minimax(child.state(), depth + 1, alpha, beta, true, redPlayer);
                if (minEval.score() < value.score()) {
                    minEval = new MiniMaxEvaluationRecord(minEval.score(), minEval.field());
                } else {
                    minEval = new MiniMaxEvaluationRecord(value.score(), child.move());
                }
                beta = Double.min(beta, minEval.score());
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }

    public static Queue<StateMove> generatePossibleStates(State state) {
        Queue<StateMove> moves = new PriorityQueue<>((o1, o2) -> {
            if (o1.state().isRedTurn()) {
                return o2.state().getTreasuryRed() - o1.state().getTreasuryRed();
            } else {
                return o2.state().getTreasuryBlue() - o1.state().getTreasuryBlue();
            }
        });
        if (state.isRedTurn()) {
            for (int i = 0; i < 6; i++) {
                if (state.getBoard()[i] > 0) {
                    moves.add(new StateMove(makeMove(state, i), i));
                }
            }
        } else {
            for (int i = 6; i < 12; i++) {
                if (state.getBoard()[i] > 0) {
                    moves.add(new StateMove(makeMove(state, i), i));
                }
            }
        }

        return moves;
    }

    public static double scoreState(State state) {
        // state scored from red player's perspective
        if (state.getTreasuryRed() > 36) {
            return 1000;
        }
        if (state.getTreasuryBlue() > 36) {
            return -1000;
        }
        double treasuryDiff = state.getTreasuryRed() - state.getTreasuryBlue();
        int[] board = state.getBoard();
        int weight = 0;
        int sumRed = 0;
        int sumBlue = 0;
        for (int i = 0; i < board.length; i++) {
            int value = board[i];
            if (i < 6) {
                sumRed += value;
            } else {
                sumBlue += value;
            }
            if (value == 1 || value == 3 || value == 5) {
                weight += (state.isRedTurn() && i > 0 && i < 7 || !state.isRedTurn() && (i == 0 || i >= 7)) ? 2 : 1;
            }
        }
        int fieldDiff = sumRed - sumBlue;
        return treasuryDiff + weight * 0.1 + fieldDiff * 0.15;
        //   return treasuryDiff;
    }

    public static State makeMove(State parent, int field) {
        State child = new State(parent);
        child.setRedTurn(!parent.isRedTurn());

        int startField = field;
        int[] gameBoard = child.getBoard();
        int value = gameBoard[field];
        gameBoard[field] = 0;
        while (value > 0) {
            field = (++field) % 12;
            gameBoard[field]++;
            value--;
        }

        if (gameBoard[field] == 2 || gameBoard[field] == 4 || gameBoard[field] == 6) {
            do {
                if (startField < 6) {
                    child.setTreasuryRed(child.getTreasuryRed() + gameBoard[field]);
                } else {
                    child.setTreasuryBlue(child.getTreasuryBlue() + gameBoard[field]);
                }
                gameBoard[field] = 0;
                field = (field == 0) ? 11 : --field;
            } while (gameBoard[field] == 2 || gameBoard[field] == 4 || gameBoard[field] == 6);
        }

        return child;
    }

    public static void starvation(State state) {
        int value = 0;
        int[] gameBoard = state.getBoard();
        for (int i = 0; i < gameBoard.length; i++) {
            value += gameBoard[i];
            gameBoard[i] = 0;
        }
        if (state.isRedTurn()) {
            state.setTreasuryBlue(state.getTreasuryBlue() + value);
        } else {
            state.setTreasuryRed(state.getTreasuryRed() + value);
        }
    }

}
