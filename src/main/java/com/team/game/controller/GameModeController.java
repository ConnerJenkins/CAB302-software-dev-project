package main.java.com.team.game.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import main.java.com.team.game.Main;
import main.java.com.team.game.ui.Windows;

public class GameModeController {

    @FXML
    private Button basicsButton;

    @FXML
    private Button trigButton;

    @FXML
    private Button targetButton;

    @FXML
    private Button backButton;

    @FXML
    public void handleBasicsMode(ActionEvent actionEvent) {
        System.out.println("Basics Mode selected");
        // Launch Basics Game
        Windows.openBasics(Main.MenuApp.getGameService(), Main.MenuApp.getCurrentUser());
        closeWindow();
    }

    @FXML
    public void handleTrigMode(ActionEvent actionEvent) {
        System.out.println("Trig Mode selected");
        // Launch Trig Game
        Windows.openTrig(Main.MenuApp.getGameService(), Main.MenuApp.getCurrentUser());
        closeWindow();
    }

    @FXML
    public void handleTargetMode(ActionEvent actionEvent) {
        System.out.println("Target Mode selected");
        // Launch Target Game
        Windows.openTarget(Main.MenuApp.getGameService(), Main.MenuApp.getCurrentUser());
        closeWindow();
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
}
