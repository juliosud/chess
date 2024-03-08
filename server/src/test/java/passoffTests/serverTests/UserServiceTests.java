package java.passoffTests.serverTests;

import dataAccess.*;
import dataAccess.exceptions.*;
import model.*;
import service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    private UserService userService;
    private UserDao userDao;
    private AuthDao authDao;

    @BeforeEach
    void setup() {
        userDao = new UserDao();
        authDao = new AuthDao();
        userService = new UserService(userDao, authDao);
    }

    @Test
    void registerPositiveTest() throws DataAccessException, BadRequestException, AlreadyTakenException {
        UserData user = new UserData("user1", "password", "email@example.com");
        AuthData authData = userService.register(user);
        assertNotNull(authData);
        assertEquals("user1", authData.username());
    }

    @Test
    void registerNegativeTest() {
        UserData user = new UserData("", "", "");
        assertThrows(BadRequestException.class, () -> userService.register(user));
    }

    @Test
    void loginPositiveTest() throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        UserData user = new UserData("user1", "password", "email@example.com");
        userService.register(user);
        AuthData authData = userService.login(new UserData("user1", "password", null));
        assertNotNull(authData);
    }

    @Test
    void loginNegativeTest() throws DataAccessException, BadRequestException, AlreadyTakenException {
        UserData user = new UserData("user1", "password", "email@example.com");
        userService.register(user);
        assertThrows(UnauthorizedException.class, () -> userService.login(new UserData("user1", "wrongpassword", null)));
    }

    @Test
    void logoutPositiveTest() throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        UserData user = new UserData("user1", "password", "email@example.com");
        AuthData authData = userService.register(user);
        userService.logout(authData.authToken());
    }

    @Test
    void logoutNegativeTest() throws DataAccessException {
        assertThrows(UnauthorizedException.class, () -> userService.logout("invalidToken"));
    }

    @Test
    void clearPositiveTest() throws DataAccessException {
        userService.clear();
    }
}
