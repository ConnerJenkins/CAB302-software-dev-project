package main.java.com.team.game.controller;

import javafx.event.ActionEvent;
import main.java.com.team.game.Main;
import main.java.com.team.game.ui.Windows;

public class MenuController {

    public void handleStartRound(ActionEvent actionEvent) {
        System.out.println("Start Round clicked");
        Windows.openGameMode(Main.MenuApp.getGameService(), Main.MenuApp.getCurrentUser());
    }

    public void handleMySessions(ActionEvent actionEvent) {
        System.out.println("My Sessions clicked");
        Windows.openViewSessions(Main.MenuApp.getGameService(), Main.MenuApp.getCurrentUser());
    }

    public void handleDeleteSession(ActionEvent actionEvent) {
        System.out.println("Delete Session clicked");
        Windows.openDeleteSession(Main.MenuApp.getGameService(), Main.MenuApp.getCurrentUser());
    }

    public void handleChangeUsername(ActionEvent actionEvent) {
        System.out.println("Change Username clicked");
        Windows.openChangeUsername(Main.MenuApp.getGameService(), Main.MenuApp.getCurrentUser());
    }

    public void handleChangePassword(ActionEvent actionEvent) {
        System.out.println("Change Password clicked");
        Windows.openChangePassword(Main.MenuApp.getGameService(), Main.MenuApp.getCurrentUser());
    }

    public void handleDeleteAccount(ActionEvent actionEvent) {
        System.out.println("Delete Account clicked");
        Windows.openDeleteAccount(Main.MenuApp.getGameService(), Main.MenuApp.getCurrentUser());
    }

    public void handleLeaderboard(ActionEvent actionEvent) {
        System.out.println("Leaderboard clicked");
        Windows.openLeaderboard(Main.MenuApp.getGameService(), Main.MenuApp.getCurrentUser());
    }

    public void handleListUsers(ActionEvent actionEvent) {
        System.out.println("List Users clicked");
        Windows.openUsersList(Main.MenuApp.getGameService(), Main.MenuApp.getCurrentUser());
    }

    public void handleExit(ActionEvent actionEvent) {
        System.out.println("Exit clicked");
        System.exit(0);
    }
}
