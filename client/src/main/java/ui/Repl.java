package ui;

import model.GameData;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;

import java.util.Scanner;


public class Repl implements NotificationHandler {
    boolean loggedIn = false;
    private final WSChessClient client;
    private final Scanner scanner;

    public Repl (WSChessClient client) {
        this.client = client;
        this.scanner = new Scanner(System.in);
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
                        GameData newGame = client.createGame(gameName);
                        System.out.println("Created ID: " + newGame.gameID());
                        break;
                    case "join":
                        System.out.print("Enter Game ID to join: ");
                        int gameId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter player type (white/black/observer): ");
                        String playerType = scanner.nextLine().toUpperCase();
                        client.joinGame(gameId, playerType);
                        System.out.println("Joined game.");
                        BoardBuilder chessBoard = new BoardBuilder();
                        chessBoard.printBoard();
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
            }
        } catch (Exception e) {
            System.out.println("Error processing command: " + e.getMessage());
        }
    }


    @Override
    public void notify(Notification notification) {

    }

    @Override
    public void load(LoadGame loadGame) {

    }

    @Override
    public void warn(Error error) {

    }

    public static void main(String[] args) throws Exception {
        WSChessClient client = new WSChessClient("http://localhost:8080",this);
        Repl repl = new Repl(client);
        repl.run();
    }
}
