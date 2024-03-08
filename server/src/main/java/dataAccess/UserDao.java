package dataAccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;
import dataAccess.exceptions.DataAccessException;

public class UserDao implements IUserDao {
    private Map<String, UserData> users = new HashMap<>();

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("User already exists with username: " + user.username());
        }
        try {
            users.put(user.username(), user);
        } catch (Exception e) {
            throw new DataAccessException("Failed to insert user: " + e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try {
            return users.get(username);
        } catch (Exception e) {
            throw new DataAccessException("Failed to retrieve user: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            users.clear();
        } catch (Exception e) {
            throw new DataAccessException("Failed to clear users: " + e.getMessage());
        }
    }
}