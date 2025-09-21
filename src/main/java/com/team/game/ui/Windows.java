package main.java.com.team.game.ui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.com.team.game.Main;
import main.java.com.team.game.model.User;
import main.java.com.team.game.service.GameService;

import main.java.com.team.game.controller.LoginController;


public final class Windows {
    private Windows() {}

    public static void openTrig(GameService svc, User user) {
        FxRuntime.ensureStarted();
        Platform.runLater(() -> {
            try {
                Main.TrigoApp.setUserData(svc, user);
                Parent root = FXMLLoader.load(Main.TrigoApp.class.getResource("/trigofun/trigofun-main.fxml"));
                Stage s = new Stage();
                s.setTitle("Trig Game");
                s.setScene(new Scene(root));
                s.show();
            } catch (Exception e) {
                System.err.println("Error opening Trig game window: " + e.getMessage());
            }
        });
    }

    public static void openTarget(GameService svc, User user) {
        FxRuntime.ensureStarted();
        Platform.runLater(() -> {
            try {
                Main.TargetApp.setUserData(svc, user);
                Parent root = FXMLLoader.load(Main.TargetApp.class.getResource("/target/target-main.fxml"));
                Stage s = new Stage();
                s.setTitle("Target Game");
                s.setScene(new Scene(root));
                s.show();
            } catch (Exception e) {
                System.err.println("Error opening Target game window: " + e.getMessage());
            }
        });
    }

    public static void openLogin(GameService svc, java.util.function.Consumer<User> onSuccess) {
        FxRuntime.ensureStarted();
        javafx.application.Platform.runLater(() -> {
            try {
                javafx.fxml.FXMLLoader loader =
                        new javafx.fxml.FXMLLoader(Windows.class.getResource("/login/login.fxml"));
                javafx.scene.Parent root = loader.load();
                main.java.com.team.game.controller.LoginController c = loader.getController();
                c.setDependencies(svc, onSuccess);
                javafx.stage.Stage s = new javafx.stage.Stage();
                s.setTitle("Sign in");
                s.setScene(new javafx.scene.Scene(root));
                s.show();
            } catch (Exception e) {
                System.err.println("Error opening login window: " + e.getMessage());
            }
        });
    }
}
