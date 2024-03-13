package service;

import dataAccess.exceptions.AlreadyTakenException;
import dataAccess.exceptions.BadRequestException;
import dataAccess.exceptions.DataAccessException;
import dataAccess.exceptions.UnauthorizedException;
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

    public AuthData register(UserData user) throws DataAccessException, BadRequestException, AlreadyTakenException {
        if (user == null || user.username() == null || user.username().isEmpty() ||
                user.password() == null || user.password().isEmpty() ||
                user.email() == null || user.email().isEmpty()) {
            throw new BadRequestException("Username, password, and email cannot be empty.");
        }

        UserData existingUser = userDao.getUser(user.username());
        if (existingUser != null) {
            throw new AlreadyTakenException("User already exists.");
        }

        try {
            userDao.insertUser(user);
        } catch (Exception e) {
            throw new DataAccessException("Failed to insert the new user: " + e.getMessage());
        }

        AuthData newAuthData = authDao.insertAuthToken(user);
        return newAuthData;
    }

    public AuthData login(UserData user) throws DataAccessException, UnauthorizedException {
        if (user == null || user.username() == null || user.username().isEmpty()
                || user.password() == null || user.password().isEmpty()) {
            throw new UnauthorizedException("Username and password must be provided.");
        }

        UserData foundUser = userDao.getUser(user.username());
        if (foundUser == null || !userDao.decoder(user, user.password())) {
            throw new UnauthorizedException("Invalid username or password.");
        }

        try {
            return authDao.insertAuthToken(user);
        } catch (Exception e) {
            throw new DataAccessException("An error occurred while logging in: " + e.getMessage());
        }
    }

    public void logout(String authToken) throws DataAccessException, UnauthorizedException {
        if (authDao.getAuthToken(authToken) == null || authToken.isEmpty()){
            throw new UnauthorizedException("Invalid or expired authToken.");
        }
        try {
            authDao.deleteAuthToken(authToken);
        } catch (Exception e) {
            throw new DataAccessException("An error occurred while logging out. " + e.getMessage());
        }
    }

    public void clear() throws DataAccessException{
        try {
            userDao.clear();
            authDao.clear();
        } catch (Exception e) {
            throw new DataAccessException("An error occurred while Clearing: " + e.getMessage());
        }
    }
}
