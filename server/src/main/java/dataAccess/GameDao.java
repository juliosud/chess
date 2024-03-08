package dataAccess;

import dataAccess.exceptions.BadRequestException;
import dataAccess.exceptions.DataAccessException;
import model.GameData;
import java.util.*;

public class GameDao implements IGameDao {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int gameIdCounter = 1;

    @Override
    public int insertGame(GameData game) {
        int gameId = gameIdCounter++;
        games.put(gameId, new GameData(gameId, game.whiteUsername(), game.blackUsername(), game.gameName()));
        return gameId;
    }

    @Override
    public GameData getGame(int gameId) throws BadRequestException {
        try {
            GameData game = games.get(gameId);
            return game;
        } catch (Exception e){
            throw new BadRequestException("Invalid Id");
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.gameID())) {
            throw new DataAccessException("Cannot update non-existing game with ID: " + game.gameID());
        }
        games.put(game.gameID(), game);
    }

    @Override
    public Collection<GameData> listGames () {
        return games.values();
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            games.clear();
            gameIdCounter = 1;
        } catch (Exception e) {
            throw new DataAccessException("Failed to clear games: " + e.getMessage());
        }
    }
}