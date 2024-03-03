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
        // Validate the input data for null values or empty strings
        if (user == null || user.username() == null || user.username().trim().isEmpty() ||
                user.password() == null || user.password().trim().isEmpty() ||
                user.email() == null || user.email().trim().isEmpty()) {
            throw new BadRequestException("Username, password, and email cannot be empty.");
        }

        // Check if the user already exists
        UserData existingUser = userDao.getUser(user.username());
        if (existingUser != null) {
            throw new AlreadyTakenException("User already exists.");
        }

        // Insert the new user
        try {
            userDao.insertUser(user);
        } catch (DataAccessException e) {
            throw new DataAccessException("Failed to insert the new user: " + e.getMessage());
        }

        // Generate auth token and return
        AuthData newAuthData = new AuthData(generateAuthToken(), user.username());
        return newAuthData;
    }

    public AuthData login(UserData user) throws DataAccessException, UnauthorizedException {
        // Validate input
        if (user == null || user.username() == null || user.username().isEmpty()
                || user.password() == null || user.password().isEmpty()) {
            throw new UnauthorizedException("Username and password must be provided.");
        }

        // Attempt to retrieve the user
        UserData foundUser = userDao.getUser(user.username());
        if (foundUser == null || !foundUser.password().equals(user.password())) {
            throw new UnauthorizedException("Invalid username or password.");
        }

        try {
            // User is authenticated, generate and return a new authToken
            String authToken = generateAuthToken();
            return new AuthData(authToken, user.username());
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
            throw new DataAccessException("An error occurred while logging in: " + e.getMessage());
        }

    }

    private String generateAuthToken() {
        return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }

    public void clear(){
        userDao.clear();
    }
}
