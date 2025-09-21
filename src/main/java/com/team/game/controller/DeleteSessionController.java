package main.java.com.team.game.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.java.com.team.game.Main;
import main.java.com.team.game.model.GameSession;
import main.java.com.team.game.service.GameService;
import main.java.com.team.game.model.User;

import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DeleteSessionController implements Initializable {

    private static final ZoneId LOCAL_TZ = ZoneId.of("Australia/Brisbane");
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    private ComboBox<SessionItem> sessionComboBox;

    @FXML
    private TextField sessionIdField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button deleteButton;

    @FXML
    private Button refreshButton;

    @FXML
    private Button backButton;

    private GameService gameService;
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameService = Main.MenuApp.getGameService();
        currentUser = Main.MenuApp.getCurrentUser();
        loadSessions();

        sessionComboBox.setOnAction(e -> {
            if (sessionComboBox.getValue() != null) {
                sessionIdField.clear();
            }
        });

        sessionIdField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.trim().isEmpty()) {
                sessionComboBox.setValue(null);
            }
        });
    }

    private void loadSessions() {
        if (gameService == null || currentUser == null) {
            showStatus("Error: Unable to load user data", false);
            return;
        }

        List<GameSession> sessions = gameService.listSessionsByUser(currentUser);
        ObservableList<SessionItem> sessionItems = FXCollections.observableArrayList();

        for (GameSession session : sessions) {
            String startedLocal = session.getStartedAt().atZone(LOCAL_TZ).format(DT_FMT);
            String displayText = String.format("ID: %d | %s | Score: %d | Started: %s",
                session.getId(), session.getMode(), session.getScore(), startedLocal);
            sessionItems.add(new SessionItem(session.getId(), displayText));
        }

        sessionComboBox.setItems(sessionItems);

        if (sessions.isEmpty()) {
            showStatus("No sessions found to delete.", false);
            deleteButton.setDisable(true);
        } else {
            hideStatus();
            deleteButton.setDisable(false);
        }
    }

    @FXML
    public void handleDelete(ActionEvent actionEvent) {
        int sessionId = getSelectedSessionId();

        if (sessionId == -1) {
            showStatus("Please select a session or enter a session ID.", false);
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText("Delete Session");
        confirmationAlert.setContentText("Are you sure you want to delete session ID " + sessionId + "?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = gameService.deleteSession(sessionId);

            if (success) {
                showStatus("Session ID " + sessionId + " deleted successfully.", true);

                loadSessions();
                sessionComboBox.setValue(null);
                sessionIdField.clear();
            } else {
                showStatus("Session ID " + sessionId + " not found.", false);
            }
        }
    }

    @FXML
    public void handleRefresh(ActionEvent actionEvent) {
        System.out.println("Refreshing session list...");
        loadSessions();
        sessionComboBox.setValue(null);
        sessionIdField.clear();
        hideStatus();
    }

    @FXML
    public void handleBack(ActionEvent actionEvent) {
        System.out.println("Back to menu");
        closeWindow();
    }

    private int getSelectedSessionId() {
        SessionItem selectedItem = sessionComboBox.getValue();
        if (selectedItem != null) {
            return selectedItem.getId();
        }

        String idText = sessionIdField.getText().trim();
        if (!idText.isEmpty()) {
            try {
                return Integer.parseInt(idText);
            } catch (NumberFormatException e) {
                showStatus("Invalid session ID format. Please enter a number.", false);
                return -1;
            }
        }

        return -1;
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

    private void hideStatus() {
        statusLabel.setVisible(false);
    }

    private void closeWindow() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    public static class SessionItem {
        private int id;
        private String displayText;

        public SessionItem(int id, String displayText) {
            this.id = id;
            this.displayText = displayText;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return displayText;
        }
    }
}
