package dataAccess;

import model.UserData;

public interface IUserDao {
    void insertUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void deleteUser(String username) throws DataAccessException;
    void clear();
}
