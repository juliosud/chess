package dataAccessTests;
import dataAccess.MySqlGameDao;
import dataAccess.exceptions.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;

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
    void positiveTestInsertGame() throws DataAccessException {
        GameData game = new GameData(0, "whitePlayer", "blackPlayer", "TestGame");
        int gameId = gameDao.insertGame(game);
        assertTrue(gameId > 0);
    }

    @Test
    void negativeTestInsertGame() {
        GameData invalidGame = new GameData(0, null, null, null); // Assuming gameName cannot be empty
        assertThrows(DataAccessException.class, () -> gameDao.insertGame(invalidGame));
    }

    @Test
    void positiveTestGetGame() throws DataAccessException {
        GameData game = new GameData(0, "whitePlayer", "blackPlayer", "TestGame");
        int gameId = gameDao.insertGame(game);
        GameData retrievedGame = gameDao.getGame(gameId);
        assertNotNull(retrievedGame);
        assertEquals("TestGame", retrievedGame.gameName());
    }

    @Test
    void negativeTestGetGame() throws DataAccessException {
        GameData retrievedGame = gameDao.getGame(-1);
        assertNull(retrievedGame);
    }

    @Test
    void positiveTestUpdateGame() throws DataAccessException {
        GameData game = new GameData(0, "whitePlayer", "blackPlayer", "TestGame");
        int gameId = gameDao.insertGame(game);
        GameData updatedGame = new GameData(gameId, "newWhitePlayer", "newBlackPlayer", "UpdatedGame");
        assertDoesNotThrow(() -> gameDao.updateGame(updatedGame));

        GameData retrievedGame = gameDao.getGame(gameId);
        assertEquals("UpdatedGame", retrievedGame.gameName());
        assertEquals("newWhitePlayer", retrievedGame.whiteUsername());
        assertEquals("newBlackPlayer", retrievedGame.blackUsername());
    }

    @Test
    void negativeTestUpdateGameNonExistent() {
        GameData nonExistentGame = new GameData(-1, "nonExistentWhite", "nonExistentBlack", "GhostGame");
        assertDoesNotThrow(() -> gameDao.updateGame(nonExistentGame),"Updating a non-existent game should not throw an exception.");
    }

    @Test
    void positiveTestListGames() throws DataAccessException {
        GameData game1 = new GameData(0, "whitePlayer1", "blackPlayer1", "TestGame1");
        gameDao.insertGame(game1);
        GameData game2 = new GameData(0, "whitePlayer2", "blackPlayer2", "TestGame2");
        gameDao.insertGame(game2);

        Collection<GameData> games = gameDao.listGames();
        assertNotNull(games);
        assertFalse(games.isEmpty());
        assertTrue(games.size() >= 2);
    }

    @Test
    void positiveTestClear() throws DataAccessException {
        GameData game = new GameData(0, "whitePlayer", "blackPlayer", "TestGame");
        gameDao.insertGame(game);

        gameDao.clear();
        Collection<GameData> games = gameDao.listGames();
        assertTrue(games.isEmpty());
    }
}

