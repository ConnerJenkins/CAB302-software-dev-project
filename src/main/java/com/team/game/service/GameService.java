package main.java.com.team.game.service;

import main.java.com.team.game.data.GameStore;
import main.java.com.team.game.model.*;
import main.java.com.team.game.model.*;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public final class GameService {
    private final GameStore store;
    public GameService(GameStore store) { this.store = store; }

    // Auth
    public User register(String username, char[] password) { return store.createUser(username, password); }
    public Optional<User> login(String username, char[] password) { return store.authenticate(username, password); }

    // Users CRUD helpers
    public List<User> listUsers() { return store.listUsers(); }
    public void updateUsername(User user, String newName) { store.updateUsername(user.getId(), newName); }
    public void updatePassword(User user, char[] newPw) { store.updatePassword(user.getId(), newPw); }
    public boolean deleteUser(User user) { return store.deleteUser(user.getId()); }

    // Rounds
    public GameSession startRound(User user, GameMode mode) { return store.startSession(user.getId(), mode); }
    public void submitCorrect(GameSession s) { store.submitCorrect(s.getId()); }
    public void submitWrong(GameSession s) { store.submitWrong(s.getId()); }
    public void finishRound(GameSession s) { store.finishSession(s.getId()); }

    // Sessions
    public List<GameSession> listSessionsByUser(User user) { return store.listSessionsByUser(user.getId()); }
    public boolean deleteSession(int sessionId) { return store.deleteSession(sessionId); }

    // Scores
    public OptionalInt highScore(User user, GameMode mode) { return store.getHighScore(user.getId(), mode); }
    public List<ScoreRow> leaderboard(GameMode mode, int limit) { return store.leaderboard(mode, limit); }

    // Questions
    private final QuestionBank qb = new QuestionBank();

    public List<Question> getBasicsQuestions() {
        return qb.getBasics();
    }

    public List<Question> getTrigoQuestions() {
        return qb.getTrigo();
    }

    public List<Question> getTargetQuestions() {
        return qb.getTarget();
    }
}
