package dataAccessTests;

import dataAccess.exceptions.AlreadyTakenException;
import dataAccess.exceptions.BadRequestException;
import dataAccess.exceptions.DataAccessException;
import dataAccess.exceptions.UnauthorizedException;
import dataAccess.MySqlUserDao;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlUserDaoTest {

    private MySqlUserDao userDao;

    @BeforeEach
    void setup() throws DataAccessException {
        userDao = new MySqlUserDao();
        // Clean the userData table before each test
        userDao.clear();
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        // Optionally, clean the userData table after each test
        userDao.clear();
    }

    @Test
    void testInsertUserAndRetrieve() throws DataAccessException {
        UserData user = new UserData("testUser", "testPass", "test@example.com");
        userDao.insertUser(user);

        UserData retrievedUser = userDao.getUser("testUser");
        assertNotNull(retrievedUser);
        assertEquals("testUser", retrievedUser.username());
        assertTrue(new BCryptPasswordEncoder().matches("testPass", retrievedUser.password()));
        assertEquals("test@example.com", retrievedUser.email());
    }

    @Test
    void testGetUserNonExistent() throws DataAccessException {
        UserData user = userDao.getUser("nonExistentUser");
        assertNull(user);
    }

    @Test
    void testPasswordDecoderWithCorrectPassword() throws DataAccessException {
        UserData user = new UserData("testUser", "testPass", "test@example.com");
        userDao.insertUser(user);

        assertTrue(userDao.decoder(new UserData("testUser", "", ""), "testPass"));
    }

    @Test
    void testPasswordDecoderWithIncorrectPassword() throws DataAccessException {
        UserData user = new UserData("testUser", "testPass", "test@example.com");
        userDao.insertUser(user);

        assertFalse(userDao.decoder(new UserData("testUser", "", ""), "wrongPass"));
    }
}
