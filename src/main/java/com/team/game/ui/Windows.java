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

    public static void openMenu(GameService svc, User user) {
        FxRuntime.ensureStarted();
        Platform.runLater(() -> {
            try {
                Main.MenuApp.setUserData(svc, user);
                Parent root = FXMLLoader.load(Main.MenuApp.class.getResource("/menu/menu-main.fxml"));
                Stage s = new Stage();
                s.setTitle("Main Menu");
                s.setScene(new Scene(root));
                s.show();
            } catch (Exception e) {
                System.err.println("Error opening Menu window: " + e.getMessage());
            }
        });
    }

    public static void openGameMode(GameService svc, User user) {
        FxRuntime.ensureStarted();
        Platform.runLater(() -> {
            try {
                Main.MenuApp.setUserData(svc, user);
                Parent root = FXMLLoader.load(Main.MenuApp.class.getResource("/menu/game-mode.fxml"));
                Stage s = new Stage();
                s.setTitle("Select Game Mode");
                s.setScene(new Scene(root));
                s.show();
            } catch (Exception e) {
                System.err.println("Error opening Game Mode window: " + e.getMessage());
            }
        });
    }

    public static void openBasics(GameService svc, User user) {
        FxRuntime.ensureStarted();
        Platform.runLater(() -> {
            try {
                Main.BasicsApp.setUserData(svc, user);
                Parent root = FXMLLoader.load(Main.BasicsApp.class.getResource("/basicsgame/basics-game.fxml"));
                Stage s = new Stage();
                s.setTitle("Basics Game");
                s.setScene(new Scene(root));
                s.show();
            } catch (Exception e) {
                System.err.println("Error opening Basics game window: " + e.getMessage());
            }
        });
    }

    public static void openViewSessions(GameService svc, User user) {
        FxRuntime.ensureStarted();
        Platform.runLater(() -> {
            try {
                Main.MenuApp.setUserData(svc, user);
                Parent root = FXMLLoader.load(Main.MenuApp.class.getResource("/menu/view-session.fxml"));
                Stage s = new Stage();
                s.setTitle("My Game Sessions");
                s.setScene(new Scene(root));
                s.show();
            } catch (Exception e) {
                System.err.println("Error opening View Sessions window: " + e.getMessage());
            }
        });
    }

    public static void openDeleteSession(GameService svc, User user) {
        FxRuntime.ensureStarted();
        Platform.runLater(() -> {
            try {
                Main.MenuApp.setUserData(svc, user);
                Parent root = FXMLLoader.load(Main.MenuApp.class.getResource("/menu/delete-session.fxml"));
                Stage s = new Stage();
                s.setTitle("Delete Session");
                s.setScene(new Scene(root));
                s.show();
            } catch (Exception e) {
                System.err.println("Error opening Delete Session window: " + e.getMessage());
            }
        });
    }

    public static void openChangeUsername(GameService svc, User user) {
        FxRuntime.ensureStarted();
        Platform.runLater(() -> {
            try {
                Main.MenuApp.setUserData(svc, user);
                Parent root = FXMLLoader.load(Main.MenuApp.class.getResource("/menu/change-username.fxml"));
                Stage s = new Stage();
                s.setTitle("Change Username");
                s.setScene(new Scene(root));
                s.show();
            } catch (Exception e) {
                System.err.println("Error opening Change Username window: " + e.getMessage());
            }
        });
    }

    public static void openChangePassword(GameService svc, User user) {
        FxRuntime.ensureStarted();
        Platform.runLater(() -> {
            try {
                Main.MenuApp.setUserData(svc, user);
                Parent root = FXMLLoader.load(Main.MenuApp.class.getResource("/menu/change-password.fxml"));
                Stage s = new Stage();
                s.setTitle("Change Password");
                s.setScene(new Scene(root));
                s.show();
            } catch (Exception e) {
                System.err.println("Error opening Change Password window: " + e.getMessage());
            }
        });
    }

    public static void openLeaderboard(GameService svc, User user) {
        FxRuntime.ensureStarted();
        Platform.runLater(() -> {
            try {
                Main.MenuApp.setUserData(svc, user);
                Parent root = FXMLLoader.load(Main.MenuApp.class.getResource("/menu/leaderboard.fxml"));
                Stage s = new Stage();
                s.setTitle("Leaderboard");
                s.setScene(new Scene(root));
                s.show();
            } catch (Exception e) {
                System.err.println("Error opening Leaderboard window: " + e.getMessage());
            }
        });
    }

    public static void openUsersList(GameService svc, User user) {
        FxRuntime.ensureStarted();
        Platform.runLater(() -> {
            try {
                Main.MenuApp.setUserData(svc, user);
                Parent root = FXMLLoader.load(Main.MenuApp.class.getResource("/menu/users-list.fxml"));
                Stage s = new Stage();
                s.setTitle("All Users");
                s.setScene(new Scene(root));
                s.show();
            } catch (Exception e) {
                System.err.println("Error opening Users List window: " + e.getMessage());
            }
        });
    }

    public static void openDeleteAccount(GameService svc, User user) {
        FxRuntime.ensureStarted();
        Platform.runLater(() -> {
            try {
                Main.MenuApp.setUserData(svc, user);
                Parent root = FXMLLoader.load(Main.MenuApp.class.getResource("/menu/delete-account.fxml"));
                Stage s = new Stage();
                s.setTitle("Delete Account");
                s.setScene(new Scene(root));
                s.show();
            } catch (Exception e) {
                System.err.println("Error opening Delete Account window: " + e.getMessage());
            }
        });
    }
}
