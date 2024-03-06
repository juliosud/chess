package service;

import dataAccess.IAuthDao;
import dataAccess.exceptions.AlreadyTakenException;
import dataAccess.exceptions.BadRequestException;
import dataAccess.exceptions.DataAccessException;
import dataAccess.IGameDao;
import dataAccess.exceptions.UnauthorizedException;
import model.GameData;
import model.AuthData;

import java.util.Collection;
import java.util.List;

public class GameService {
    private final IGameDao gameDao;
    private final IAuthDao authDao;


    public GameService(IGameDao gameDao, IAuthDao authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public Integer createGame(String authToken, GameData gameName) throws DataAccessException, UnauthorizedException, BadRequestException {
        if (authToken == null || authToken.isEmpty() || authDao.getAuthToken(authToken) == null) {
            throw new UnauthorizedException("Invalid or expired authToken.");
        }
        if (gameName == null || gameName.gameName() == null || gameName.gameName().isEmpty()) {
            throw new BadRequestException("Game name cannot be empty.");        }

//        GameData game = new GameData(0, null, null, gameName.gameName());
        int gameId;

        try {
            gameId = gameDao.insertGame(gameName);
        } catch (Exception e) {
            throw new DataAccessException("Failed to create a new game: " + e.getMessage());
        }
        return gameDao.getGame(gameId).gameID();
    }

    public Collection<GameData> listGames(String authToken) throws UnauthorizedException, DataAccessException {
        if (authToken == null || authToken.isEmpty() || authDao.getAuthToken(authToken) == null) {
            throw new UnauthorizedException("Invalid or expired authToken.");
        }
        try {
            return gameDao.listGames();
        } catch (Exception e) {
            throw new DataAccessException("Failed to list games: " + e.getMessage());
        }
    }

    public void clear() throws DataAccessException{
        try {
            gameDao.clear();
        } catch (Exception e) {
            throw new DataAccessException("Fail to clear: " + e.getMessage());
        }

    }

    public void joinGame(String authToken, int gameId, String playerColor) throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        if (authToken == null || authToken.isEmpty() || authDao.getAuthToken(authToken) == null) {
            throw new UnauthorizedException("Invalid or expired authToken.");
        }
        AuthData user = authDao.getAuthToken(authToken);
        GameData game = gameDao.getGame(gameId);
        if (game == null) {
            throw new BadRequestException("Game not found with ID: " + gameId);
        }

        boolean updateNeeded = false;
        if ("WHITE".equalsIgnoreCase(playerColor) && (game.whiteUsername() == null || game.whiteUsername().isEmpty())) {
            game = new GameData(game.gameID(), user.username(), game.blackUsername(), game.gameName()); // Assuming authToken or a user identifier from authToken is used as username
            updateNeeded = true;
        } else if ("BLACK".equalsIgnoreCase(playerColor) && (game.blackUsername() == null || game.blackUsername().isEmpty())) {
            game = new GameData(game.gameID(), game.whiteUsername(), user.username(), game.gameName()); // Same assumption as above
            updateNeeded = true;
        } else if (!"WHITE".equalsIgnoreCase(playerColor) && !"BLACK".equalsIgnoreCase(playerColor)) {
            return;
        } else {
            throw new AlreadyTakenException("Requested player slot is already taken."); // Check with the TAs if this is needed
        }

        if (updateNeeded) {
            gameDao.updateGame(gameId, game);
        }
    }
}

