package com.team.game.model;

public final class ScoreRow {
    private final int userId;
    private final String username;
    private final GameMode mode;
    private final int highScore;

    public ScoreRow(int userId, String username, GameMode mode, int highScore) {
        this.userId = userId; this.username = username; this.mode = mode; this.highScore = highScore;
    }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public GameMode getMode() { return mode; }
    public int getHighScore() { return highScore; }
}
