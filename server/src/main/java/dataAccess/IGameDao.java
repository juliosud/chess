package dataAccess;

import dataAccess.exceptions.DataAccessException;
import model.GameData;

import java.util.List;

public interface IGameDao {
    int insertGame(GameData game) throws DataAccessException;
    GameData getGame(int gameId)  throws DataAccessException;
    void updateGame(int gameId, GameData game) throws DataAccessException;
    void deleteGame(int gameId)  throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    void clear()throws DataAccessException;
    void joinGame(int gameId, String username, String playerColor) throws DataAccessException;
}
