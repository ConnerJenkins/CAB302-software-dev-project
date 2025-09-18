package com.team.game.controller;

import main.java.com.team.game.model.Question;
import main.java.com.team.game.model.GameSession;
import main.java.com.team.game.model.User;
import main.java.com.team.game.model.GameMode;
import main.java.com.team.game.service.GameService;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
public class BasicsGameControllerTest {

    private BasicsGameController controller;
    private GameService mockGameService;
    private User mockUser;
    private GameSession mockGameSession;

    @BeforeEach
    void setUp() {
        controller = new BasicsGameController();

        // Mock dependencies
        mockGameService = mock(GameService.class);
        mockUser = mock(User.class);
        mockGameSession = mock(GameSession.class);

        // Initialize UI components (they would normally be injected by FXML)
        controller.scoreLabel = new Label();
        controller.strikesLabel = new Label();
        controller.questionLabel = new Label();
        controller.feedbackLabel = new Label();
        controller.optionsContainer = new VBox();
        controller.inputContainer = new HBox();
        controller.option1Button = new Button();
        controller.option2Button = new Button();
        controller.option3Button = new Button();
        controller.option4Button = new Button();
        controller.answerField = new TextField();

        // Set mock dependencies
        controller.gameService = mockGameService;
        controller.currentUser = mockUser;
        controller.currentGameSession = mockGameSession;
    }

    @Test
    void testIsAnswerCorrect_ExactMatch() {
        assertTrue(invokeIsAnswerCorrect("answer", "answer"));
    }

    @Test
    void testIsAnswerCorrect_CaseInsensitive() {
        assertTrue(invokeIsAnswerCorrect("ANSWER", "answer"));
        assertTrue(invokeIsAnswerCorrect("answer", "ANSWER"));
    }

    @Test
    void testIsAnswerCorrect_WhitespaceIgnored() {
        assertTrue(invokeIsAnswerCorrect("  answer  ", "answer"));
        assertTrue(invokeIsAnswerCorrect("answer", "  answer  "));
    }

    @Test
    void testIsAnswerCorrect_DifferentAnswers() {
        assertFalse(invokeIsAnswerCorrect("answer1", "answer2"));
    }

    @Test
    void testIsAnswerCorrect_NumericEquivalents() {
        assertTrue(invokeIsAnswerCorrect("0.5", "0.5"));
        assertFalse(invokeIsAnswerCorrect("0.5", "0.6"));
    }

    @Test
    void testUpdateScoreDisplay() {
        // Set initial score and strikes
        controller.score = 5;
        controller.strikes = 2;

        // Call the method
        controller.updateScoreDisplay();

        // Verify the labels are updated correctly
        assertEquals("Score: 5", controller.scoreLabel.getText());
        assertEquals("Strikes: 2/3", controller.strikesLabel.getText());
    }

    @Test
    void testDisplayCurrentQuestion_MultipleChoice() {
        // Create a multiple choice question
        Question question = new Question(
                "Test question",
                "Correct answer",
                Arrays.asList("Option 1", "Option 2", "Option 3", "Option 4")
        );

        controller.gameQuestions = Arrays.asList(question);
        controller.currentQuestionIndex = 0;
        controller.currentQuestion = question;

        // Call the method
        controller.displayCurrentQuestion();

        // Verify options container is visible and input container is hidden
        assertTrue(controller.optionsContainer.isVisible());
        assertFalse(controller.inputContainer.isVisible());

        // Verify button texts are set correctly
        assertEquals("Option 1", controller.option1Button.getText());
        assertEquals("Option 2", controller.option2Button.getText());
        assertEquals("Option 3", controller.option3Button.getText());
        assertEquals("Option 4", controller.option4Button.getText());
    }

    @Test
    void testDisplayCurrentQuestion_DirectAnswer() {
        // Create a direct answer question
        Question question = new Question("Test question", "Correct answer");

        controller.gameQuestions = Arrays.asList(question);
        controller.currentQuestionIndex = 0;
        controller.currentQuestion = question;

        // Call the method
        controller.displayCurrentQuestion();

        // Verify input container is visible and options container is hidden
        assertTrue(controller.inputContainer.isVisible());
        assertFalse(controller.optionsContainer.isVisible());

        // Verify answer field is cleared
        assertEquals("", controller.answerField.getText());
    }

    @Test
    void testEndGame() {
        // Set initial state
        controller.gameActive = true;
        controller.optionsContainer.setVisible(true);
        controller.inputContainer.setVisible(true);

        // Call the method
        controller.endGame("Game Over!");

        // Verify game state is updated
        assertFalse(controller.gameActive);
        assertFalse(controller.optionsContainer.isVisible());
        assertFalse(controller.inputContainer.isVisible());
        assertEquals("Game Over!", controller.feedbackLabel.getText());
        assertEquals("Game Over!", controller.questionLabel.getText());

        // Verify database interaction
        verify(mockGameService).finishRound(mockGameSession);
    }

    // Helper method to invoke private isAnswerCorrect method
    private boolean invokeIsAnswerCorrect(String userAnswer, String correctAnswer) {
        try {
            Method method = BasicsGameController.class.getDeclaredMethod("isAnswerCorrect", String.class, String.class);
            method.setAccessible(true);
            return (Boolean) method.invoke(controller, userAnswer, correctAnswer);
        } catch (Exception e) {
            fail("Failed to invoke private method: " + e.getMessage());
            return false;
        }
    }

    @Test
    void testInitializeFromLogin_WithUserAndService() {
        // Set up mocks
        when(mockUser.getUsername()).thenReturn("testuser");

        // Call the method
        controller.initializeFromLogin();

        // Verify interactions
        verify(mockUser).getUsername();
        // No exception should be thrown
    }

    @Test
    void testInitializeFromLogin_NoUser() {
        // Set controller to have no user
        controller.currentUser = null;

        // Call the method
        controller.initializeFromLogin();

        // No exception should be thrown
    }

    @Test
    void testInitializeFromLogin_NoService() {
        // Set controller to have no service
        controller.gameService = null;

        // Call the method
        controller.initializeFromLogin();

        // No exception should be thrown
    }
}