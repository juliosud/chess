package ui;

import model.AuthData;
import model.UserData;

import java.util.Scanner;

public class ChessClient {
    private ServerFacade serverFacade;
    private Scanner scanner;
    private AuthData userToken;

    public ChessClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
        this.scanner = new Scanner(System.in);
        this.userToken = null;
    }

    public void run() {
        System.out.println("Welcome to Chess!");
        boolean loggedIn = false;

        while (true) {
            if (!loggedIn) {
                System.out.println("Available commands: help, login, register, quit");
                String command = scanner.nextLine();

                switch (command) {
                    case "help":
                        System.out.println("help - Show this message");
                        System.out.println("login - Log in to your account");
                        System.out.println("register - Register a new account");
                        System.out.println("quit - Exit the program");
                        break;
                    case "login":
                        try {
                            System.out.println("Enter username:");
                            String username = scanner.nextLine();
                            System.out.println("Enter password:");
                            String password = scanner.nextLine();

                            UserData userData = new UserData(username, password,null);
                            AuthData authData = serverFacade.login(userData);
                            if (authData != null && authData.authToken() != null) {
                                userToken = authData;
                                loggedIn = true;
                                System.out.println("Logged in successfully.");
                            } else {
                                System.out.println("Login failed. Please check your username and password.");
                            }
                        } catch (Exception e) {
                            System.out.println("Login failed: " + e.getMessage());
                            loggedIn = false;
                        }
                        break;

                    case "register":
                        try {
                            System.out.println("Enter username:");
                            String username = scanner.nextLine();
                            System.out.println("Enter password:");
                            String password = scanner.nextLine();
                            System.out.println("Enter email:");
                            String email = scanner.nextLine();

                            UserData newUser = new UserData(username, password, email);
                            AuthData authData = serverFacade.register(newUser);
                            userToken = authData;
                            loggedIn = authData != null && authData.authToken() != null;
                            System.out.println("Registered and logged in successfully.");
                        } catch (Exception e) {
                            System.out.println("Registration failed: " + e.getMessage());
                            loggedIn = false;
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
                System.out.println("Available commands: help, logout, create game, list games, join game, join observer");
                String command = scanner.nextLine();

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
                    case "logout":
                        try {
                            serverFacade.logout(userToken.authToken());
                            loggedIn = false;
                            userToken = null;
                            System.out.println("Logged out successfully.");
                        } catch (Exception e) {
                            System.out.println("Logout failed: " + e.getMessage());
                        }
                        break;

                    case "create game":
                        break;
                    case "list games":
                        break;
                    case "join game":
                        break;
                    case "join observer":
                        break;
                    default:
                        System.out.println("Unknown command.");
                        break;
                }
            }
        }
    }

    public static void main(String[] args) {
        ServerFacade facade = new ServerFacade("http://localhost:8080");
        ChessClient client = new ChessClient(facade);
        client.run();
    }
}