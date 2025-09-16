package main.java.com.team.game.model;

import java.time.Instant;

public final class GameSession {
    private final int id;
    private final int userId;
    private final GameMode mode;
    private final Instant startedAt;
    private final Instant endedAt;   // null until finished
    private final int score;
    private final int strikes;
    private final boolean completed;

    public GameSession(int id, int userId, GameMode mode, Instant startedAt,
                       Instant endedAt, int score, int strikes, boolean completed) {
        this.id = id; this.userId = userId; this.mode = mode;
        this.startedAt = startedAt; this.endedAt = endedAt;
        this.score = score; this.strikes = strikes; this.completed = completed;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public GameMode getMode() { return mode; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getEndedAt() { return endedAt; }
    public int getScore() { return score; }
    public int getStrikes() { return strikes; }
    public boolean isCompleted() { return completed; }
}
