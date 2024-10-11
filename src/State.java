import java.util.Arrays;
import java.util.Objects;

public class State {

    private int[] board;
    private int treasuryRed, treasuryBlue;
    private boolean redTurn;

    public State() {
        // creates a Node in the starting state
        treasuryRed = treasuryBlue = 0;
        board = new int[]{6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6};
        redTurn = true;
    }

    public State(State state) {
        // copies the given Node
        treasuryRed = state.treasuryRed;
        treasuryBlue = state.treasuryBlue;
        redTurn = state.redTurn;
        board = Arrays.copyOf(state.board, state.board.length);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        State st = (State) o;
        return treasuryRed == st.treasuryRed && treasuryBlue == st.treasuryBlue && redTurn == st.redTurn
            && Arrays.equals(board, st.board);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(treasuryRed, treasuryBlue, redTurn);
        result = 31 * result + Arrays.hashCode(board);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int i = board.length - 1; i >= 6; i--) {
            res.append(board[i]).append(" ");
        }
        res.append("\n");
        for (int i = 0; i < 6; i++) {
            res.append(board[i]).append(" ");
        }
        res.append("\nRed treasury: ").append(treasuryRed);
        res.append("\nBlue treasury: ").append(treasuryBlue);
        res.append("\nCurrent player: ").append(redTurn ? "Red player" : "Blue player").append("\n");
        return res.toString();
    }

    public int[] getBoard() {
        return board;
    }

    public boolean isRedTurn() {
        return redTurn;
    }

    public void setRedTurn(boolean redTurn) {
        this.redTurn = redTurn;
    }

    public int getTreasuryRed() {
        return treasuryRed;
    }

    public void setTreasuryRed(int treasuryRed) {
        this.treasuryRed = treasuryRed;
    }

    public int getTreasuryBlue() {
        return treasuryBlue;
    }

    public void setTreasuryBlue(int treasuryBlue) {
        this.treasuryBlue = treasuryBlue;
    }
}