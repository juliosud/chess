package dataAccessTests;

import dataAccess.exceptions.DataAccessException;
import dataAccess.MySqlAuthDao;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MySqlAuthDaoTest {

    private MySqlAuthDao authDao;

    @BeforeEach
    void setup() throws DataAccessException {
        authDao = new MySqlAuthDao();
        authDao.clear();
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        authDao.clear();
    }

    @Test
    void positiveTestInsertAuthToken() throws DataAccessException {
        UserData user = new UserData("username", "password", "email@example.com");
        AuthData authData = authDao.insertAuthToken(user);
        assertNotNull(authData);
        assertEquals(user.username(), authData.username());
        assertNotNull(authData.authToken());
    }

    @Test
    void negativeTestInsertAuthToken() {
        UserData invalidUser = new UserData(null, "password", "email@example.com");
        assertThrows(DataAccessException.class, () -> authDao.insertAuthToken(invalidUser));
    }

    @Test
    void positiveTestGetAuthToken() throws DataAccessException {
        UserData user = new UserData("username", "password", "email@example.com");
        AuthData insertedAuthData = authDao.insertAuthToken(user);
        AuthData retrievedAuthData = authDao.getAuthToken(insertedAuthData.authToken());
        assertNotNull(retrievedAuthData);
        assertEquals(insertedAuthData.authToken(), retrievedAuthData.authToken());
        assertEquals(user.username(), retrievedAuthData.username());
    }

    @Test
    void negativeTestGetAuthToken() throws DataAccessException {
        AuthData authData = authDao.getAuthToken("nonexistentToken");
        assertNull(authData);
    }

    @Test
    void positiveTestDeleteAuthToken() throws DataAccessException {
        UserData user = new UserData("username", "password", "email@example.com");
        AuthData authData = authDao.insertAuthToken(user);
        assertDoesNotThrow(() -> authDao.deleteAuthToken(authData.authToken()));
        AuthData deletedAuthData = authDao.getAuthToken(authData.authToken());
        assertNull(deletedAuthData);
    }

    @Test
    void negativeTestDeleteAuthToken() {
        assertDoesNotThrow(() -> authDao.deleteAuthToken("nonexistentToken"));
    }
}
