package test.java.com.team.game.controller;

import main.java.com.team.game.controller.BasicsGameController;
import main.java.com.team.game.service.GameService;
import main.java.com.team.game.model.User;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link BasicsGameController}.
 * Verifies answer-checking logic, normalization behaviour, and dependency setup.
 */
public class BasicsControllerTest {

    /** Controller instance under test. */
    private final BasicsGameController controller = new BasicsGameController();

    /** Ensures multiple-choice answers match exactly. */
    @Test
    void testIsAnswerCorrect_MultipleChoiceExactMatch() {
        assertTrue(controller.isAnswerCorrect("v = u + at", "v = u + at", true));
    }

    /** Verifies case insensitivity for multiple-choice answers. */
    @Test
    void testIsAnswerCorrect_MultipleChoiceCaseInsensitive() {
        assertTrue(controller.isAnswerCorrect("V = U + AT", "v = u + at", true));
    }

    /** Verifies whitespace is ignored for multiple-choice answers. */
    @Test
    void testIsAnswerCorrect_MultipleChoiceWhitespaceInsensitive() {
        assertTrue(controller.isAnswerCorrect("  v=u+at  ", "v = u + at", true));
    }

    /** Confirms exact numeric string matches are accepted. */
    @Test
    void testIsAnswerCorrect_NumericExactMatch() {
        assertTrue(controller.isAnswerCorrect("5", "5", false));
    }

    /** Checks that numeric answers within ±0.01 are treated as correct. */
    @Test
    void testIsAnswerCorrect_NumericWithTolerance() {
        assertTrue(controller.isAnswerCorrect("5.01", "5", false));
        assertTrue(controller.isAnswerCorrect("4.99", "5", false));
    }

    /** Ensures numeric answers outside ±0.01 are rejected. */
    @Test
    void testIsAnswerCorrect_NumericOutsideTolerance() {
        assertFalse(controller.isAnswerCorrect("5.02", "5", false));
        assertFalse(controller.isAnswerCorrect("4.98", "5", false));
    }

    /** Confirms numeric equivalence detection (e.g., 0.5 vs 1/2). */
    @Test
    void testIsAnswerCorrect_NumericDecimalEquivalence() {
        assertTrue(controller.isAnswerCorrect("0.5", "1/2", false));
    }

    /** Tests normalization of multiple-choice input (lowercasing and whitespace removal). */
    @Test
    void testNormalizeAnswer_MultipleChoice() throws Exception {
        var method = BasicsGameController.class.getDeclaredMethod("normalizeAnswer", String.class, boolean.class);
        method.setAccessible(true);

        String result = (String) method.invoke(controller, "  V = U + AT  ", true);
        assertEquals("v=u+at", result);
    }

    /** Tests normalization for numeric answers (should stay unchanged). */
    @Test
    void testNormalizeAnswer_Numeric() throws Exception {
        var method = BasicsGameController.class.getDeclaredMethod("normalizeAnswer", String.class, boolean.class);
        method.setAccessible(true);

        String result = (String) method.invoke(controller, "  5.0  ", false);
        assertEquals("5.0", result);
    }

    /** Ensures dependencies can be injected and logic remains functional. */
    @Test
    void testSetDependencies_WithUserAndService() {
        GameService mockService = mock(GameService.class);
        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn("Alice");

        controller.setDependencies(mockService, mockUser);

        assertTrue(controller.isAnswerCorrect("5", "5", false));
    }

    /** Ensures controller still operates when no user is provided. */
    @Test
    void testSetDependencies_NoUser() {
        GameService mockService = mock(GameService.class);

        controller.setDependencies(mockService, null);

        assertTrue(controller.isAnswerCorrect("5", "5", false));
    }
}
