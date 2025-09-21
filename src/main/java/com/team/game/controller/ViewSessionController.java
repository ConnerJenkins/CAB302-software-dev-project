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
import main.java.com.team.game.model.GameSession;
import main.java.com.team.game.service.GameService;
import main.java.com.team.game.model.User;

import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ViewSessionController implements Initializable {

    private static final ZoneId LOCAL_TZ = ZoneId.of("Australia/Brisbane");
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

    @FXML
    private TableView<SessionRow> sessionsTable;

    @FXML
    private TableColumn<SessionRow, Integer> idColumn;

    @FXML
    private TableColumn<SessionRow, String> modeColumn;

    @FXML
    private TableColumn<SessionRow, Integer> scoreColumn;

    @FXML
    private TableColumn<SessionRow, Integer> strikesColumn;

    @FXML
    private TableColumn<SessionRow, String> completedColumn;

    @FXML
    private TableColumn<SessionRow, String> startedColumn;

    @FXML
    private TableColumn<SessionRow, String> endedColumn;

    @FXML
    private Label noSessionsLabel;

    @FXML
    private Button refreshButton;

    @FXML
    private Button backButton;

    private GameService gameService;
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get the game service and user from MenuApp
        gameService = Main.MenuApp.getGameService();
        currentUser = Main.MenuApp.getCurrentUser();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        modeColumn.setCellValueFactory(new PropertyValueFactory<>("mode"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        strikesColumn.setCellValueFactory(new PropertyValueFactory<>("strikes"));
        completedColumn.setCellValueFactory(new PropertyValueFactory<>("completed"));
        startedColumn.setCellValueFactory(new PropertyValueFactory<>("started"));
        endedColumn.setCellValueFactory(new PropertyValueFactory<>("ended"));

        loadSessions();
    }

    private void loadSessions() {
        if (gameService == null || currentUser == null) {
            showNoSessions();
            return;
        }

        List<GameSession> sessions = gameService.listSessionsByUser(currentUser);

        if (sessions.isEmpty()) {
            showNoSessions();
        } else {
            hideNoSessions();
            ObservableList<SessionRow> sessionRows = FXCollections.observableArrayList();

            for (GameSession session : sessions) {
                String startedLocal = session.getStartedAt().atZone(LOCAL_TZ).format(DT_FMT);
                String endedLocal = (session.getEndedAt() == null) ? "-" : session.getEndedAt().atZone(LOCAL_TZ).format(DT_FMT);

                SessionRow row = new SessionRow(
                    session.getId(),
                    session.getMode().toString(),
                    session.getScore(),
                    session.getStrikes(),
                    session.isCompleted() ? "Yes" : "No",
                    startedLocal,
                    endedLocal
                );

                sessionRows.add(row);
            }

            sessionsTable.setItems(sessionRows);
        }
    }

    private void showNoSessions() {
        sessionsTable.setVisible(false);
        noSessionsLabel.setVisible(true);
    }

    private void hideNoSessions() {
        sessionsTable.setVisible(true);
        noSessionsLabel.setVisible(false);
    }

    @FXML
    public void handleRefresh(ActionEvent actionEvent) {
        System.out.println("Refreshing sessions...");
        loadSessions();
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

    public static class SessionRow {
        private Integer id;
        private String mode;
        private Integer score;
        private Integer strikes;
        private String completed;
        private String started;
        private String ended;

        public SessionRow(Integer id, String mode, Integer score, Integer strikes,
                         String completed, String started, String ended) {
            this.id = id;
            this.mode = mode;
            this.score = score;
            this.strikes = strikes;
            this.completed = completed;
            this.started = started;
            this.ended = ended;
        }

        public Integer getId() { return id; }
        public String getMode() { return mode; }
        public Integer getScore() { return score; }
        public Integer getStrikes() { return strikes; }
        public String getCompleted() { return completed; }
        public String getStarted() { return started; }
        public String getEnded() { return ended; }

        public void setId(Integer id) { this.id = id; }
        public void setMode(String mode) { this.mode = mode; }
        public void setScore(Integer score) { this.score = score; }
        public void setStrikes(Integer strikes) { this.strikes = strikes; }
        public void setCompleted(String completed) { this.completed = completed; }
        public void setStarted(String started) { this.started = started; }
        public void setEnded(String ended) { this.ended = ended; }
    }
}
