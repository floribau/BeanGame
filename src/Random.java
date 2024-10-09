public class Random {

    // static String server = "http://127.0.0.1:5000";
    static String server = "http://ole.informatik.uni-mannheim.de";
    static String name = "random-AI";

    static int p1 = 0;
    static int p2 = 0;


    public static void main(String[] args) throws Exception {

        while (true) {
            System.out.println("Enter create or join (1,2): ");
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            String output = scanner.nextLine();
            switch (output) {
                case "1":
                    createGame();
                    break;
                case "2":
                    openGames();
                    System.out.println("Enter gameID: ");
                    String gameID = scanner.nextLine();
                    joinGame(gameID);
                    break;
                default:
                    System.out.println("Invalid input");
                    break;
            }
        }
    }


    static void createGame() throws Exception {
        String url = server + "/api/creategame/" + name;
        String gameID = load(url);
        System.out.println("Spiel erstellt. ID: " + gameID);

        url = server + "/api/check/" + gameID + "/" + name;
        while (true) {
            Thread.sleep(1000);
            String state = load(url);
            System.out.print("." + " (" + state + ")");
            if (state.equals("0") || state.equals("-1")) {
                break;
            } else if (state.equals("-2")) {
                System.out.println("time out");
                return;
            }
        }
        play(gameID, 0);
    }


    static void openGames() throws Exception {
        String url = server + "/api/opengames";
        String[] opengames = load(url).split(";");
        for (int i = 0; i < opengames.length; i++) {
            System.out.println(opengames[i]);
        }
    }


    static void joinGame(String gameID) throws Exception {
        String url = server + "/api/joingame/" + gameID + "/" + name;
        String state = load(url);
        System.out.println("Join-Game-State: " + state);
        if (state.equals("1")) {
            play(gameID, 6);
        } else if (state.equals("0")) {
            System.out.println("error (join game)");
        }
    }


    static void play(String gameID, int offset) throws Exception {
        String checkURL = server + "/api/check/" + gameID + "/" + name;
        String statesMsgURL = server + "/api/statemsg/" + gameID;
        String stateIdURL = server + "/api/state/" + gameID;
        int[] board = {6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6}; // position 1-12
        int start, end;
        if (offset == 0) {
            start = 7;
            end = 12;
        } else {
            start = 1;
            end = 6;
        }

        while (true) {
            Thread.sleep(100);
            int moveState = Integer.parseInt(load(checkURL));
            int stateID = Integer.parseInt(load(stateIdURL));
            if (stateID != 2 && ((start <= moveState && moveState <= end) || moveState == -1)) {
                if (moveState != -1) {
                    int selectedField = moveState - 1;
                    updateBoard(board, selectedField);
                    System.out.println("Gegner waehlte: " + moveState + " /\t" + p1 + " - " + p2);
                    System.out.println(printBoard(board) + "\n");
                }
                // calculate fieldID
                int selectField;
                // System.out.println("Finde Zahl: ");
                do {
                    selectField = (int) (Math.random() * 6) + offset;
                    // System.out.println("\t-> " + selectField );
                } while (board[selectField] == 0);

                updateBoard(board, selectField);
                System.out.println("Wï¿½hle Feld: " + (selectField + 1) + " /\t" + p1 + " - " + p2);
                System.out.println(printBoard(board) + "\n\n");

                move(gameID, selectField + 1);
            } else if (moveState == -2 || stateID == 2) {
                System.out.println("GAME Finished");
                checkURL = server + "/api/statemsg/" + gameID;
                System.out.println(load(checkURL));
                return;
            } else {
                System.out.println("- " + moveState + "\t\t" + load(statesMsgURL));
            }

        }
    }


    static int[] updateBoard(int[] board, int field) {
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
                    p1 += board[field];
                } else {
                    p2 += board[field];
                }
                board[field] = 0;
                field = (field == 0) ? field = 11 : --field;
            } while (board[field] == 2 || board[field] == 4 || board[field] == 6);
        }
        return board;
    }


    static String printBoard(int[] board) {
        String s = "";
        for (int i = 11; i >= 6; i--) {
            if (i != 6) {
                s += board[i] + "; ";
            } else {
                s += board[i];
            }
        }

        s += "\n";
        for (int i = 0; i <= 5; i++) {
            if (i != 5) {
                s += board[i] + "; ";
            } else {
                s += board[i];
            }
        }

        return s;
    }


    static void move(String gameID, int fieldID) throws Exception {
        String url = server + "/api/move/" + gameID + "/" + name + "/" + fieldID;
        System.out.println(load(url));
    }


    static String load(String url) throws Exception {
        return Main.getResponse(url);
    }
}