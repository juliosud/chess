package dataAccess;

import dataAccess.exceptions.BadRequestException;
import dataAccess.exceptions.DataAccessException;
import model.GameData;
import java.util.Collection;


public interface IGameDao {
    int insertGame(GameData game) throws DataAccessException;
    public GameData getGame(int gameId) throws BadRequestException;
    public void updateGame(GameData game) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void clear()throws DataAccessException;
}
