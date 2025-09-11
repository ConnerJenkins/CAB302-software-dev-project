package com.team.game.data;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public final class Database {
    private static final String URL = "jdbc:sqlite:data/game.db";

    static {
        try {
            Files.createDirectories(Path.of("data"));
            Class.forName("org.sqlite.JDBC"); // make sure driver is on classpath
        } catch (Exception e) {
            throw new RuntimeException("DB init failed", e);
        }
    }

    private Database() {}


    public static Connection open() throws SQLException {
        Connection c = DriverManager.getConnection(URL);
        try (var st = c.createStatement()) { st.execute("PRAGMA foreign_keys = ON"); }
        return c;
    }
}
