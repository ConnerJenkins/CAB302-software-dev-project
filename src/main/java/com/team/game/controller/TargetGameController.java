package main.java.com.team.game.controller;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import main.java.com.team.game.Main;
import main.java.com.team.game.model.GameMode;
import main.java.com.team.game.model.GameSession;
import main.java.com.team.game.model.User;
import main.java.com.team.game.service.GameService;
import main.java.com.team.game.target.TargetPhysics;

import java.util.Random;

public class TargetGameController {

    @FXML private Label scoreLabel;
    @FXML private Label strikesLabel;
    @FXML private Label statusLabel;
    @FXML private Label questionLabel;
    @FXML private TextField answerField;
    @FXML private Button fireBtn;
    @FXML private Button nextBtn;
    @FXML private Button newGameBtn;
    @FXML private Button returnBtn;
    @FXML private Canvas canvas;

    private GameService gameService;
    private User currentUser;
    private GameSession session;

    private final Random rng = new Random();
    private int score = 0;
    private int strikes = 0;

    private boolean roundActive = false;
    private boolean animating = false;

    private final double ppm = 50.0;
    private final double g = 9.8;
    private final double wallX_px = 700.0;
    private final double groundMargin = 40.0;
    private final double ballRadius_px = 8.0;
    private final double targetRadius_px = 14.0;

    private static final double ANGLE_MIN_DEG = 30.0;
    private static final double ANGLE_MAX_DEG = 60.0;

    private double angleDeg;
    private double angleRad;

    private double x0_m;
    private double y0_m;
    private double targetY_m;
    private double correctV;

    private AnimationTimer timer;
    private long lastNanos;
    private double t;
    private double vUser;

    @FXML
    private void initialize() {
        try {
            this.gameService = Main.TargetApp.getGameService();
            this.currentUser = Main.TargetApp.getCurrentUser();
        }catch (Throwable ignored) {}

        if (gameService != null && currentUser != null) {
            session = gameService.startRound(currentUser, GameMode.TARGET);
            statusLabel.setText("Session started. User: " + currentUser.getUsername());
        } else {
            statusLabel.setText("database ain't there chief");
        }

        newGameBtn.setDisable(true);
        returnBtn.setDisable(true);

        updateHud();
        newRound();
    }

    @FXML
    private void handleFire() {
        if (!roundActive || animating) {
            return;
        }

        String text = answerField.getText();

        if (text == null) {
            text = "";
        }
        text = text.trim();

        if (text.isEmpty()) {
            statusLabel.setText("Enter a launch speed first.");
            return;
        }

        try {
            vUser = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            statusLabel.setText("Please enter a valid number (m/s).");
            return;
        }

        if (vUser <= 0) {
            statusLabel.setText("Speed must be > 0.");
            return;
        }

        startAnimation();
    }

    @FXML
    private void handleNext() {
        if (animating) {
            return;
        }
        newRound();
    }

    @FXML
    private void handleNewGame() {
        score = 0;
        strikes = 0;
        updateHud();

        if (gameService != null && currentUser != null) {
            session = gameService.startRound(currentUser, GameMode.TARGET);
        }

        fireBtn.setDisable(false);
        nextBtn.setDisable(true);

        newGameBtn.setDisable(true);
        returnBtn.setDisable(true);
        statusLabel.setText("New game started.");
        newRound();
    }

    @FXML
    private void handleReturnToMenu() {
        Stage stage = (Stage) canvas.getScene().getWindow();

        if (stage != null) {
            stage.close();
        }
    }

    private void newRound() {
        if (strikes >= 3) {

            endGame("3 strikes reached.");
            return;
        }

        angleDeg = ANGLE_MIN_DEG + rng.nextDouble() * (ANGLE_MAX_DEG - ANGLE_MIN_DEG);
        angleRad = Math.toRadians(angleDeg);

        double canvasH_m = canvas.getHeight() / ppm;

        y0_m = ballRadius_px / ppm;
        x0_m = 1.0;

        double xWall_m = wallX_px / ppm;
        double x_m = xWall_m - x0_m;

        double maxY = (x_m * Math.tan(angleRad)) - 0.75;

        if (maxY < 0.5) {
            maxY = 0.5;
        }

        double cap = canvasH_m - 1.0;

        if (maxY > cap) {
            maxY = cap;
        }

        if (maxY <= 0.5) {
            angleDeg = (ANGLE_MIN_DEG + ANGLE_MAX_DEG) / 2.0;
            angleRad = Math.toRadians(angleDeg);
            maxY = (x_m * Math.tan(angleRad)) - 0.75;
            if (maxY < 0.5) {
                maxY = 0.5;
            }
            if (maxY > cap) {
                maxY = cap;
            }
        }

        targetY_m = 0.5 + rng.nextDouble() * (maxY - 0.5);

        correctV = TargetPhysics.requiredSpeed(g, angleRad, x_m, targetY_m);

        String q = "Angle θ = " + Math.round(angleDeg) + "°, wall = " + String.format("%.1f", x_m) + " m, target height = " + String.format("%.1f", targetY_m) + " m.\nEnter v (m/s) and press Fire.";
        questionLabel.setText(q);
        statusLabel.setText("Enter v and click Fire.");
        answerField.clear();
        nextBtn.setDisable(true);
        fireBtn.setDisable(false);

        roundActive = true;
        animating = false;

        drawWorld();
    }

