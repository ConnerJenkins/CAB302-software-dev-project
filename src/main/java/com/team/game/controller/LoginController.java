package main.java.com.team.game.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.java.com.team.game.model.User;
import main.java.com.team.game.service.GameService;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Controller for the Login screen.
 * <p>
 * Handles user authentication and registration, validating input
 * and delegating logic to {@link GameService}. On success, passes
 * the logged-in user to a callback so the next view can be loaded.
 */
public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label feedbackLabel;

    private GameService svc;
    private Consumer<User> onSuccess;

    /**
     * JavaFX lifecycle hook. No initialization logic required at load.
     */
    @FXML
    private void initialize() { }

    /**
     * Injects the dependencies required for login handling.
     *
     * @param svc        the game service used for authentication and registration
     * @param onSuccess  callback invoked when a user successfully logs in or registers
     */
    public void setDependencies(GameService svc, Consumer<User> onSuccess) {
        this.svc = svc;
        this.onSuccess = onSuccess;
    }

    /**
     * Handles user login.
     * <p>
     * Validates credentials, attempts authentication via {@link GameService#login(String, char[])},
     * and invokes {@code onSuccess} if successful. Clears the password array after use.
     */
    @FXML
    private void handleLogin() {
        String u = safe(usernameField.getText());
        char[] pw = toChars(passwordField.getText());
        if (u.isEmpty() || pw.length == 0) {
            feedbackLabel.setText("Enter username and password");
            return;
        }
        try {
            Optional<User> user = checkUser(u, pw);
            if (user.isPresent()) {
                onSuccess.accept(user.get());
                closeWindow();
            } else {
                feedbackLabel.setText("Incorrect username or password");
            }
        } finally {
            Arrays.fill(pw, '\0');
        }
    }

    /**
     * Handles user registration.
     * <p>
     * Validates input and attempts to create a new account via {@link GameService#register(String, char[])}.
     * On success, immediately logs the user in and closes the window.
     */
    @FXML
    private void handleRegister() {
        String u = safe(usernameField.getText());
        char[] pw = toChars(passwordField.getText());
        if (u.isEmpty() || pw.length == 0) {
            feedbackLabel.setText("Enter username and password");
            return;
        }
        try {
            User user = registerUser(u, pw);
            onSuccess.accept(user);
            closeWindow();
        } catch (IllegalStateException dup) {
            feedbackLabel.setText("Username already exists or invalid");
        } finally {
            Arrays.fill(pw, '\0');
        }
    }

    /**
     * Validates credentials and checks if the user exists.
     *
     * @param username the username to verify
     * @param password the password entered
     * @return an {@link Optional} containing the authenticated user if valid
     */
    public Optional<User> checkUser(String username, char[] password) {
        return svc.login(username, password);
    }

    /**
     * Registers a new user account.
     *
     * @param username the desired username
     * @param password the chosen password
     * @return a {@link User} object representing the newly registered account
     * @throws IllegalStateException if the username already exists or is invalid
     */
    public User registerUser(String username, char[] password) throws IllegalStateException {
        return svc.register(username, password);
    }

    /**
     * Closes the login window after successful login or registration.
     */
    private void closeWindow() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        if (stage != null) stage.close();
    }

    /**
     * Safely trims a string, returning an empty string if null.
     */
    private static String safe(String s) { return s == null ? "" : s.trim(); }

    /**
     * Converts a string to a char array, returning an empty array if null.
     */
    private static char[] toChars(String s) { return s == null ? new char[0] : s.toCharArray(); }
}
