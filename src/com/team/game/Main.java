package com.team.game;

import com.team.game.data.GameStore;
import com.team.game.model.*;
import com.team.game.service.GameService;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final ZoneId LOCAL_TZ = ZoneId.of("Australia/Brisbane"); // or ZoneId.systemDefault()
    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

    public static void main(String[] args) {
        System.out.println("cwd = " + System.getProperty("user.dir"));
        System.out.println("db  = data/game.db");

        GameStore store = new GameStore();
        GameService svc = new GameService(store);

        Scanner in = new Scanner(System.in);
        System.out.println("1) Register   2) Login");
        System.out.print("Choose: ");
        String choice = in.nextLine().trim();

        System.out.print("Username: ");
        String username = in.nextLine().trim();
        System.out.print("Password: ");
        char[] pw = in.nextLine().toCharArray();

        User user;
        if ("1".equals(choice)) {
            try {
                user = svc.register(username, pw);
                System.out.println("Registered as " + user.getUsername());
            } catch (IllegalStateException dup) {
                System.out.println("Username is taken.");
                return;
            }
        } else {
            Optional<User> maybe = svc.login(username, pw);
            if (maybe.isEmpty()) { System.out.println("Invalid credentials."); return; }
            user = maybe.get();
            System.out.println("Welcome back, " + user.getUsername());
        }

        while (true) {
            System.out.println("\n== MENU ==");
            System.out.println("1) Start round (pick game mode)");
            System.out.println("2) My sessions (Read)");
            System.out.println("3) Delete a session (Delete)");
            System.out.println("4) Change username (Update)");
            System.out.println("5) Change password (Update)");
            System.out.println("6) Delete my account (Delete)");
            System.out.println("7) Leaderboard (pick mode)");
            System.out.println("8) List all users (Read)");
            System.out.println("0) Exit");
            System.out.print("Pick: ");
            String op = in.nextLine().trim();

            switch (op) {
                case "1" -> startRoundFlow(in, svc, user);
                case "2" -> {
                    var sessions = svc.listSessionsByUser(user);
                    if (sessions.isEmpty()) System.out.println("(no sessions)");
                    for (var s : sessions) {
                        var startedLocal = s.getStartedAt().atZone(LOCAL_TZ).format(DT_FMT);
                        var endedLocal = (s.getEndedAt() == null) ? "-" : s.getEndedAt().atZone(LOCAL_TZ).format(DT_FMT);

                        System.out.printf(
                                "id=%d mode=%s score=%d strikes=%d done=%s started=%s ended=%s%n",
                                s.getId(), s.getMode(), s.getScore(), s.getStrikes(),
                                s.isCompleted(), startedLocal, endedLocal
                        );
                    }
                }
                case "3" -> {
                    System.out.print("Session id to delete: ");
                    int id = Integer.parseInt(in.nextLine());
                    boolean ok = svc.deleteSession(id);
                    System.out.println(ok ? "Deleted." : "Not found.");
                }
                case "4" -> {
                    System.out.print("New username: ");
                    String nn = in.nextLine().trim();
                    try {
                        svc.updateUsername(user, nn);
                        // update local object so menu shows your new name
                        user = new User(user.getId(), nn, user.getRegisteredAt());
                        System.out.println("Updated.");
                    } catch (IllegalStateException ex) { System.out.println("That name is taken."); }
                }
                case "5" -> {
                    System.out.print("New password: ");
                    char[] npw = in.nextLine().toCharArray();
                    svc.updatePassword(user, npw);
                    System.out.println("Password changed.");
                }
                case "6" -> {
                    System.out.print("Type DELETE to confirm account deletion: ");
                    if ("DELETE".equals(in.nextLine().trim())) {
                        boolean ok = svc.deleteUser(user);
                        System.out.println(ok ? "Account deleted." : "Delete failed.");
                        return;
                    }
                }
                case "7" -> {
                    GameMode mode = chooseMode(in);
                    if (mode == null) break;
                    var rows = svc.leaderboard(mode, 10);
                    if (rows.isEmpty()) System.out.println("(no scores yet)");
                    for (var r : rows) System.out.printf("- %s: %d%n", r.getUsername(), r.getHighScore());
                }
                case "8" -> {
                    var users = svc.listUsers();
                    for (var u : users) System.out.printf("%d  %s  (%s)%n",
                            u.getId(), u.getUsername(), u.getRegisteredAt());
                }
                case "0" -> { return; }
                default -> System.out.println("Unknown option.");
            }
        }
    }



    private static GameMode chooseMode(Scanner in) {
        System.out.println("Pick game mode:");
        System.out.println("1) BASICS");
        System.out.println("2) TRIG");
        System.out.println("3) TARGET");
        System.out.println("0) Cancel");
        System.out.print("Choose: ");
        String m = in.nextLine().trim();
        return switch (m) {
            case "1" -> GameMode.BASICS;
            case "2" -> GameMode.TRIG;
            case "3" -> GameMode.TARGET;
            case "0" -> null;
            default -> { System.out.println("Invalid."); yield null; }
        };
    }
    private static void startRoundFlow(Scanner in, GameService svc, User user) {
        GameMode mode = chooseMode(in);
        if (mode == null) return;

        GameSession s = svc.startRound(user, mode);
        int score = 0, strikes = 0;
        System.out.printf("Started %s round (session id=%d).%n", mode, s.getId());

        while (true) {
            System.out.printf("[score=%d, strikes=%d]  c) Correct   w) Wrong   f) Finish  >", score, strikes);
            String cmd = in.nextLine().trim().toLowerCase();
            if (cmd.equals("c")) {
                svc.submitCorrect(s);
                score++;
            } else if (cmd.equals("w")) {
                svc.submitWrong(s);
                strikes++;
                if (strikes >= 3) {
                    System.out.println("3 strikes reached — round finished automatically.");
                    break;
                }
            } else if (cmd.equals("f")) {
                svc.finishRound(s);
                break;
            } else {
                System.out.println("Enter c / w / f");
            }
        }

        int hs = svc.highScore(user, mode).orElse(0);
        System.out.println("Round finished. Your high score (" + mode + ") = " + hs);
    }

}
