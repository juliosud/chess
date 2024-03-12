package dataAccess;

import chess.ChessGame;
import dataAccess.exceptions.DataAccessException;
import model.GameData;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import static dataAccess.DatabaseManager.configureDatabase;
import static dataAccess.DatabaseManager.executeUpdate;
import com.google.gson.Gson;

public class MySqlGameDao implements IGameDao {

    public MySqlGameDao() throws DataAccessException {
        String[] createTableSQL = {"""
            CREATE TABLE IF NOT EXISTS game (
                `gameId` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                `whiteUsername` VARCHAR(255) NOT NULL,
                `blackUsername` VARCHAR(255) NOT NULL,
                `gameName` VARCHAR(255) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """};
        try {
            configureDatabase(createTableSQL);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int insertGame(GameData game) throws DataAccessException {
        String insertSQL = "INSERT INTO gameData (gameName) VALUES (?);";
        int gameId = executeUpdate(insertSQL, game.gameName());
        return gameId;
    }

//    @Override
//    public GameData getGame(int gameId) throws DataAccessException {
//        String selectSQL = "SELECT gameId, whiteUsername, blackUsername, gameName FROM gameData WHERE gameId = ?;";
//        try (Connection conn = DatabaseManager.getConnection();
//             PreparedStatement ps = conn.prepareStatement(selectSQL)) {
//            ps.setInt(1, gameId);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    return new GameData(rs.getInt("gameId"), rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"));
//                }
//            }
//        } catch (SQLException e) {
//            throw new DataAccessException("Unable to get game with ID: " + gameId, e);
//        }
//        return null;
//    }

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
        String updateSQL = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ? WHERE gameId = ?;";
        executeUpdate(updateSQL, game.whiteUsername(), game.blackUsername(), game.gameName(), game.gameID());
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> games = new ArrayList<>();
        String selectSQL = "SELECT gameId, whiteUsername, blackUsername, gameName FROM game;";
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

    @Override
    public void clear() throws DataAccessException {
        String truncateSQL = "TRUNCATE game;";
        executeUpdate(truncateSQL);
    }
}
