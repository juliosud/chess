package dataAccess;

import dataAccess.exceptions.DataAccessException;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.sql.*;
import static dataAccess.DatabaseManager.configureDatabase;
import static dataAccess.DatabaseManager.executeUpdate;

public class MySqlUserDao implements IUserDao{
    public MySqlUserDao() {
        String[] createTableSQL = {"""
            CREATE TABLE IF NOT EXISTS userData (
                `userId` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                `username` VARCHAR(255) NOT NULL,
                `password` VARCHAR(255) NOT NULL,
                `email` VARCHAR(255) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """};
        try {
            configureDatabase(createTableSQL);
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to create userData table: " + e.getMessage(), e);
        }
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException{
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(user.password());
        var insertSQL = "INSERT INTO userData (username, password, email) VALUES (?, ?, ?);";
        DatabaseManager.executeUpdate(insertSQL, user.username(), hashedPassword, user.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String selectSQL = "SELECT * FROM userData WHERE username = ?;";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(selectSQL)) {
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

    @Override
    public boolean decoder (UserData user, String password) throws DataAccessException {
        UserData username = getUser(user.username());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(password, username.password());
    }

    @Override
    public void clear() throws DataAccessException {
        String truncateSQL = "TRUNCATE userData;";
        executeUpdate(truncateSQL);
    }
}
