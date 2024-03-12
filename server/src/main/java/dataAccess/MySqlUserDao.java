package dataAccess;

import dataAccess.exceptions.DataAccessException;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlUserDao {

    public MySqlUserDao() throws DataAccessException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS user (
                `id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                `username` VARCHAR(255) NOT NULL UNIQUE,
                `password` VARCHAR(255) NOT NULL,
                `email` VARCHAR(255) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """;
        DatabaseManager.executeUpdate(createTableSQL);
    }

    public UserData insertUser(String username, String password, String email) throws DataAccessException {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(password);
        String insertSQL = "INSERT INTO user (username, password, email) VALUES (?, ?, ?);";
        int userId = DatabaseManager.executeUpdate(insertSQL, username, hashedPassword, email);
        return new UserData(userId, username, hashedPassword, email);
    }

    public UserData getUser(String username) throws DataAccessException {
        String selectSQL = "SELECT id, username, password, email FROM user WHERE username = ?;";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(selectSQL)) {
            ps.setString(1, username);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserData(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get user: " + username, e);
        }
        return null;
    }

    public void clear() throws DataAccessException {
        String truncateSQL = "TRUNCATE user;";
        DatabaseManager.executeUpdate(truncateSQL);
    }
}
