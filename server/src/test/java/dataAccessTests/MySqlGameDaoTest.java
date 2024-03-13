package dataAccessTests;

import dataAccess.exceptions.DataAccessException;
import dataAccess.MySqlGameDao;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;

public class MySqlGameDaoTest {

    private MySqlGameDao gameDao;

    @BeforeEach
    void setup() throws DataAccessException {
        gameDao = new MySqlGameDao();
        gameDao.clear();
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        gameDao.clear();
    }

    @Test
    void testInsertGame() throws DataAccessException {
        GameData game = new GameData(0, "whitePlayer", "blackPlayer", "Test Game");
        int gameId = gameDao.insertGame(game);
        assertTrue(gameId > 0);

        GameData retrievedGame = gameDao.getGame(gameId);
        assertNotNull(retrievedGame);
        assertEquals("Test Game", retrievedGame.gameName());
        assertEquals("whitePlayer", retrievedGame.whiteUsername());
        assertEquals("blackPlayer", retrievedGame.blackUsername());
    }

    @Test
    void testGetGameNotFound() throws DataAccessException {
        GameData game = gameDao.getGame(999999);
        assertNull(game);
    }

    @Test
    void testUpdateGame() throws DataAccessException {
        GameData game = new GameData(0, "whitePlayer", "blackPlayer", "Initial Game");
        int gameId = gameDao.insertGame(game);

        GameData updatedGame = new GameData(gameId, "newWhitePlayer", "newBlackPlayer", "Updated Game");
        gameDao.updateGame(updatedGame);

        GameData retrievedGame = gameDao.getGame(gameId);
        assertNotNull(retrievedGame);
        assertEquals("Updated Game", retrievedGame.gameName());
        assertEquals("newWhitePlayer", retrievedGame.whiteUsername());
        assertEquals("newBlackPlayer", retrievedGame.blackUsername());
    }

    @Test
    void testListGames() throws DataAccessException {
        gameDao.insertGame(new GameData(0, "white1", "black1", "Game 1"));
        gameDao.insertGame(new GameData(0, "white2", "black2", "Game 2"));

        Collection<GameData> games = gameDao.listGames();
        assertNotNull(games);
        assertFalse(games.isEmpty());
        assertTrue(games.size() >= 2);
    }
}
