package test.java.com.team.game.controller;


import main.java.com.team.game.controller.TrigoFunController;
import main.java.com.team.game.service.GameService;
import main.java.com.team.game.model.User;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TrigoControllerTest {

    private final TrigoFunController controller = new TrigoFunController();

    @Test
    void testIsAnswerCorrect_ExactMatch() {
        assertTrue(controller.isAnswerCorrect("√2/2", "√2/2"));
    }

    @Test
    void testIsAnswerCorrect_DifferentWhitespace() {
        assertTrue(controller.isAnswerCorrect(" √2 / 2 ", "√2/2"));
    }

    @Test
    void testIsAnswerCorrect_DecimalEquivalent() {
        assertTrue(controller.isAnswerCorrect("0.5", "1/2"));
    }

    @Test
    void testIsAnswerCorrect_SqrtNotation() {
        assertTrue(controller.isAnswerCorrect("sqrt(2)/2", "√2/2"));
        assertTrue(controller.isAnswerCorrect("0.707", "√2/2"));
    }

    @Test
    void testIsAnswerCorrect_IncorrectAnswer() {
        assertFalse(controller.isAnswerCorrect("0.6", "√2/2"));
    }

    @Test
    void testNormalizeAnswer() throws Exception {
        var method = TrigoFunController.class.getDeclaredMethod("normalizeAnswer", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(controller, " sqrt(3) ");
        assertEquals("√3", result);
    }

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

    @Test
    void testInitializeFromLogin_NoUser() {
        GameService mockService = mock(GameService.class);

        TrigoFunController controller = new TrigoFunController();
        controller.setDependencies(mockService, null);

        controller.initializeFromLogin();

    }


}