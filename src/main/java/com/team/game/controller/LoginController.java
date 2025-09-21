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

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label feedbackLabel;

    private GameService svc;
    private Consumer<User> onSuccess;

    @FXML
    private void initialize() { }

    public void setDependencies(GameService svc, Consumer<User> onSuccess) {
        this.svc = svc;
        this.onSuccess = onSuccess;
    }

    @FXML
    private void handleLogin() {
        String u = safe(usernameField.getText());
        char[] pw = toChars(passwordField.getText());
        if (u.isEmpty() || pw.length == 0) {
            feedbackLabel.setText("Enter username and password");
            return;
        }
        try {
            Optional<User> user = svc.login(u, pw);
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

    @FXML
    private void handleRegister() {
        String u = safe(usernameField.getText());
        char[] pw = toChars(passwordField.getText());
        if (u.isEmpty() || pw.length == 0) {
            feedbackLabel.setText("Enter username and password");
            return;
        }
        try {
            User user = svc.register(u, pw);
            onSuccess.accept(user);
            closeWindow();
        } catch (IllegalStateException dup) {
            feedbackLabel.setText("Username already exists or invalid");
        } finally {
            Arrays.fill(pw, '\0');
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        if (stage != null) stage.close();
    }

    private static String safe(String s) { return s == null ? "" : s.trim(); }
    private static char[] toChars(String s) { return s == null ? new char[0] : s.toCharArray(); }
}
