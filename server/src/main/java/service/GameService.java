package service;

import dataAccess.exceptions.DataAccessException;
import dataAccess.IGameDao;
import model.GameData;

import java.util.List;

public class GameService {
    private final IGameDao gameDao;

    public GameService(IGameDao gameDao) {
        this.gameDao = gameDao;
    }

    public GameData createGame(String whiteUsername, String blackUsername, String gameName) throws DataAccessException {
        GameData game = new GameData(0, whiteUsername, blackUsername, gameName); // '0' as placeholder for ID
        int gameId = gameDao.insertGame(game);
        return gameDao.getGame(gameId); // Retrieve the game to include the generated ID
    }

    public GameData getGame(int gameId) throws DataAccessException{
        return gameDao.getGame(gameId);
    }

    public void updateGame(GameData game) throws DataAccessException{
        gameDao.updateGame(game.gameID(), game);
    }

    public void deleteGame(int gameId) throws DataAccessException{
        gameDao.deleteGame(gameId);
    }

    public List<GameData> listGames() {
        return gameDao.listGames();
    }

    void clear(){
        gameDao.clear();
    }

    public void joinGame(int gameId, String username, String playerColor) throws DataAccessException {
    }

}

