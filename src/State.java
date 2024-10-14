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
    String res = "";
    for (int i = board.length - 1; i >= 6; i--) {
      res += board[i] + " ";
    }
    res += "\n";
    for (int i = 0; i < 6; i++) {
      res += board[i] + " ";
    }
    res += "\nRed treasury: " + treasuryRed;
    res += "\nBlue treasury: " + treasuryBlue;
    res += "\nCurrent player: " + (redTurn ? "Red player" : "Blue player") + "\n";
    return res;
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

  public void switchRedTurn() {
    this.redTurn = !this.redTurn;
  }

  public void updateBoard(int field) {
    int startField = field;

    int value = board[field];
    board[field] = 0;
    while (value > 0) {
      field = (++field) % 12;
      board[field]++;
      value--;
    }

    if (board[field] == 2 || board[field] == 4 || board[field] == 6) {
      do {
        if (startField < 6) {
          treasuryRed += board[field];
        } else {
          treasuryBlue += board[field];
        }
        board[field] = 0;
        field = (field == 0) ? 11 : --field;
      } while (board[field] == 2 || board[field] == 4 || board[field] == 6);
    }

    switchRedTurn();
  }
}