package main.java.com.team.game.controller;

import main.java.com.team.game.model.Question;
import main.java.com.team.game.model.QuestionBank;
import main.java.com.team.game.model.GameMode;
import main.java.com.team.game.model.GameSession;
import main.java.com.team.game.model.User;
import main.java.com.team.game.service.GameService;
import main.java.com.team.game.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BasicsGameController implements Initializable {
    @FXML
    private Label scoreLabel;

    @FXML
    private Label strikesLabel;

    @FXML
    private Label questionLabel;

    @FXML
    private Label feedbackLabel;

    @FXML
    private VBox optionsContainer;

    @FXML
    private HBox inputContainer;

    @FXML
    private Button option1Button;

    @FXML
    private Button option2Button;

    @FXML
    private Button option3Button;

    @FXML
    private Button option4Button;

    @FXML
    private TextField answerField;

    // Game state variables
    private List<Question> gameQuestions;
    private QuestionBank questionBank;
    private GameService gameService;
    private User currentUser;
    private GameSession currentGameSession;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int strikes = 0;
    private Question currentQuestion;
    private boolean gameActive = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get the logged-in user and GameService from Main
        initializeFromLogin();

        // Initialize questions from QuestionBank
        initializeQuestionsFromBank();

        // Start a new game session in the database
        startGameSession();

        // Display the first question
        displayCurrentQuestion();
    }

    private void initializeFromLogin() {
        gameService = Main.BasicsApp.getGameService();
        currentUser = Main.BasicsApp.getCurrentUser();

        if (currentUser != null) {
            System.out.println("Using logged-in user: " + currentUser.getUsername());
        } else {
            System.err.println("No user logged in! This should not happen.");
        }

        if (gameService == null) {
            System.err.println("GameService not available! This should not happen.");
        }
    }

    private void startGameSession() {
        if (gameService != null && currentUser != null) {
            try {
                currentGameSession = gameService.startRound(currentUser, GameMode.BASICS);
                System.out.println("Started new game session with ID: " + currentGameSession.getId());
            } catch (Exception e) {
                System.err.println("Failed to start game session: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void initializeQuestionsFromBank() {
        questionBank = new QuestionBank();
        gameQuestions = new ArrayList<>();
        gameQuestions.addAll(questionBank.getBasics());
        System.out.println("Loaded " + gameQuestions.size() + " questions from QuestionBank");
    }

    private void displayCurrentQuestion() {
        if (currentQuestionIndex < gameQuestions.size()) {
            currentQuestion = gameQuestions.get(currentQuestionIndex);
            questionLabel.setText("Question " + (currentQuestionIndex + 1) + ": " + currentQuestion.getText());

            // Show appropriate input method based on question type
            if (currentQuestion.getOptions() != null && !currentQuestion.getOptions().isEmpty()) {
                // Multiple choice question
                optionsContainer.setVisible(true);
                inputContainer.setVisible(false);

                // Set option text
                List<String> options = currentQuestion.getOptions();
                option1Button.setText(options.get(0));
                option2Button.setText(options.get(1));
                option3Button.setText(options.get(2));
                option4Button.setText(options.get(3));
            } else {
                // Direct answer question
                optionsContainer.setVisible(false);
                inputContainer.setVisible(true);
                answerField.clear();
            }
        } else {
            endGame("Game Finished! Final Score: " + score + "/" + gameQuestions.size());
        }
    }

    @FXML
    public void handleOptionSelect(ActionEvent event) {
        if (!gameActive) return;

        Button selectedButton = (Button) event.getSource();
        String selectedAnswer = selectedButton.getText();
        checkAnswer(selectedAnswer);
    }

    @FXML
    public void handleSubmitAnswer(ActionEvent event) {
        if (!gameActive) return;

        String userAnswer = answerField.getText().trim();
        if (userAnswer.isEmpty()) {
            feedbackLabel.setText("Please enter an answer!");
            feedbackLabel.setStyle("-fx-text-fill: orange;");
            return;
        }

        checkAnswer(userAnswer);
    }

    private void checkAnswer(String userAnswer) {
        boolean isCorrect = isAnswerCorrect(userAnswer, currentQuestion.getAnswer());

        if (isCorrect) {
            score++;
            feedbackLabel.setText("Correct!");
            feedbackLabel.setStyle("-fx-text-fill: green;");

            // Save correct answer to database
            if (gameService != null && currentGameSession != null) {
                try {
                    gameService.submitCorrect(currentGameSession);
                } catch (Exception e) {
                    System.err.println("Failed to save correct answer: " + e.getMessage());
                }
            }
        } else {
            strikes++;
            feedbackLabel.setText("Wrong! The correct answer is: " + currentQuestion.getAnswer());
            feedbackLabel.setStyle("-fx-text-fill: red;");

            // Save wrong answer to database
            if (gameService != null && currentGameSession != null) {
                try {
                    gameService.submitWrong(currentGameSession);
                } catch (Exception e) {
                    System.err.println("Failed to save wrong answer: " + e.getMessage());
                }
            }

            // Check if game should end due to strikes
            if (strikes >= 3) {
                endGame("Game Over! You reached 3 strikes. Final Score: " + score + "/" + gameQuestions.size());
                return;
            }
        }

        // Update UI
        updateScoreDisplay();

        // Move to next question after a short delay
        currentQuestionIndex++;
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(2), e -> {
                    if (gameActive) {
                        displayCurrentQuestion();
                        feedbackLabel.setText("");
                    }
                })
        );
        timeline.play();
    }

    private boolean isAnswerCorrect(String userAnswer, String correctAnswer) {
        // Normalize answers for comparison
        String normalizedUserAnswer = userAnswer.toLowerCase().replaceAll("\\s+", "");
        String normalizedCorrectAnswer = correctAnswer.toLowerCase().replaceAll("\\s+", "");

        return normalizedUserAnswer.equals(normalizedCorrectAnswer);
    }

    private void updateScoreDisplay() {
        scoreLabel.setText("Score: " + score);
        strikesLabel.setText("Strikes: " + strikes + "/3");
    }

    private void endGame(String message) {
        gameActive = false;

        // Finish the game session in the database
        if (gameService != null && currentGameSession != null) {
            try {
                gameService.finishRound(currentGameSession);
                System.out.println("Finished game session. Final Score: " + score);
            } catch (Exception e) {
                System.err.println("Failed to finish game session: " + e.getMessage());
            }
        }

        // Show final message
        feedbackLabel.setText(message);
        feedbackLabel.setStyle("-fx-text-fill: blue; -fx-font-size: 20px;");

        // Disable inputs
        optionsContainer.setVisible(false);
        inputContainer.setVisible(false);
        questionLabel.setText("Game Over!");
    }

    // Test helper method to set dependencies
    public void setDependencies(GameService gameService, User user) {
        this.gameService = gameService;
        this.currentUser = user;
    }
}