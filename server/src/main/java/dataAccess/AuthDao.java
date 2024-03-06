package dataAccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;
import dataAccess.exceptions.DataAccessException;
import model.UserData;

public class AuthDao implements IAuthDao {
    private Map<String, AuthData> tokens = new HashMap<>();

    @Override
    public AuthData insertAuthToken(UserData user) throws DataAccessException {
        if (user.username() == null || user.username().isEmpty()) {
            throw new DataAccessException("Failed to insert auth token");
        }
        String authToken = generateAuthToken();
        AuthData newAuth = new AuthData(authToken, user.username());
        tokens.put(authToken, newAuth);
        return newAuth;
    }

    @Override
    public AuthData getAuthToken(String authToken) throws DataAccessException {
        try {
            return tokens.get(authToken);
        } catch (Exception e) {
            throw new DataAccessException("Failed to retrieve auth token: " + e.getMessage());
        }
    }

    @Override
    public void deleteAuthToken(String authToken) throws DataAccessException {
        if (!tokens.containsKey(authToken)) {
            throw new DataAccessException("Auth token not found");
        }
        tokens.remove(authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            tokens.clear();
        } catch (Exception e) {
            throw new DataAccessException("Failed to clear auth tokens: " + e.getMessage());
        }
    }

    private String generateAuthToken() {
        return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }
}