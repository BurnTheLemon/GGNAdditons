package de.burnthelemon.zestkit.hooks.database;

import java.sql.*;
import java.util.*;

public class MembershipDatabase {
    private static final String DB_URL = "jdbc:sqlite:membership.db";

    public MembershipDatabase() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS player_channel_membership (
                uuid TEXT NOT NULL,
                channel_name TEXT NOT NULL,
                PRIMARY KEY (uuid, channel_name)
            );
        """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPlayerToChannel(UUID uuid, String channelName) {
        String sql = "INSERT OR IGNORE INTO player_channel_membership(uuid, channel_name) VALUES (?, ?);";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, channelName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removePlayerFromChannel(UUID uuid, String channelName) {
        String sql = "DELETE FROM player_channel_membership WHERE uuid = ? AND channel_name = ?;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, channelName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getPlayerChannels(UUID uuid) {
        List<String> channels = new ArrayList<>();
        String sql = "SELECT channel_name FROM player_channel_membership WHERE uuid = ?;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                channels.add(rs.getString("channel_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return channels;
    }

    public Integer getPlayerChatRange(UUID playerUUID) {
        List<String> channels = getPlayerChannels(playerUUID);
        if (channels.isEmpty()) return null;

        String channelName = channels.get(0); // Replace with active logic if needed
        return new ChatChannelDatabase().getChatRange(channelName);
    }

}
