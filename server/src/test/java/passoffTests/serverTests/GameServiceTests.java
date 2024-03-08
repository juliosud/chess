package java.passoffTests.serverTests;

import dataAccess.*;
import dataAccess.exceptions.AlreadyTakenException;
import dataAccess.exceptions.BadRequestException;
import dataAccess.exceptions.DataAccessException;
import dataAccess.exceptions.UnauthorizedException;
import service.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    private GameService gameService;
    private UserService userService;
    private AuthDao authDao;
    private GameDao gameDao;
    private UserDao userDao;

    @BeforeEach
    void setup() {
        userDao = new UserDao();
        gameDao = new GameDao();
        authDao = new AuthDao();
        userService = new UserService(userDao, authDao);
        gameService = new GameService(gameDao, authDao);
    }

    @Test
    void createGamePositiveTest() throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        UserData user = new UserData("user1", "password", "email");
        AuthData authData = userService.register(user);
        GameData gameData = new GameData(0, null, null, "gameName");
        Integer gameId = gameService.createGame(authData.authToken(), gameData);
        assertNotNull(gameId);
    }

    @Test
    void createGameNegativeTest() {
        UserData user = new UserData("user1", "password", "email");
        assertThrows(UnauthorizedException.class, () -> {
            GameData gameData = new GameData(0, null, null, "gameName");
            gameService.createGame("badToken", gameData);
        });
    }
    @Test
    void listGamesPositiveTest() throws DataAccessException, UnauthorizedException, AlreadyTakenException, BadRequestException {
        UserData user = new UserData("user1","password","email");
        AuthData authData = userService.register(user);
        assertNotNull(gameService.listGames(authData.authToken()));
    }

    @Test
    void listGamesNegativeTest(){
        assertThrows(UnauthorizedException.class,()->gameService.listGames("badToken"));
    }

    @Test
    void joinGamePositiveTest() throws DataAccessException,UnauthorizedException,BadRequestException,AlreadyTakenException{
        UserData user=new UserData("user1","password","email");
        AuthData authData = userService.register(user);
        GameData gameData= new GameData(0,null,null,"gameName");
        Integer gameId=gameService.createGame(authData.authToken(),gameData);
        gameService.joinGame(authData.authToken(),gameId,"WHITE");
    }

    @Test
    void joinGameNegativeTest() throws DataAccessException,UnauthorizedException,BadRequestException,AlreadyTakenException{
        UserData user=new UserData("user1","password","email");
        AuthData authData=userService.register(user);
        GameData gameData=new GameData(0,null,null,"gameName");
        Integer gameId=gameService.createGame(authData.authToken(),gameData);
        assertThrows(UnauthorizedException.class,()->gameService.joinGame("badToken",gameId,"WHITE"));
    }

    @Test
    void clearPositiveTest()throws DataAccessException{
        gameService.clear();
    }
}
