package dataAccess;

import dataAccess.exceptions.DataAccessException;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dataAccess.DatabaseManager.configureDatabase;
import static dataAccess.DatabaseManager.executeUpdate;

public class MySqlUserDao {
    public MySqlUserDao() throws DataAccessException {
        String[] createTableSQL = {"""
            CREATE TABLE IF NOT EXISTS user (
                `id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                `username` VARCHAR(255) NOT NULL UNIQUE,
                `password` VARCHAR(255) NOT NULL,
                `email` VARCHAR(255) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """};
//        DatabaseManager.executeUpdate(Arrays.toString(createTableSQL));
        try {
            configureDatabase(createTableSQL);
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to create userData table: " + e.getMessage(), e);
        }
    }

    public UserData insertUser(String username, String password, String email) throws DataAccessException {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(password);
        String insertSQL = "INSERT INTO user (username, password, email) VALUES (?, ?, ?);";
//        int userId = executeUpdate(insertSQL, username, hashedPassword, email);
        executeUpdate(insertSQL, username, hashedPassword, email);
        return new UserData(username, hashedPassword, email);
    }

    public UserData getUser(String username) throws DataAccessException {
        String selectSQL = "SELECT id, username, password, email FROM user WHERE username = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectSQL)) {
//             var ps = conn.prepareStatement(selectSQL)) {
            stmt.setString(1, username);
            try (var result = stmt.executeQuery()) {
                if (result.next()) {
                    return new UserData(result.getString("username"), result.getString("password"), result.getString("email"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get user: " + username);
        }
        return null;
    }

    public void clear() throws DataAccessException {
        String truncateSQL = "TRUNCATE user;";
        executeUpdate(truncateSQL);
    }
}