    private void endRound(boolean wasHit) {
        roundActive = false;
        animating = false;
        nextBtn.setDisable(false);
        fireBtn.setDisable(true);

        if (wasHit == true) {
            score++;
            statusLabel.setText("Hit! v* = " + String.format("%.2f", correctV) + " m/s, your v = " + String.format("%.2f", vUser) + " m/s");
            if (gameService != null && session != null) {
                gameService.submitCorrect(session);
            }
        } else {
            strikes++;
            statusLabel.setText("Miss. v* = " + String.format("%.2f", correctV) + " m/s, your v = " + String.format("%.2f", vUser) + " m/s");
            if (gameService != null && session != null) {
                gameService.submitWrong(session);
            }
        }

        updateHud();

        if (strikes >= 3) {
            endGame("3 strikes reached.");
        }
    }

    private void endGame(String reason) {
        roundActive = false;
        animating = false;
        fireBtn.setDisable(true);
        nextBtn.setDisable(true);
        newGameBtn.setDisable(false);
        returnBtn.setDisable(false);

        if (timer != null) timer.stop();

        String finalMsg = "Game over: " + reason;
        if (gameService != null && session != null) {
            gameService.finishRound(session);
            finalMsg = finalMsg + " (session " + session.getId() + " finished)";
        }
        statusLabel.setText(finalMsg);
    }

    private void startAnimation() {
        animating = true;
        t = 0.0;
        lastNanos = 0L;

        if (timer != null) {
            timer.stop();
        }
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastNanos == 0L) {
                    lastNanos = now;
                    return;
                }
                double dt = (now - lastNanos) / 1000000000.0;
                lastNanos = now;
                step(dt);
            }
        };
        timer.start();
    }

    private void step(double dt) {
        t += dt;

        double vx = vUser * Math.cos(angleRad);
        double vy = vUser * Math.sin(angleRad);
        double x_m = x0_m + vx * t;
        double y_m = y0_m + vy * t - 0.5 * g * t * t;

        double groundY_px = canvas.getHeight() - groundMargin;
        double x_px = x_m * ppm;
        double y_px = groundY_px - (y_m * ppm);

        drawWorld();
        drawBall(x_px, y_px);

        if (y_px + ballRadius_px >= groundY_px) {
            timer.stop();

            animating = false;
            endRound(false);
            return;
        }

        if (x_px + ballRadius_px >= wallX_px) {
            double targetCenterY_px = groundY_px - (targetY_m * ppm);
            boolean withinTarget = TargetPhysics.isHit(y_px, targetCenterY_px, targetRadius_px);

            timer.stop();
            animating = false;
            endRound(withinTarget);
        }
    }

    private void drawWorld() {
        GraphicsContext g2 = canvas.getGraphicsContext2D();
        double W = canvas.getWidth();
        double H = canvas.getHeight();
        double groundY_px = H - groundMargin;

        g2.setFill(Color.web("#5775ad"));
        g2.fillRect(0, 0, W, H);

        g2.setStroke(Color.GRAY);
        g2.setLineWidth(2);
        g2.strokeLine(0, groundY_px, W, groundY_px);

        g2.setStroke(Color.DARKGRAY);
        g2.setLineWidth(4);
        g2.strokeLine(wallX_px, groundY_px, wallX_px, 40);

        g2.setStroke(Color.web("#0066CC"));
        g2.setLineWidth(3);
        double targetCenterY_px = groundY_px - (targetY_m * ppm);
        g2.strokeOval(wallX_px - targetRadius_px,targetCenterY_px - targetRadius_px,targetRadius_px * 2, targetRadius_px * 2);

        double x0_px = x0_m * ppm;
        double y0_px = groundY_px - (y0_m * ppm);


        g2.setFill(Color.BLACK);
        g2.fillOval(x0_px - 3, y0_px - 3, 6, 6);
    }

    private void drawBall(double x_px, double y_px) {
        GraphicsContext g2 = canvas.getGraphicsContext2D();
        g2.setFill(Color.web("#E53935"));
        g2.fillOval(x_px - ballRadius_px, y_px - ballRadius_px,ballRadius_px * 2, ballRadius_px * 2);
    }

    private void updateHud() {
        scoreLabel.setText("Score: " + score);
        strikesLabel.setText("Strikes: " + strikes + " / 3");
    }
}
