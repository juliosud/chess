package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.exceptions.DataAccessException;
import model.GameData;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import static dataAccess.DatabaseManager.configureDatabase;
import static dataAccess.DatabaseManager.executeUpdate;

public class MySqlGameDao implements IGameDao {

    public MySqlGameDao() {
        String[] createTableSQL = {"""
            CREATE TABLE IF NOT EXISTS gameData (
                `gameId` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                `whiteUsername` VARCHAR(255) NULL,
                `blackUsername` VARCHAR(255) NULL,
                `gameName` VARCHAR(255) NOT NULL,
                `gameState` TEXT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
            """};
        try {
            configureDatabase(createTableSQL);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int insertGame(GameData game) throws DataAccessException {
        ChessGame chessGame = new ChessGame(); // check with TAs
        String insertSQL = "INSERT INTO gameData (gameName, gameState) VALUES (?, ?);";
        Gson gson = new Gson();
        int gameId = executeUpdate(insertSQL, game.gameName(), gson.toJson(chessGame));
        return gameId;
    }

    public GameData getGame(int gameId) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameId, whiteUsername, blackUsername, gameName FROM gameData WHERE gameId = ?;";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameId);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve auth token: " + e.getMessage());
        }
        return null;
    }

    private GameData readGame (ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameId");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        //ChessGame gameStatus = gson.fromJson(rs.getString("game"), ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName);
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String updateSQL = "UPDATE gameData SET whiteUsername = ?, blackUsername = ?, gameName = ? WHERE gameId = ?;";
        executeUpdate(updateSQL, game.whiteUsername(), game.blackUsername(), game.gameName(), game.gameID());
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> games = new ArrayList<>();
        String selectSQL = "SELECT gameId, whiteUsername, blackUsername, gameName FROM gameData;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                games.add(new GameData(rs.getInt("gameId"), rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName")));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to list games");
        }
        return games;
    }

    public ChessGame getGameState(int gameId) throws DataAccessException {
        String query = "SELECT gameState FROM gameData WHERE gameId = ?;";
        Gson gson = new Gson();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, gameId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String gameStateJson = rs.getString("gameState");
                    return gson.fromJson(gameStateJson, ChessGame.class);
                } else {
                    throw new DataAccessException("Game with ID " + gameId + " not found.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve game state: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String truncateSQL = "TRUNCATE gameData;";
        executeUpdate(truncateSQL);
    }


}
