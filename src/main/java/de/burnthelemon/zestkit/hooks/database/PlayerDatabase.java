package de.burnthelemon.zestkit.hooks.database;

import java.sql.*;
import java.util.UUID;

public class PlayerDatabase {
    private static final String DB_URL = "jdbc:sqlite:players.db";

    public PlayerDatabase() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS players (
                uuid TEXT PRIMARY KEY,
                nickname TEXT
            );
        """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPlayerIfAbsent(UUID uuid) {
        String sql = "INSERT OR IGNORE INTO players(uuid) VALUES (?);";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setPlayerNickname(UUID uuid, String nickname) {
        addPlayerIfAbsent(uuid);
        String sql = "UPDATE players SET nickname = ? WHERE uuid = ?;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nickname);
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getPlayerNickname(UUID uuid) {
        String sql = "SELECT nickname FROM players WHERE uuid = ?;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("nickname");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deletePlayerNickname(UUID uuid) {
        String sql = "UPDATE players SET nickname = NULL WHERE uuid = ?;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePlayer(UUID uuid) {
        String sql = "DELETE FROM players WHERE uuid = ?;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
