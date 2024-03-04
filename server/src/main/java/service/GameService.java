package service;

import dataAccess.IAuthDao;
import dataAccess.exceptions.BadRequestException;
import dataAccess.exceptions.DataAccessException;
import dataAccess.IGameDao;
import dataAccess.exceptions.UnauthorizedException;
import model.GameData;
import model.AuthData;

import java.util.List;

public class GameService {
    private final IGameDao gameDao;
    private final IAuthDao authDao;

    public GameService(IGameDao gameDao, IAuthDao authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public Integer createGame(String authToken, GameData gameName) throws DataAccessException, UnauthorizedException, BadRequestException {
        // Validate authToken
        if (authToken == null || authToken.isEmpty() || authDao.getAuthToken(authToken) == null) {
            throw new UnauthorizedException("Invalid or expired authToken.");
        }

        // Validate gameName or any other game data
        if (gameName == null || gameName.gameName() == null || gameName.gameName().isEmpty()) {
            throw new BadRequestException("Game name cannot be empty.");        }

        // Assuming GameData requires only a gameName for simplification
        // Generate a new game ID in the DAO layer, not here, to keep business logic out of the service layer
        GameData game = new GameData(0, null, null, gameName.gameName()); // Placeholder '0' for ID, assuming DAO assigns real ID

        int gameId;
        try {
            gameId = gameDao.insertGame(game);
        } catch (Exception e) {
            throw new DataAccessException("Failed to create a new game: " + e.getMessage());
        }

        // Retrieve and return the newly created game with its assigned ID
        return gameDao.getGame(gameId).gameID();
    }

    public List<GameData> listGames(String authToken) throws UnauthorizedException, DataAccessException {
        // Validate the authToken
        if (authToken == null || authToken.isEmpty() || authDao.getAuthToken(authToken) == null) {
            throw new UnauthorizedException("Invalid or expired authToken.");
        }

        try {
            return gameDao.listGames();
        } catch (Exception e) {
            // Wrap any unexpected exceptions into a DataAccessException
            throw new DataAccessException("Failed to list games: " + e.getMessage());
        }
    }

    void clear() throws DataAccessException{
        gameDao.clear();
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





    public void joinGame(int gameId, String username, String playerColor) throws DataAccessException {
    }

}

