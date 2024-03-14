package dataAccessTests;

import dataAccess.MySqlUserDao;
import dataAccess.exceptions.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

public class MySqlUserDaoTest {

    private MySqlUserDao userDao;

    @BeforeEach
    void setup() throws DataAccessException {
        userDao = new MySqlUserDao();
        userDao.clear();
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        userDao.clear();
    }

    @Test
    void positiveTestInsertUser() throws DataAccessException {
        UserData user = new UserData("testUser", "testPass", "test@example.com");
        assertDoesNotThrow(() -> userDao.insertUser(user), "Inserting a user should not throw an exception.");
    }

    @Test
    void positiveTestGetUser() throws DataAccessException {
        UserData user = new UserData("testUser", "testPass", "test@example.com");
        userDao.insertUser(user);
        UserData retrievedUser = userDao.getUser("testUser");
        assertNotNull(retrievedUser, "Retrieved user should not be null.");
        assertEquals("testUser", retrievedUser.username(), "Username should match.");
        assertTrue(new BCryptPasswordEncoder().matches("testPass", retrievedUser.password()), "Passwords should match.");
    }

    @Test
    void negativeTestGetUser() throws DataAccessException {
        UserData user = userDao.getUser("nonExistentUser");
        assertNull(user, "Non-existent user retrieval should return null.");
    }

    @Test
    void positiveTestDecoder() throws DataAccessException {
        UserData user = new UserData("testUser", "testPass", "test@example.com");
        userDao.insertUser(user);
        assertTrue(userDao.decoder(new UserData("testUser", "", ""), "testPass"), "Password should match for existing user.");
    }

    @Test
    void negativeTestDecoder() throws DataAccessException {
        UserData user = new UserData("testUser", "testPass", "test@example.com");
        userDao.insertUser(user);
        assertFalse(userDao.decoder(new UserData("testUser", "", ""), "wrongPass"), "Decoder should return false for incorrect password.");
    }

    @Test
    void positiveTestClear() throws DataAccessException {
        UserData user = new UserData("testUser", "testPass", "test@example.com");
        userDao.insertUser(user);
        userDao.clear();
        UserData userAfterClear = userDao.getUser("testUser");
        assertNull(userAfterClear, "User data should be cleared.");
    }
}

