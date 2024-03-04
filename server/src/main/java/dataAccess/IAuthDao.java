package dataAccess;

import dataAccess.exceptions.DataAccessException;
import model.AuthData;
import model.UserData;

public interface IAuthDao {
    AuthData insertAuthToken(UserData authToken) throws DataAccessException;
    AuthData getAuthToken(String authToken) throws DataAccessException;
    void deleteAuthToken(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
}
