package dataAccess;

import dataAccess.exceptions.DataAccessException;
import model.AuthData;

public interface IAuthDao {
    void insertAuthToken(AuthData authToken) throws DataAccessException;
    AuthData getAuthToken(String authToken) throws DataAccessException;
    void deleteAuthToken(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
}
