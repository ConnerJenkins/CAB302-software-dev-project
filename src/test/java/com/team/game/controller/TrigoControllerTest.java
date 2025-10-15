package test.java.com.team.game.controller;

import main.java.com.team.game.controller.TrigoFunController;
import main.java.com.team.game.service.GameService;
import main.java.com.team.game.model.User;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link TrigoFunController}.
 * Verifies trigonometric answer validation, normalization, and initialization logic.
 */
public class TrigoControllerTest {

    /** Controller instance under test. */
    private final TrigoFunController controller = new TrigoFunController();

    /** Ensures identical answers are recognized as correct. */
    @Test
    void testIsAnswerCorrect_ExactMatch() {
        assertTrue(controller.isAnswerCorrect("√2/2", "√2/2"));
    }

    /** Verifies that extra whitespace is ignored when comparing answers. */
    @Test
    void testIsAnswerCorrect_DifferentWhitespace() {
        assertTrue(controller.isAnswerCorrect(" √2 / 2 ", "√2/2"));
    }

    /** Confirms that decimal and fractional equivalents are treated as equal. */
    @Test
    void testIsAnswerCorrect_DecimalEquivalent() {
        assertTrue(controller.isAnswerCorrect("0.5", "1/2"));
    }

    /** Checks that different square-root notations (e.g., sqrt vs √) are normalized correctly. */
    @Test
    void testIsAnswerCorrect_SqrtNotation() {
        assertTrue(controller.isAnswerCorrect("sqrt(2)/2", "√2/2"));
        assertTrue(controller.isAnswerCorrect("0.707", "√2/2"));
    }

    /** Ensures incorrect numeric values are properly rejected. */
    @Test
    void testIsAnswerCorrect_IncorrectAnswer() {
        assertFalse(controller.isAnswerCorrect("0.6", "√2/2"));
    }

    /** Tests normalization logic to ensure whitespace and sqrt() are replaced with proper symbols. */
    @Test
    void testNormalizeAnswer() throws Exception {
        var method = TrigoFunController.class.getDeclaredMethod("normalizeAnswer", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(controller, " sqrt(3) ");
        assertEquals("√3", result);
    }

    /** Verifies controller initialization when both user and service are provided. */
    @Test
    void testInitializeFromLogin_WithUserAndService() {
        GameService mockService = mock(GameService.class);
        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn("Alice");

        TrigoFunController controller = new TrigoFunController();
        controller.setDependencies(mockService, mockUser);

        controller.initializeFromLogin();

        verify(mockUser).getUsername(); // ensure username was accessed
    }

    /** Ensures controller can initialize even if no user is supplied. */
    @Test
    void testInitializeFromLogin_NoUser() {
        GameService mockService = mock(GameService.class);

        TrigoFunController controller = new TrigoFunController();
        controller.setDependencies(mockService, null);

        controller.initializeFromLogin();
    }
}
