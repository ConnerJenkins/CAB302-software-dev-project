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
import main.java.com.team.game.model.User;
import main.java.com.team.game.service.GameService;

import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class UsersListController implements Initializable {

    private static final ZoneId LOCAL_TZ = ZoneId.of("Australia/Brisbane");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    private TableView<UserRow> usersTable;

    @FXML
    private TableColumn<UserRow, Integer> idColumn;

    @FXML
    private TableColumn<UserRow, String> usernameColumn;

    @FXML
    private TableColumn<UserRow, String> registeredColumn;

    @FXML
    private Label noUsersLabel;

    @FXML
    private Label totalUsersLabel;

    @FXML
    private Button refreshButton;

    @FXML
    private Button backButton;

    private GameService gameService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameService = Main.MenuApp.getGameService();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        registeredColumn.setCellValueFactory(new PropertyValueFactory<>("registeredAt"));

        loadUsers();
    }

    private void loadUsers() {
        if (gameService == null) {
            showNoUsers();
            return;
        }

        List<User> users = gameService.listUsers();

        if (users.isEmpty()) {
            showNoUsers();
        } else {
            showUsers(users);
        }
    }

    private void showUsers(List<User> users) {
        noUsersLabel.setVisible(false);

        ObservableList<UserRow> userRows = FXCollections.observableArrayList();

        for (User user : users) {
            String formattedDate = user.getRegisteredAt().atZone(LOCAL_TZ).format(DATE_FORMATTER);

            UserRow row = new UserRow(
                user.getId(),
                user.getUsername(),
                formattedDate
            );

            userRows.add(row);
        }

        usersTable.setItems(userRows);
        usersTable.setVisible(true);

        totalUsersLabel.setText("Total users: " + users.size());
        totalUsersLabel.setVisible(true);
    }

    private void showNoUsers() {

        usersTable.setVisible(false);
        noUsersLabel.setVisible(true);
        totalUsersLabel.setText("Total users: 0");
        totalUsersLabel.setVisible(true);
    }

    @FXML
    public void handleRefresh(ActionEvent actionEvent) {
        System.out.println("Refreshing users list...");
        loadUsers();
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

    public static class UserRow {
        private Integer id;
        private String username;
        private String registeredAt;

        public UserRow(Integer id, String username, String registeredAt) {
            this.id = id;
            this.username = username;
            this.registeredAt = registeredAt;
        }

        public Integer getId() { return id; }
        public String getUsername() { return username; }
        public String getRegisteredAt() { return registeredAt; }

        public void setId(Integer id) { this.id = id; }
        public void setUsername(String username) { this.username = username; }
        public void setRegisteredAt(String registeredAt) { this.registeredAt = registeredAt; }
    }
}
