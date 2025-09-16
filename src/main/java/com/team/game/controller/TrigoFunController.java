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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TrigoFunController implements Initializable {
    @FXML
    private Label scoreLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label questionLabel;

    @FXML
    private ImageView questionImage;

    @FXML
    private ImageView clockImage;

    @FXML
    private TextField answerField;

    @FXML
    private Label feedbackLabel;

    // List of questions from QuestionBank
    private List<Question> gameQuestions;
    private QuestionBank questionBank;

    // Database integration
    private GameService gameService;
    private User currentUser;
    private GameSession currentGameSession;

    // Game state variables
    private int currentQuestionIndex = 0;
    private int totalScore = 0;
    private int currentStrikes = 0;
    private int highestStrikes = 0;
    private Question currentQuestion;

    // Timer variables
    private Timeline gameTimer;
    private int timeRemaining = 300;
    private boolean gameActive = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get the logged-in user and GameService from Main
        initializeFromLogin();

        // Load the clock image when the controller is initialized
        loadClockImage();

        // Initialize questions from QuestionBank
        initializeQuestionsFromBank();

        // Start a new game session in the database
        startGameSession();

        // Display the first question
        displayCurrentQuestion();

        // Start the game timer
        startGameTimer();
    }

    // Method to get the logged-in user and GameService from Main.java
    private void initializeFromLogin() {
        gameService = Main.TrigoApp.getGameService();
        currentUser = Main.TrigoApp.getCurrentUser();

        if (currentUser != null) {
            System.out.println("Using logged-in user: " + currentUser.getUsername());
        } else {
            System.err.println("No user logged in! This should not happen.");
        }

        if (gameService == null) {
            System.err.println("GameService not available! This should not happen.");
        }
    }

    // Method to start a new game session in the database
    private void startGameSession() {
        if (gameService != null && currentUser != null) {
            try {
                currentGameSession = gameService.startRound(currentUser, GameMode.TRIG);
                System.out.println("Started new game session with ID: " + currentGameSession.getId());
            } catch (Exception e) {
                System.err.println("Failed to start game session: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Method to initialize questions from QuestionBank
    private void initializeQuestionsFromBank() {
        questionBank = new QuestionBank();
        gameQuestions = new ArrayList<>();

        gameQuestions.addAll(questionBank.getTrigo());

        System.out.println("Loaded " + gameQuestions.size() + " questions from QuestionBank");
    }

    // Method to load the clock image
    private void loadClockImage() {
        try {
            String imagePath = "/images/clock1.png";
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            clockImage.setImage(image);
            System.out.println("Clock image loaded successfully");
        } catch (Exception e) {
            System.err.println("Failed to load clock image");
            e.printStackTrace();
        }
    }

    // Method to load an image from the main.resources/images folder
    public void loadQuestionImage(String imageName) {
        try {
            String imagePath = "/images/" + imageName;
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            questionImage.setImage(image);
            System.out.println("Image loaded successfully: " + imageName);
        } catch (Exception e) {
            System.err.println("Failed to load image: " + imageName);
            e.printStackTrace();
            questionImage.setImage(null);
        }
    }

    @FXML
    public void handleSubmitAnswer(ActionEvent actionEvent) {

        if (!gameActive) {
            return;
        }

        String userAnswer = answerField.getText().trim();

        if (userAnswer.isEmpty()) {
            feedbackLabel.setText("Please enter an answer!");
            feedbackLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 18px; -fx-font-weight: bold;");
            return;
        }

        // Check if the answer is correct
        if (isAnswerCorrect(userAnswer, currentQuestion.getAnswer())) {
            totalScore++;
            currentStrikes++;
            updateScoreDisplay();
            feedbackLabel.setText("Correct!");
            feedbackLabel.setStyle("-fx-text-fill: green; -fx-font-size: 18px; -fx-font-weight: bold;");

            // Save correct answer to database
            if (gameService != null && currentGameSession != null) {
                try {
                    gameService.submitCorrect(currentGameSession);
                    System.out.println("Saved correct answer to database. Session ID: " + currentGameSession.getId() + ", Consecutive correct: " + currentStrikes);
                } catch (Exception e) {
                    System.err.println("Failed to save correct answer: " + e.getMessage());
                }
            }
        } else {
            if (currentStrikes > highestStrikes) {
                highestStrikes = currentStrikes;
            }
            currentStrikes = 0;
            feedbackLabel.setText("Wrong! Correct answer: " + currentQuestion.getAnswer());
            feedbackLabel.setStyle("-fx-text-fill: red; -fx-font-size: 18px; -fx-font-weight: bold;");

            if (gameService != null && currentGameSession != null) {
                try {
                    gameService.submitWrong(currentGameSession);
                    System.out.println("Saved wrong answer to database. Session ID: " + currentGameSession.getId() + ", Consecutive correct reset to: " + currentStrikes);
                } catch (Exception e) {
                    System.err.println("Failed to save wrong answer: " + e.getMessage());
                }
            }
        }

        // Clear the answer field
        answerField.clear();

        // Move to next question after a short delay
        currentQuestionIndex++;
        if (currentQuestionIndex < gameQuestions.size()) {
            // Use Timeline to delay showing next question
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(2), e -> {
                        if (gameActive) {
                            displayCurrentQuestion();
                            feedbackLabel.setText("");
                        }
                    })
            );
            timeline.play();
        } else {
            // Game finished - all questions completed
            endGame("Game Finished! Final Score: " + totalScore + "/" + gameQuestions.size());
        }
    }

    // Method to display the current question
    private void displayCurrentQuestion() {
        if (currentQuestionIndex < gameQuestions.size()) {
            currentQuestion = gameQuestions.get(currentQuestionIndex);
            questionLabel.setText("Question " + (currentQuestionIndex + 1) + ": " + currentQuestion.getText());
            loadQuestionImage("triangle.png"); // Load default triangle image
        }
    }

    // Method to check if the answer is correct (with some flexibility for different formats)
    private boolean isAnswerCorrect(String userAnswer, String correctAnswer) {
        String normalizedUserAnswer = normalizeAnswer(userAnswer);
        String normalizedCorrectAnswer = normalizeAnswer(correctAnswer);

        return normalizedUserAnswer.equals(normalizedCorrectAnswer);
    }

    // Method to normalize answers for flexible matching
    private String normalizeAnswer(String answer) {
        return answer.toLowerCase()
                .replaceAll("\\s+", "")  // Remove all whitespace
                .replace("sqrt(2)/2", "√2/2")  // Handle sqrt notation
                .replace("sqrt(3)/3", "√3/3")
                .replace("sqrt(3)", "√3")
                .replace("0.5", "1/2")  // Handle decimal equivalents
                .replace("0.707", "√2/2")  // Approximate decimal for √2/2
                .replace("1.732", "√3")  // Approximate decimal for √3
                .replace("0.577", "√3/3");  // Approximate decimal for √3/3
    }

    // Method to update the score display
    private void updateScoreDisplay() {
        scoreLabel.setText("Total Score: " + totalScore);
    }

    // Timer methods
    private void startGameTimer() {
        updateTimeDisplay();

        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            updateTimeDisplay();

            // Check if time is up
            if (timeRemaining <= 0) {
                endGame("Time's Up! Final Score: " + totalScore + "/" + gameQuestions.size());
            }
        }));

        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();
    }

    private void updateTimeDisplay() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        String timeText = String.format("%d:%02d", minutes, seconds);
        timeLabel.setText(timeText);

        if (timeRemaining <= 30) {
            timeLabel.setStyle("-fx-text-fill: red; -fx-font-family: 'Verdana'; -fx-font-size: 20px; -fx-font-weight: bold;");
        } else if (timeRemaining <= 60) {
            timeLabel.setStyle("-fx-text-fill: orange; -fx-font-family: 'Verdana'; -fx-font-size: 20px; -fx-font-weight: bold;");
        } else {
            timeLabel.setStyle("-fx-text-fill: #B56L37; -fx-font-family: 'Verdana'; -fx-font-size: 20px; -fx-font-weight: bold;");
        }
    }

    private void endGame(String message) {
        gameActive = false;

        // Finish the game session in the database
        if (gameService != null && currentGameSession != null) {
            try {
                gameService.finishRound(currentGameSession);
                System.out.println("Finished game session in database. Session ID: " + currentGameSession.getId() +
                                 ", Final Score: " + totalScore + ", Total Strikes: " + highestStrikes);
            } catch (Exception e) {
                System.err.println("Failed to finish game session: " + e.getMessage());
            }
        }

        // Stop the timer
        if (gameTimer != null) {
            gameTimer.stop();
        }

        // Disable input
        answerField.setDisable(true);

        // Show final message
        feedbackLabel.setText(message);
        feedbackLabel.setStyle("-fx-text-fill: blue; -fx-font-size: 20px; -fx-font-weight: bold;");

        // Hide question and image
        questionLabel.setText("Game Over!");
        questionImage.setImage(null);

        System.out.println("Game ended: " + message);
    }
}
