package dataAccess;

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
    public GameData getGame(int gameId) throws DataAccessException {
        GameData game = games.get(gameId);
        if (game == null) {
            throw new DataAccessException("Game not found with ID: " + gameId);
        }
        return game;
    }

    @Override
    public void updateGame(int gameId, GameData game) throws DataAccessException {
        if (!games.containsKey(gameId)) {
            throw new DataAccessException("Cannot update non-existing game with ID: " + gameId);
        }
        games.put(gameId, game);
    }

    @Override
    public void deleteGame(int gameId) throws DataAccessException {
        if (games.remove(gameId) == null) {
            throw new DataAccessException("Cannot delete non-existing game with ID: " + gameId);
        }
    }

//    @Override
//    public List<GameData> listGames() throws DataAccessException {
//        try {
//            return new ArrayList<>(games.values());
//        } catch (Exception e) {
//            throw new DataAccessException("Failed to list games: " + e.getMessage());
//        }
//    }
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

    @Override
    public void joinGame(int gameId, String username, String playerColor) throws DataAccessException {
        try {
            // Placeholder for join game logic
        } catch (Exception e) {
            throw new DataAccessException("Failed to join game: " + e.getMessage());
        }
    }



}

