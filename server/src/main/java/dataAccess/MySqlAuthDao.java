package dataAccess;

import dataAccess.exceptions.DataAccessException;
import model.AuthData;
import model.UserData;

import java.sql.*;
import java.util.UUID;

import static dataAccess.DatabaseManager.configureDatabase;
import static dataAccess.DatabaseManager.executeUpdate;

public class MySqlAuthDao implements IAuthDao {

    public MySqlAuthDao() {
        String[] createTableSQL = {"""
            CREATE TABLE IF NOT EXISTS authData (
                `username` VARCHAR(255) NOT NULL,
                `authToken` VARCHAR(255) NOT NULL,
                PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """};
        try {
            DatabaseManager.configureDatabase(createTableSQL);
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to create authData table: " + e.getMessage(), e);
        }
    }

    @Override
    public AuthData insertAuthToken(UserData user) throws DataAccessException {
        if (user.username() == null || user.username().isEmpty()) {
            throw new DataAccessException("Failed to insert auth token: username is null or empty");
        }
        try {
            String authToken = generateAuthToken();
            var statement = "INSERT INTO authData (username, authToken) VALUES (?, ?);";
            executeUpdate(statement, user.username(), authToken);
            return new AuthData(authToken, user.username());
        } catch (Exception e) {
            throw new DataAccessException("Failed to insert auth token: " + e.getMessage());
        }
    }

    @Override
    public AuthData getAuthToken(String authToken) throws DataAccessException {
        var selectSQL = "SELECT username, authToken FROM authData WHERE authToken = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectSQL)) {
            stmt.setString(1, authToken);
            ResultSet resultStmt = stmt.executeQuery();
            if (resultStmt.next()) {
                return new AuthData(resultStmt.getString("authToken"), resultStmt.getString("username"));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve auth token: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAuthToken(String authToken) throws DataAccessException {
        var deleteSQL = "DELETE FROM authData WHERE authToken = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSQL)) {
            stmt.setString(1, authToken);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Auth token not found: " + authToken);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete auth token: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String truncateSQL = "TRUNCATE TABLE authData;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(truncateSQL)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear auth tokens: " + e.getMessage());
        }
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
