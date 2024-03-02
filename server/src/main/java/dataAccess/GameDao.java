package dataAccess;

import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class GameDao implements IGameDao {
    private final Map<Integer, GameData> games = new HashMap<>();
    private final AtomicInteger gameIdCounter = new AtomicInteger();

    @Override
    public int insertGame(GameData game) {
        int gameId = gameIdCounter.incrementAndGet();
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

    @Override
    public List<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public void clear() {
        games.clear();
        gameIdCounter.set(0);
    }

    @Override
    public void joinGame(int gameId, String username, String playerColor) throws DataAccessException {
    }


}

