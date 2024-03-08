package dataAccess;

import dataAccess.exceptions.DataAccessException;
import model.UserData;

public interface IUserDao {
    void insertUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void clear() throws DataAccessException;
}
