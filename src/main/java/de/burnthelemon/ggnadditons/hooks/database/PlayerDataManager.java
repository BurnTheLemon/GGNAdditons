package de.burnthelemon.ggnadditons.hooks.database;

import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerDataManager {
    private final DatabaseHandler databaseHandler;

    public PlayerDataManager(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
        createPlayerChatModeTable();
    }

    // Create a new table to store player chat modes
    private void createPlayerChatModeTable() {
        String sql = "CREATE TABLE IF NOT EXISTS player_chat_modes ("
                + "uuid TEXT PRIMARY KEY,"
                + "chat_mode TEXT NOT NULL"
                + ");";

        executeSQL(sql);
    }

    // Create or update a player's chat mode
    public void setPlayerChatMode(String uuid, String chatMode) {
        String sql = "INSERT INTO player_chat_modes(uuid, chat_mode) VALUES(?, ?) "
                + "ON CONFLICT(uuid) DO UPDATE SET chat_mode = excluded.chat_mode;";

        executeUpdate(sql, uuid, chatMode);
    }

    // Get a player's chat mode
    @Nullable
    public String getPlayerChatMode(String uuid) {
        String sql = "SELECT chat_mode FROM player_chat_modes WHERE uuid = ?";
        try (Connection conn = databaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("chat_mode");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;  // Return null if no entry exists for the player
    }

    // Delete a player's chat mode
    public void deletePlayerChatMode(String uuid) {
        String sql = "DELETE FROM player_chat_modes WHERE uuid = ?";
        executeUpdate(sql, uuid);
    }

    // Method to update the player name
    public void updatePlayerName(String uuid, String name) {
        String sql = "INSERT INTO player_information(uuid, name) VALUES(?, ?) "
                + "ON CONFLICT(uuid) DO UPDATE SET name = excluded.name;";
        executeUpdate(sql, uuid, name);
    }

    // Get a player's name
    public String getPlayerName(String uuid) {
        String sql = "SELECT name FROM player_information WHERE uuid = ?";
        try (Connection conn = databaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // Delete a player's name entry
    public void deletePlayerName(String uuid) {
        String sql = "DELETE FROM player_information WHERE uuid = ?";
        executeUpdate(sql, uuid);
    }

    // Helper method to execute SQL update queries
    private void executeUpdate(String sql, Object... params) {
        try (Connection conn = databaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Helper method to execute SQL queries that don't return results
    private void executeSQL(String sql) {
        try (Connection conn = databaseHandler.connect();
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}