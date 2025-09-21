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

public class ChangeUsernameController implements Initializable {

    @FXML
    private Label currentUsernameLabel;

    @FXML
    private TextField newUsernameField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button updateButton;

    @FXML
    private Button cancelButton;

    private GameService gameService;
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameService = Main.MenuApp.getGameService();
        currentUser = Main.MenuApp.getCurrentUser();

        if (currentUser != null) {
            currentUsernameLabel.setText(currentUser.getUsername());
        }

        newUsernameField.setOnAction(this::handleUpdateUsername);
    }

    @FXML
    public void handleUpdateUsername(ActionEvent actionEvent) {
        String newUsername = newUsernameField.getText().trim();

        if (newUsername.isEmpty()) {
            showStatus("Username cannot be empty.", false);
            return;
        }

        if (newUsername.equals(currentUser.getUsername())) {
            showStatus("New username is the same as current username.", false);
            return;
        }

        try {
            gameService.updateUsername(currentUser, newUsername);

            currentUser = new User(currentUser.getId(), newUsername, currentUser.getRegisteredAt());

            Main.MenuApp.setUserData(gameService, currentUser);
            currentUsernameLabel.setText(newUsername);
            newUsernameField.clear();

            showStatus("Updated.", true);

        } catch (IllegalStateException ex) {
            showStatus("That name is taken.", false);
        } catch (Exception ex) {
            showStatus("Error updating username: " + ex.getMessage(), false);
        }
    }

    @FXML
    public void handleCancel(ActionEvent actionEvent) {
        System.out.println("Cancel username change");
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
