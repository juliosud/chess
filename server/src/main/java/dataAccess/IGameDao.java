package dataAccess;

import dataAccess.exceptions.BadRequestException;
import dataAccess.exceptions.DataAccessException;
import model.GameData;

import java.util.Collection;
import java.util.List;

public interface IGameDao {
    int insertGame(GameData game) throws DataAccessException;
    public GameData getGame(int gameId) throws DataAccessException, BadRequestException;
    public void updateGame(GameData game) throws DataAccessException;
    void deleteGame(int gameId)  throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void clear()throws DataAccessException;
    void joinGame(int gameId, String username, String playerColor) throws DataAccessException;
}
