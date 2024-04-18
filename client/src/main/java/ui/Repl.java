package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.GameData;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;




import java.util.Map;
import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;


public class Repl implements NotificationHandler {

    private final WSChessClient client;
    private final Scanner scanner;
    boolean loggedIn = false;
    boolean inGame = false;
    private Integer inGameID;

    public Repl (String URL) throws ResponseException {
        client  = new WSChessClient(URL,this);
        scanner = new Scanner(System.in);
    }

    public void run() throws Exception {
        System.out.println("Welcome to Chess!");
        processCommand("help");
        String input;
        while (true) {
            System.out.print("Enter command: ");
            input = scanner.nextLine();
            if ("quit".equalsIgnoreCase(input)) {
                break;
            }
            processCommand(input);
        }
    }

    private void processCommand(String input) throws Exception {
        String[] parts = input.split(" ");
        String command = parts[0].toLowerCase();
        try {
            if (!loggedIn) {
                switch (command) {
                    case "help":
                        System.out.println("help - Show this message");
                        System.out.println("login - Log in to your account");
                        System.out.println("register - Register a new account");
                        System.out.println("quit - Exit the program");
                        break;
                    case "login":
                        System.out.print("Enter username: ");
                        String username = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String password = scanner.nextLine();
                        if (client.login(username, password)) {
                            System.out.println("Login successful.");
                            loggedIn = true;
                        } else {
                            System.out.println("Login failed.");
                        }
                        break;
                    case "register":
                        System.out.print("Enter username: ");
                        String regUsername = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String regPassword = scanner.nextLine();
                        System.out.print("Enter email: ");
                        String email = scanner.nextLine();
                        if (client.register(regUsername, regPassword, email)) {
                            System.out.println("Registration successful.");
                            loggedIn = true;
                        } else {
                            System.out.println("Registration failed.");
                        }
                        break;
                    case "quit":
                        System.out.println("Exiting program.");
                        return;
                    default:
                        System.out.println("Unknown command.");
                        break;
                }
            } else {
                if (!inGame){
                    switch (command) {
                        case "help":
                            System.out.println("Available commands:");
                            System.out.println("help - Show this message");
                            System.out.println("logout - Log out from your account");
                            System.out.println("create game - Create a new game");
                            System.out.println("list games - List all available games");
                            System.out.println("join game - Join an existing game");
                            System.out.println("join observer - Join as an observer in a game");
                            break;
                        case "list":
                            client.listGames().forEach(game -> System.out.println("ID: " + game.gameID() + ", Game Name: " + game.gameName() + ", WHITE: " + game.whiteUsername() + ", BLACK: " + game.blackUsername()));
                            break;
                        case "create":
                            System.out.print("Enter the name of the game: ");
                            String gameName = scanner.nextLine();
                            //GameData newGame = client.createGame(gameName);
                            Map<String, Integer> newGame = client.createGame(gameName);
                            System.out.println("Created ID: " + newGame.get("gameID"));
                            break;
                        case "join":
                            System.out.print("Enter Game ID to join: ");
                            int gameId = Integer.parseInt(scanner.nextLine());
                            System.out.print("Enter player type (white/black/observer): ");
                            String playerType = scanner.nextLine().toUpperCase();
                            if (playerType.equals("OBSERVER")){
                                client.joinObserver(gameId);
                                System.out.println("Joined game as OBSERVER.");
                                inGame = true;
                                inGameID = gameId;
                                client.redrawChessBoard(inGameID);
                            } else {
                                client.joinGame(gameId, playerType);
                                System.out.println("Joined game.");
                                inGame = true;
                                inGameID = gameId;
                                client.redrawChessBoard(inGameID);
                            }
                            break;
                        case "logout":
                            client.logout();
                            System.out.println("Logged out successfully.");
                            loggedIn = false;
                            break;
                        case "quit":
                            System.out.println("Exiting program.");
                            return;
                        default:
                            System.out.println("Unknown command.");
                            break;
                    }
                } else {
                    switch (command){
                        case "help":
                            System.out.println("redraw");
                            System.out.println("leave");
                            System.out.println("move");
                            System.out.println("resign");
                            System.out.println("highlight");
                        case "redraw":
                            client.redrawChessBoard(inGameID);
                            break;
                        case "leave":
                            client.leaveGame(inGameID);
                            inGame = false;
                            inGameID = null;
                            break;
                        case "move":
                            if (inGameID != null) {
                                System.out.print("Enter move (e.g., a2-a3): ");
                                String moveInput = scanner.nextLine();
                                String[] moveParts = moveInput.split("-");

                                if (moveParts.length == 2) {
                                    String start = moveParts[0].trim().toLowerCase();
                                    String end = moveParts[1].trim().toLowerCase();

                                    if (start.length() == 2 && end.length() == 2) {
                                        ChessPosition startPosition = parseChessPosition(start);
                                        ChessPosition endPosition = parseChessPosition(end);

                                        if (startPosition != null && endPosition != null) {
                                            ChessMove move = new ChessMove(startPosition, endPosition, null);

                                            try {
                                                client.makeMove(inGameID, move);
                                            } catch (Exception e) {
                                                System.out.println("Error making move: " + e.getMessage());
                                            }
                                        } else {
                                            System.out.println("Invalid move format. Please use format like 'a2-a3'.");
                                        }
                                    } else {
                                        System.out.println("Invalid move format. Please use format like 'a2-a3'.");
                                    }
                                } else {
                                    System.out.println("Invalid move format. Please use format like 'a2-a3'.");
                                }
                            } else {
                                System.out.println("Not in a game. Use 'join' command to join a game.");
                            }
                            break;


                        case "resign":
                            client.resignGame(inGameID);
                            inGame = false;
                            inGameID = null;
                            break;
                        case "highlight":
                            break;
                        default:
                            System.out.println("Unknown command.");
                            break;
                    }

                }

            }
        } catch (Exception e) {
            System.out.println("Error processing command: " + e.getMessage());
        }
    }

    public ChessPosition parseChessPosition(String input) {
        if (input.length() != 2) {
            return null; // Invalid input format, return null
        }

        char colChar = Character.toLowerCase(input.charAt(0));
        int col = colChar - 'a';
        int row = Character.getNumericValue(input.charAt(1)) - 1;

        // Check if the row and column values are within the valid range
        if (col >= 0 && col <= 7 && row >= 0 && row <= 7) {
            return new ChessPosition(row, col);
        } else {
            return null;
        }
    }



    @Override
    public void notify(Notification notification) {
        System.out.println("\n" + SET_TEXT_COLOR_BLUE + notification.message + SET_TEXT_COLOR_GREEN);
        printPrompt();

    }

    @Override
    public void load(LoadGame loadGame) {
        ChessGame game = loadGame.game;
        ChessBoardPrinter.printChessBoard(game.getBoard());
    }

    @Override
    public void warn(Error error) {

    }

    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }

    public static void main(String[] args) throws Exception {
        Repl repl = new Repl("http://localhost:8080");
        repl.run();
    }
}
