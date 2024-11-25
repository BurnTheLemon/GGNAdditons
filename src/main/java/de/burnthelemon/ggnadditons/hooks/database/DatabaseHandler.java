package de.burnthelemon.ggnadditons.hooks.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHandler {
    private static final String URL = "jdbc:sqlite:playerdata.db";

    public DatabaseHandler() {
        createTables();
    }

    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private void createTables() {
        createPlayerInformationTable();
        createBadgeTable();
        createPlayerBadgesTable();
    }

    private void createPlayerInformationTable() {
        String sql = "CREATE TABLE IF NOT EXISTS player_information ("
                + "uuid TEXT PRIMARY KEY,"
                + "name TEXT NOT NULL"
                + ");";

        executeSQL(sql);
    }

    private void createBadgeTable() {
        String sql = "CREATE TABLE IF NOT EXISTS badges ("
                + "name TEXT PRIMARY KEY,"
                + "hover_text TEXT,"
                + "display_text TEXT,"
                + "is_locked BOOLEAN"
                + ");";

        executeSQL(sql);
    }

    private void createPlayerBadgesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS player_badges ("
                + "uuid TEXT,"
                + "badge_name TEXT,"
                + "FOREIGN KEY(uuid) REFERENCES player_information(uuid),"
                + "FOREIGN KEY(badge_name) REFERENCES badges(name),"
                + "PRIMARY KEY(uuid, badge_name)"
                + ");";

        executeSQL(sql);
    }

    private void executeSQL(String sql) {
        try (Connection conn = connect();
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
