package service;

import dataAccess.DataAccessException;
import dataAccess.IAuthDao;
import dataAccess.IUserDao;
import model.UserData;
import model.AuthData;

public class UserService {

    private IUserDao userDao;
    private IAuthDao authDao;

    public UserService(IUserDao userDao, IAuthDao authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public AuthData register(UserData user) throws DataAccessException {
        // Check if user already exists
        if (userDao.getUser(user.username()) != null) {
            throw new RuntimeException("User already exists.");
        }
        userDao.insertUser(user);
        // Generate auth token and return
        return new AuthData(generateAuthToken(), user.username());
    }

    public AuthData login(UserData user) throws DataAccessException {
        UserData foundUser = userDao.getUser(user.username());
        if (foundUser == null || !foundUser.password().equals(user.password())) {
            throw new RuntimeException("Invalid login.");
        }
        // Generate auth token and return
        return new AuthData(generateAuthToken(), user.username());
    }

    public void logout(String authToken) throws DataAccessException {
        authDao.deleteAuthToken(authToken);
    }

    private String generateAuthToken() {
        // Implement token generation logic
        // This is a placeholder implementation. Ensure you use a secure, unpredictable token generation method.
        return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }

    public void clear(){
        userDao.clear();
    }
}
