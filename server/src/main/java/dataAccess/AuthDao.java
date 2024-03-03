package dataAccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;
import dataAccess.exceptions.DataAccessException;

public class AuthDao implements IAuthDao {
    private Map<String, AuthData> tokens = new HashMap<>();

    @Override
    public void insertAuthToken(AuthData authToken) throws DataAccessException {
        try {
            tokens.put(authToken.authToken(), authToken);
        } catch (Exception e) {
            throw new DataAccessException("Failed to insert auth token: " + e.getMessage());
        }
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
        try {
            if (tokens.remove(authToken) == null) {
                throw new DataAccessException("Auth token not found: " + authToken);
            }
        } catch (Exception e) {
            throw new DataAccessException("Failed to delete auth token: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            tokens.clear();
        } catch (Exception e) {
            throw new DataAccessException("Failed to clear auth tokens: " + e.getMessage());
        }
    }
}
