package clientTests;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
        try {serverFacade.clear();
        }catch (Exception e){
            throw new RuntimeException("Errou!");
        }

    }

    //test_____
    @Test
    void registerPositiveTest() throws Exception {
        UserData newUser = new UserData("user", "password", "user@example.com");
        var authData = serverFacade.register(newUser);
        Assertions.assertNotNull(authData);
    }

    @Test
    void registerNegativeTest() {
        UserData newUser = new UserData("user", "password", "user@example.com");
        Assertions.assertThrows(Exception.class, () -> serverFacade.register(newUser));
    }

    @Test
    void loginPositiveTest() throws Exception {
        UserData loginUser = new UserData("user", "password", null);
        var authData = serverFacade.login(loginUser);
        Assertions.assertNotNull(authData);
    }

    @Test
    void loginNegativeTest() {
        UserData loginUser = new UserData("user", "wrongpassword", null);
        Assertions.assertThrows(Exception.class, () -> serverFacade.login(loginUser));
    }

    @Test
    void logoutPositiveTest() throws Exception {
        UserData loginUser = new UserData("user2", "wrongpassword2", "log@gmail.com");
        AuthData authToken = serverFacade.register(loginUser);
        Assertions.assertDoesNotThrow(() -> serverFacade.logout(authToken.authToken()));
    }

    @Test
    void logoutNegativeTest() {
        String authToken = "invalidToken";
        Assertions.assertThrows(Exception.class, () -> serverFacade.logout(authToken));
    }



    @Test
    void listGamesPositiveTest() throws Exception {
        UserData loginUser = new UserData("user3", "wrongpassword23", "log@gmail.com3");
        AuthData authToken = serverFacade.register(loginUser);
        var games = serverFacade.listGames(authToken.authToken());
        Assertions.assertNotNull(games);
    }

    @Test
    void listGamesNegativeTest() {
        String authToken = "invalidToken";
        Assertions.assertThrows(Exception.class, () -> serverFacade.listGames(authToken));
    }

    @Test
    void createGamePositiveTest() throws Exception {
        UserData loginUser = new UserData("user34", "wrongpassword234", "log@gmail.com34");
        AuthData authToken = serverFacade.register(loginUser);
        GameData newGame = new GameData(1, "player1", "player2", "Test Game1");
        var gameData = serverFacade.createGame(authToken.authToken(), newGame);
        Assertions.assertNotNull(gameData);
    }

    @Test
    void createGameNegativeTest() {
        String authToken = "invalidToken";
        GameData newGame = new GameData(1, "player1", "player2", "Test Game");
        Assertions.assertThrows(Exception.class, () -> serverFacade.createGame(authToken, newGame));
    }

    @Test
    void joinGamePositiveTest() throws Exception {
        GameData newGame = new GameData(13, "player13", "player23", "Test Game13");
        UserData loginUser = new UserData("user345", "wrongpassword2345", "log@gmail.com345");
        AuthData authToken = serverFacade.register(loginUser);
        String playerColor = "WHITE";
        Assertions.assertDoesNotThrow(() -> serverFacade.joinGame(authToken.authToken(), newGame.gameID(), playerColor));
    }

    @Test
    void joinGameNegativeTest() {
        String authToken = "invalidToken";
        int gameId = 1;
        String playerColor = "WHITE";
        Assertions.assertThrows(Exception.class, () -> serverFacade.joinGame(authToken, gameId, playerColor));
    }



    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
