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

public class GameService {
    private final IGameDao gameDao;
    private final IAuthDao authDao;


    public GameService(IGameDao gameDao, IAuthDao authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public Integer createGame(String authToken, GameData gameData) throws DataAccessException, UnauthorizedException, BadRequestException {
        if (authToken == null || authToken.isEmpty() || authDao.getAuthToken(authToken) == null) {
            throw new UnauthorizedException("Invalid or expired authToken.");
        }
        if (gameData == null || gameData.gameName() == null || gameData.gameName().isEmpty()) {
            throw new BadRequestException("Game name cannot be empty.");
        }

        int gameId;
        try {
            gameId = gameDao.insertGame(gameData);
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

    public void clear() throws DataAccessException {
        try {
            gameDao.clear();
        } catch (Exception e) {
            throw new DataAccessException("Fail to clear: " + e.getMessage());
        }
    }

    public void joinGame(String authToken, int gameId, String playerColor) throws DataAccessException, BadRequestException, UnauthorizedException, AlreadyTakenException {
        AuthData auth = authDao.getAuthToken(authToken);
        if (auth == null || !auth.authToken().equals(authToken)) {
            throw new UnauthorizedException("Invalid auth token");
        }

        GameData game = gameDao.getGame(gameId);
        if (game == null) {
            throw new BadRequestException("Game not found");
        }

        if (playerColor != null) {
            if (("WHITE".equals(playerColor) && game.whiteUsername() != null) || ("BLACK".equals(playerColor) && game.blackUsername() != null)) {
                throw new AlreadyTakenException("Color is already taken");
            }
            if ("WHITE".equals(playerColor)) {
                gameDao.updateGame(new GameData(gameId, auth.username(), game.blackUsername(), game.gameName()));
            } else if ("BLACK".equals(playerColor)) {
                gameDao.updateGame(new GameData(gameId, game.whiteUsername(), auth.username(), game.gameName()));
            } else {
                throw new BadRequestException("Wrong Color");
            }
        } else {
            return;
        }
    }
}