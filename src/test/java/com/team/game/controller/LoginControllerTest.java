package test.java.com.team.game.controller;

import main.java.com.team.game.controller.LoginController;
import main.java.com.team.game.service.GameService;
import main.java.com.team.game.model.User;
import main.java.com.team.game.data.GameStore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LoginController}.
 * Verifies registration and login behaviour with various credential scenarios.
 */
public class LoginControllerTest {

    /** Controller under test. */
    private LoginController controller;

    /** Real {@link GameService} backed by in-memory {@link GameStore}. */
    private GameService gameService;

    /**
     * Sets up a fresh controller and service before each test,
     * ensuring isolated state and dependency injection.
     */
    @BeforeEach
    public void setUp() {
        controller = new LoginController();
        gameService = new GameService(new GameStore());
        controller.setDependencies(gameService, user -> {});
    }

    /** Ensures a new user can be successfully registered. */
    @Test
    void testRegisterUser_NewUser() {
        String username = "testUser123";
        char[] password = "password123".toCharArray();

        User result = controller.registerUser(username, password);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertTrue(result.getId() > 0);
        assertNotNull(result.getRegisteredAt());
    }

    /** Confirms duplicate usernames trigger an {@link IllegalStateException}. */
    @Test
    void testRegisterUser_DuplicateUsername() {
        String username = "duplicateUser";
        char[] password = "password123".toCharArray();

        controller.registerUser(username, password);
        assertThrows(IllegalStateException.class, () -> {
            controller.registerUser(username, password);
        });
    }

    /** Verifies valid credentials allow a successful login. */
    @Test
    void testCheckUser_ValidCredentials() {
        String username = "validUser";
        char[] password = "validPassword".toCharArray();

        controller.registerUser(username, password);

        Optional<User> result = controller.checkUser(username, password);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
    }

    /** Ensures non-existent users cannot log in. */
    @Test
    void testCheckUser_InvalidCredentials() {
        String username = "nonexistentUser";
        char[] password = "wrongPassword".toCharArray();

        Optional<User> result = controller.checkUser(username, password);

        assertFalse(result.isPresent());
    }

    /** Verifies that incorrect passwords prevent login even for existing users. */
    @Test
    void testCheckUser_WrongPassword() {
        String username = "testUser";
        char[] correctPassword = "correctPassword".toCharArray();
        char[] wrongPassword = "wrongPassword".toCharArray();

        controller.registerUser(username, correctPassword);

        Optional<User> result = controller.checkUser(username, wrongPassword);
        assertFalse(result.isPresent());
    }
}
