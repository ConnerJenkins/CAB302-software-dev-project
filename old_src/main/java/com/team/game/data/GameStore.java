package main.java.com.team.game.data;

import main.java.com.team.game.model.GameMode;
import main.java.com.team.game.model.GameSession;
import main.java.com.team.game.model.ScoreRow;
import main.java.com.team.game.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public final class GameStore {

    public GameStore() { initSchema(); } // create tables if missing


    private void initSchema() {
        String users = """
          CREATE TABLE IF NOT EXISTS users (
            id            INTEGER PRIMARY KEY AUTOINCREMENT,
            username      TEXT NOT NULL UNIQUE,
            password      TEXT NOT NULL,
            registered_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
          )
        """;
        String sessions = """
          CREATE TABLE IF NOT EXISTS game_session (
            id         INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id    INTEGER  NOT NULL,
            mode       TEXT     NOT NULL,
            started_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
            ended_at   DATETIME,
            score      INTEGER  NOT NULL DEFAULT 0,
            strikes    INTEGER  NOT NULL DEFAULT 0,
            completed  INTEGER  NOT NULL DEFAULT 0,
            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
          );
          CREATE INDEX IF NOT EXISTS idx_session_user_mode ON game_session(user_id, mode, completed)
        """;
        try (var c = Database.open(); var st = c.createStatement()) {
            st.execute(users);
            for (String s : sessions.split(";")) {
                String t = s.trim();
                if (!t.isEmpty()) st.execute(t);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Schema init failed", e);
        }
    }

    // ---- AUTH  ----
    public User createUser(String username, char[] password) {
        try (var c = Database.open()) {
            // ensure unique (case-insensitive)
            try (var chk = c.prepareStatement("SELECT 1 FROM users WHERE LOWER(username)=LOWER(?)")) {
                chk.setString(1, username);
                try (var rs = chk.executeQuery()) { if (rs.next()) throw new IllegalStateException("Username is taken"); }
            }
            int newId;
            try (var ins = c.prepareStatement(
                    "INSERT INTO users(username, password) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ins.setString(1, username);
                ins.setString(2, new String(password));
                ins.executeUpdate();
                try (var keys = ins.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("No key");
                    newId = keys.getInt(1);
                }
            }
            try (var ps = c.prepareStatement("SELECT id, username, registered_at FROM users WHERE id=?")) {
                ps.setInt(1, newId);
                try (var rs = ps.executeQuery()) { rs.next(); return mapUser(rs); }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Optional<User> authenticate(String username, char[] password) {
        try (var c = Database.open();
             var ps = c.prepareStatement(
                     "SELECT id, username, registered_at, password FROM users WHERE LOWER(username)=LOWER(?)")) {
            ps.setString(1, username);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                String stored = rs.getString("password");
                if (!stored.equals(new String(password))) return Optional.empty();
                return Optional.of(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getTimestamp("registered_at").toInstant()));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ---- USERS ----
    public List<User> listUsers() {
        var out = new ArrayList<User>();
        try (var c = Database.open();
             var ps = c.prepareStatement("SELECT id, username, registered_at FROM users ORDER BY username ASC");
             var rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapUser(rs));
            return out;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void updateUsername(int userId, String newUsername) {
        try (var c = Database.open()) {
            try (var chk = c.prepareStatement("SELECT 1 FROM users WHERE LOWER(username)=LOWER(?) AND id<>?")) {
                chk.setString(1, newUsername); chk.setInt(2, userId);
                try (var rs = chk.executeQuery()) { if (rs.next()) throw new IllegalStateException("Username is taken"); }
            }
            try (var ps = c.prepareStatement("UPDATE users SET username=? WHERE id=?")) {
                ps.setString(1, newUsername); ps.setInt(2, userId); ps.executeUpdate();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void updatePassword(int userId, char[] newPassword) {
        try (var c = Database.open();
             var ps = c.prepareStatement("UPDATE users SET password=? WHERE id=?")) {
            ps.setString(1, new String(newPassword));
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean deleteUser(int userId) {
        try (var c = Database.open();
             var ps = c.prepareStatement("DELETE FROM users WHERE id=?")) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ---- SESSIONS ----
    public GameSession startSession(int userId, GameMode mode) {
        try (var c = Database.open()) {
            int id;
            try (var ps = c.prepareStatement(
                    "INSERT INTO game_session(user_id, mode) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setString(2, mode.name());
                ps.executeUpdate();
                try (var keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("No key");
                    id = keys.getInt(1);
                }
            }
            try (var ps = c.prepareStatement(
                    "SELECT id,user_id,mode,started_at,ended_at,score,strikes,completed FROM game_session WHERE id=?")) {
                ps.setInt(1, id);
                try (var rs = ps.executeQuery()) { rs.next(); return mapSession(rs); }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void submitCorrect(int sessionId) {
        try (var c = Database.open();
             var ps = c.prepareStatement("UPDATE game_session SET score = score + 1 WHERE id=? AND completed=0")) {
            ps.setInt(1, sessionId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void submitWrong(int sessionId) {
        try (var c = Database.open()) {
            try (var inc = c.prepareStatement("UPDATE game_session SET strikes = strikes + 1 WHERE id=? AND completed=0")) {
                inc.setInt(1, sessionId);
                inc.executeUpdate();
            }
            int strikes;
            try (var sel = c.prepareStatement("SELECT strikes FROM game_session WHERE id=?")) {
                sel.setInt(1, sessionId);
                try (var rs = sel.executeQuery()) { rs.next(); strikes = rs.getInt(1); }
            }
            if (strikes >= 3) {
                try (var fin = c.prepareStatement("UPDATE game_session SET completed=1, ended_at=CURRENT_TIMESTAMP WHERE id=?")) {
                    fin.setInt(1, sessionId);
                    fin.executeUpdate();
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void finishSession(int sessionId) {
        try (var c = Database.open();
             var ps = c.prepareStatement("UPDATE game_session SET completed=1, ended_at=CURRENT_TIMESTAMP WHERE id=?")) {
            ps.setInt(1, sessionId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<GameSession> listSessionsByUser(int userId) {
        var out = new ArrayList<GameSession>();
        String sql = """
            SELECT id,user_id,mode,started_at,ended_at,score,strikes,completed
            FROM game_session
            WHERE user_id=? ORDER BY started_at DESC
            """;
        try (var c = Database.open(); var ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (var rs = ps.executeQuery()) { while (rs.next()) out.add(mapSession(rs)); }
            return out;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean deleteSession(int sessionId) {
        try (var c = Database.open(); var ps = c.prepareStatement("DELETE FROM game_session WHERE id=?")) {
            ps.setInt(1, sessionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ---- QUERIES ----
    public OptionalInt getHighScore(int userId, GameMode mode) {
        try (var c = Database.open();
             var ps = c.prepareStatement(
                     "SELECT MAX(score) FROM game_session WHERE completed=1 AND user_id=? AND mode=?")) {
            ps.setInt(1, userId);
            ps.setString(2, mode.name());
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    int v = rs.getInt(1);
                    if (rs.wasNull()) return OptionalInt.empty();
                    return OptionalInt.of(v);
                }
                return OptionalInt.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<ScoreRow> leaderboard(GameMode mode, int limit) {
        String sql = """
            SELECT u.id AS user_id, u.username, MAX(s.score) AS high_score
            FROM users u
            JOIN game_session s ON s.user_id = u.id
            WHERE s.completed = 1 AND s.mode = ?
            GROUP BY u.id, u.username
            ORDER BY high_score DESC
            LIMIT ?
            """;
        var out = new ArrayList<ScoreRow>();
        try (var c = Database.open(); var ps = c.prepareStatement(sql)) {
            ps.setString(1, mode.name());
            ps.setInt(2, Math.max(1, limit));
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new ScoreRow(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            mode,
                            rs.getInt("high_score")));
                }
            }
            return out;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ---- mappers ----
    private static User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getTimestamp("registered_at").toInstant());
    }

    private static GameSession mapSession(ResultSet rs) throws SQLException {
        Timestamp endTs = rs.getTimestamp("ended_at");
        return new GameSession(
                rs.getInt("id"),
                rs.getInt("user_id"),
                GameMode.valueOf(rs.getString("mode")),
                rs.getTimestamp("started_at").toInstant(),
                endTs == null ? null : endTs.toInstant(),
                rs.getInt("score"),
                rs.getInt("strikes"),
                rs.getInt("completed") != 0);
    }
}
