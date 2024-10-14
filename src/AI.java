import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class AI {

    public static int alphaBetaSearch(State state, boolean redPlayer) {
      int bestMove = -1;
      float maxEval = Float.NEGATIVE_INFINITY;
      float alpha = Float.NEGATIVE_INFINITY;
      float beta = Float.POSITIVE_INFINITY;


      List<Integer> moves = generatePossibleMoves(state);
      System.out.println("Anzahl zu prüfender Moves: " + moves.size());
      int initialDepth = 8;
      int depth = (int) Math.round(Math.log(60.0 / moves.size()) * initialDepth / 2.3);

      for (int i : moves) {
        State child = makeMove(state, i);
        float eval = minimax(child, depth, alpha, beta, false, redPlayer);
        if (eval > maxEval) {
          maxEval = eval;
          bestMove = i;
        }
      }

      return bestMove;
    }

    public static float minimax(State state, int depth, float alpha, float beta, boolean maximizingPlayer,
        boolean redPlayer) {
      Queue<State> moves = generatePossibleStates(state);
      if (moves.isEmpty()) {
        starvation(state);
      }

      if (depth < 0 || moves.isEmpty()  || state.getTreasuryRed() > 36 || state.getTreasuryBlue() > 36) {
        // negate score if blue player
        int sign = redPlayer ? 1 : -1;
        return sign * scoreState(state);
      }

      float resEval;

      if (maximizingPlayer) {
        float maxEval = Float.NEGATIVE_INFINITY;
        for (State child : moves) {
          maxEval = Float.max(maxEval, minimax(child, depth - 1, alpha, beta, false, redPlayer));
          alpha = Float.max(alpha, maxEval);
          if (beta <= alpha) {
            break;
          }
        }
        resEval = maxEval;
      } else {
        float minEval = Float.POSITIVE_INFINITY;
        for (State child : moves) {
          minEval = Float.min(minEval, minimax(child, depth - 1, alpha, beta, true, redPlayer));
          beta = Float.min(beta, minEval);
          if (beta <= alpha) {
            break;
          }
        }
        resEval = minEval;
      }
      return resEval;
    }

    public static List<Integer> generatePossibleMoves(State state) {
      List<Integer> moves = new ArrayList<>();
      if (state.isRedTurn()) {
        for (int i = 0; i < 6; i++) {
          if (state.getBoard()[i] > 0) {
            moves.add(i);
          }
        }
      }
      else {
        for (int i = 6; i < 12; i++) {
          if (state.getBoard()[i] > 0) {
            moves.add(i);
          }
        }
      }

      return moves;
    }

    public static Queue<State> generatePossibleStates(State state) {
      Queue<State> moves = new PriorityQueue<>((o1, o2) -> {
        if (o1.isRedTurn()) {
          return o2.getTreasuryRed() - o1.getTreasuryRed();
        } else {
          return o2.getTreasuryBlue() - o1.getTreasuryBlue();
        }
      });
      if (state.isRedTurn()) {
        for (int i = 0; i < 6; i++) {
          if (state.getBoard()[i] > 0) {
           moves.add(makeMove(state, i));
          }
        }
      }
      else {
        for (int i = 6; i < 12; i++) {
          if (state.getBoard()[i] > 0) {
            moves.add(makeMove(state, i));
          }
        }
      }

      return moves;
    }

    public static float scoreState(State state) {
      // state scored from red player's perspective
      if (state.getTreasuryRed() > 36) {
        return 1000;
      }
      if (state.getTreasuryBlue() > 36) {
        return -1000;
      }
      float score = (float) (state.getTreasuryRed() - state.getTreasuryBlue());

      int sign = state.isRedTurn() ? 1 : -1;
      int[] board = state.getBoard();
      for (int i=0; i < board.length; i++) {
        int value = board[i];
        if (value == 1 || value == 3 || value == 5) {
          int weight = (state.isRedTurn() && i > 0 && i < 7 || !state.isRedTurn() && (i == 0 || i >= 7) ) ? 2 : 1;
          score += weight * sign * 0.1 * i;
        }
      }

      return score;
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
