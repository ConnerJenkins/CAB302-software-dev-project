package main.java.com.team.game.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.java.com.team.game.Main;
import main.java.com.team.game.model.User;
import main.java.com.team.game.service.GameService;

import java.net.URL;
import java.util.ResourceBundle;

public class ChangePasswordController implements Initializable {

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button changeButton;

    @FXML
    private Button cancelButton;

    private GameService gameService;
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameService = Main.MenuApp.getGameService();
        currentUser = Main.MenuApp.getCurrentUser();

        confirmPasswordField.setOnAction(this::handleChangePassword);
    }

    @FXML
    public void handleChangePassword(ActionEvent actionEvent) {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (currentPassword.isEmpty()) {
            showStatus("Please enter your current password.", false);
            return;
        }

        if (newPassword.isEmpty()) {
            showStatus("Please enter a new password.", false);
            return;
        }

        if (confirmPassword.isEmpty()) {
            showStatus("Please confirm your new password.", false);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showStatus("New password and confirmation do not match.", false);
            return;
        }

        if (currentPassword.equals(newPassword)) {
            showStatus("New password must be different from current password.", false);
            return;
        }

        try {
            var loginResult = gameService.login(currentUser.getUsername(), currentPassword.toCharArray());
            if (loginResult.isEmpty()) {
                showStatus("Current password is incorrect.", false);
                return;
            }
        } catch (Exception e) {
            showStatus("Current password is incorrect.", false);
            return;
        }

        try {
            char[] npw = newPassword.toCharArray();

            gameService.updatePassword(currentUser, npw);
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();

            showStatus("Password changed.", true);

        } catch (Exception ex) {
            showStatus("Error changing password: " + ex.getMessage(), false);
        }
    }

    @FXML
    public void handleCancel(ActionEvent actionEvent) {
        System.out.println("Cancel password change");
        closeWindow();
    }

    private void showStatus(String message, boolean isSuccess) {
        statusLabel.setText(message);
        statusLabel.setVisible(true);

        if (isSuccess) {
            statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        } else {
            statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #f44336; -fx-font-weight: bold;");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
