package de.burnthelemon.zestkit.hooks.database;

import java.sql.*;
import java.util.*;

public class ChatChannelDatabase {

    private static final String DB_URL = "jdbc:sqlite:chat_channels.db";

    public ChatChannelDatabase() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS chat_channels (" +
                "channel_name TEXT PRIMARY KEY," +
                "chat_range INTEGER," +
                "icon TEXT," +
                "is_isolated INTEGER);";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create or Update Channel
    public void upsertChatChannel(String name, int chatRange, String icon, boolean isIsolated) {
        String sql = "INSERT INTO chat_channels (channel_name, chat_range, icon, is_isolated) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON CONFLICT(channel_name) DO UPDATE SET chat_range = ?, icon = ?, is_isolated = ?;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, chatRange);
            pstmt.setString(3, icon);
            pstmt.setInt(4, isIsolated ? 1 : 0);
            pstmt.setInt(5, chatRange);
            pstmt.setString(6, icon);
            pstmt.setInt(7, isIsolated ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getChatChannel(String name) {
        String sql = "SELECT * FROM chat_channels WHERE channel_name = ?;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Map<String, Object> result = new HashMap<>();
                result.put("channel_name", rs.getString("channel_name"));
                result.put("chat_range", rs.getInt("chat_range"));
                result.put("icon", rs.getString("icon"));
                result.put("is_isolated", rs.getInt("is_isolated") == 1);
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteChatChannel(String name) {
        String sql = "DELETE FROM chat_channels WHERE channel_name = ?;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllChannelNames() {
        List<String> channels = new ArrayList<>();
        String sql = "SELECT channel_name FROM chat_channels;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                channels.add(rs.getString("channel_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return channels;
    }

    // Helper methods to fetch specific fields
    public Integer getChatRange(String name) {
        Map<String, Object> data = getChatChannel(name);
        return data != null ? (Integer) data.get("chat_range") : null;
    }

    public String getChatIcon(String name) {
        Map<String, Object> data = getChatChannel(name);
        return data != null ? (String) data.get("icon") : null;
    }

    public Boolean isChannelIsolated(String name) {
        Map<String, Object> data = getChatChannel(name);
        return data != null ? (Boolean) data.get("is_isolated") : null;
    }
}
