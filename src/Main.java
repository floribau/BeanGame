import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;


public class Main
{
  // static String server = "http://127.0.0.1:5000";
  static String server = "http://ole.informatik.uni-mannheim.de";
  static String name   = "depthDynamic";


  public static void main(String[] args) throws Exception
  {
    // System.out.println(load(server));
    createGame();
    // openGames();
    // joinGame("41");
  }


  static void createGame() throws Exception
  {
    String url = server + "/api/creategame/" + name;
    String gameID = load(url);
    System.out.println("Spiel erstellt. ID: " + gameID);

    url = server + "/api/check/" + gameID + "/" + name;
    while(true) {
      Thread.sleep(1000);
      String state = load(url);
      System.out.print("." + " (" + state + ")");
      if(state.equals("0") || state.equals("-1")) {
        break;
      } else if(state.equals("-2")) {
        System.out.println("time out");
        return;
      }
    }
    play(gameID, true);
  }


  static void openGames() throws Exception
  {
    String url = server + "/api/opengames";
    String[] opengames = load(url).split(";");
    for(int i = 0; i < opengames.length; i++) {
      System.out.println(opengames[i]);
    }
  }


  static void joinGame(String gameID) throws Exception
  {
    String url = server + "/api/joingame/" + gameID + "/" + name;
    String state = load(url);
    System.out.println("Join-Game-State: " + state);
    if(state.equals("1")) {
      play(gameID, false);
    } else if(state.equals("0")) {
      System.out.println("error (join game)");
    }
  }


  static void play(String gameID, boolean redPlayer) throws Exception
  {
    String checkURL = server + "/api/check/" + gameID + "/" + name;
    String statesMsgURL = server + "/api/statemsg/" + gameID;
    String stateIdURL = server + "/api/state/" + gameID;
    State gameState = new State();
    int startOpponent, endOpponent;
    if(redPlayer) {
      startOpponent = 7;
      endOpponent = 12;
    } else {
      startOpponent = 1;
      endOpponent = 6;
    }

    while(true) {
      Thread.sleep(100);
      int moveState = Integer.parseInt(load(checkURL));
      int stateID = Integer.parseInt(load(stateIdURL));
      if(stateID != 2 && ((startOpponent <= moveState && moveState <= endOpponent) || moveState == -1)) {
        if(moveState != -1) {
          int selectedField = moveState - 1;
          updateBoard(gameState, selectedField);
          System.out.println("Gegner waehlte: " + moveState + " /\t" + gameState.getTreasuryRed() + " - " + gameState.getTreasuryBlue());
          System.out.println(gameState + "\n");
        }

        // apply minimax with alpha-beta-pruning
        System.out.println("Rufe Alpha-Beta-Search auf...");
        int selectField = AI.alphaBetaSearch(new State(gameState), redPlayer);

        updateBoard(gameState, selectField);
        System.out.println("Waehle Feld: " + (selectField + 1) + " /\t" + gameState.getTreasuryRed() + " - " + gameState.getTreasuryBlue());
        System.out.println(gameState + "\n\n");

        move(gameID, selectField + 1);
      } else if(moveState == -2 || stateID == 2) {
        System.out.println("GAME Finished");
        checkURL = server + "/api/statemsg/" + gameID;
        System.out.println(load(checkURL));
        return;
      } else {
        System.out.println("- " + moveState + "\t\t" + load(statesMsgURL));
      }

    }
  }


  static void updateBoard(State gameState, int field) {
    gameState.updateBoard(field);
  }


  static void move(String gameID, int fieldID) throws Exception
  {
    String url = server + "/api/move/" + gameID + "/" + name + "/" + fieldID;
    System.out.println(load(url));
  }


  static String load(String url) throws Exception
  {
    URI uri = new URI(url.replace(" ", ""));
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uri.toURL().openConnection().getInputStream()));
    StringBuilder sb = new StringBuilder();
    String line = null;
    while((line = bufferedReader.readLine()) != null) {
      sb.append(line);
    }
    bufferedReader.close();
    return (sb.toString());
  }
}
