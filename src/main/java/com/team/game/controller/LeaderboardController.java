package main.java.com.team.game.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.java.com.team.game.Main;
import main.java.com.team.game.model.GameMode;
import main.java.com.team.game.model.ScoreRow;
import main.java.com.team.game.service.GameService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LeaderboardController implements Initializable {

    @FXML
    private Button basicsButton;

    @FXML
    private Button trigButton;

    @FXML
    private Button targetButton;

    @FXML
    private Button backButton;

    @FXML
    private Label selectedModeLabel;

    @FXML
    private TableView<LeaderboardRow> leaderboardTable;

    @FXML
    private TableColumn<LeaderboardRow, Integer> rankColumn;

    @FXML
    private TableColumn<LeaderboardRow, String> usernameColumn;

    @FXML
    private TableColumn<LeaderboardRow, Integer> highScoreColumn;

    @FXML
    private Label noScoresLabel;

    @FXML
    private Label instructionLabel;

    private GameService gameService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameService = Main.MenuApp.getGameService();

        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        highScoreColumn.setCellValueFactory(new PropertyValueFactory<>("highScore"));
    }

    @FXML
    public void handleBasicsLeaderboard(ActionEvent actionEvent) {
        System.out.println("Basics leaderboard selected");
        loadLeaderboard(GameMode.BASICS);
    }

    @FXML
    public void handleTrigLeaderboard(ActionEvent actionEvent) {
        System.out.println("Trig leaderboard selected");
        loadLeaderboard(GameMode.TRIG);
    }

    @FXML
    public void handleTargetLeaderboard(ActionEvent actionEvent) {
        System.out.println("Target leaderboard selected");
        loadLeaderboard(GameMode.TARGET);
    }

    private void loadLeaderboard(GameMode mode) {
        if (gameService == null) {
            showNoScores();
            return;
        }

        selectedModeLabel.setText(mode.toString() + " LEADERBOARD");
        selectedModeLabel.setVisible(true);
        instructionLabel.setVisible(false);

        List<ScoreRow> rows = gameService.leaderboard(mode, 10);

        if (rows.isEmpty()) {
            showNoScores();
        } else {
            showLeaderboard(rows);
        }
    }

    private void showLeaderboard(List<ScoreRow> scoreRows) {
        noScoresLabel.setVisible(false);

        ObservableList<LeaderboardRow> leaderboardRows = FXCollections.observableArrayList();

        int rank = 1;
        for (ScoreRow scoreRow : scoreRows) {
            LeaderboardRow row = new LeaderboardRow(rank, scoreRow.getUsername(), scoreRow.getHighScore());
            leaderboardRows.add(row);
            rank++;
        }

        leaderboardTable.setItems(leaderboardRows);
        leaderboardTable.setVisible(true);
    }

    private void showNoScores() {
        leaderboardTable.setVisible(false);
        noScoresLabel.setVisible(true);
    }

    @FXML
    public void handleBack(ActionEvent actionEvent) {
        System.out.println("Back to menu");
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    public static class LeaderboardRow {
        private Integer rank;
        private String username;
        private Integer highScore;

        public LeaderboardRow(Integer rank, String username, Integer highScore) {
            this.rank = rank;
            this.username = username;
            this.highScore = highScore;
        }

        public Integer getRank() { return rank; }
        public String getUsername() { return username; }
        public Integer getHighScore() { return highScore; }

        public void setRank(Integer rank) { this.rank = rank; }
        public void setUsername(String username) { this.username = username; }
        public void setHighScore(Integer highScore) { this.highScore = highScore; }
    }
}
