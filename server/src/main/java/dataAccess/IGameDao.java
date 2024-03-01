package dataAccess;

import model.GameData;

import java.util.List;

public interface IGameDao {
    int insertGame(GameData game) throws DataAccessException;
    GameData getGame(int gameId)  throws DataAccessException;
    void updateGame(int gameId, GameData game) throws DataAccessException;
    void deleteGame(int gameId)  throws DataAccessException;
    List<GameData> listGames();
    void clear();
    void joinGame() throws DataAccessException;
}
