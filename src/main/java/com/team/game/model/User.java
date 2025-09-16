package main.java.com.team.game.model;

import java.time.Instant;
import java.util.Objects;

public final class User {
    private final int id;
    private final String username;
    private final Instant registeredAt;

    public User(int id, String username, Instant registeredAt) {
        this.id = id;
        this.username = Objects.requireNonNull(username);
        this.registeredAt = Objects.requireNonNull(registeredAt);
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public Instant getRegisteredAt() { return registeredAt; }
}
