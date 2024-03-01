package service;

import dataAccess.DataAccessException;
import dataAccess.IAuthDao;
import model.AuthData;
import java.security.SecureRandom;
import java.math.BigInteger;

public class AuthService {
    private final IAuthDao authDao;
    private final SecureRandom random = new SecureRandom();

    public AuthService(IAuthDao authDao) {
        this.authDao = authDao;
    }

    public AuthData createAuthToken(String username) throws DataAccessException {
        // Generate a secure, random token
        String token = new BigInteger(130, random).toString(32);
        AuthData newAuthData = new AuthData(token, username);

        // Insert the new token into the DAO
        authDao.insertAuthToken(newAuthData);
        return newAuthData;
    }

    public boolean validateAuthToken(String token) throws DataAccessException {
        // Check if the token exists and is valid
        AuthData authData = authDao.getAuthToken(token);
        return authData != null;
    }

    public void invalidateAuthToken(String token) throws DataAccessException {
        // Delete the token from the DAO, effectively "logging out" the user
        authDao.deleteAuthToken(token);
    }

    public void clear(){
        authDao.clear();
    }
}



