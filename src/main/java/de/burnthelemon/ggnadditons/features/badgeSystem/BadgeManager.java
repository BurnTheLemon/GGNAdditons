package de.burnthelemon.ggnadditons.features.badgeSystem;

import de.burnthelemon.ggnadditons.hooks.database.DatabaseHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BadgeManager {
    private final DatabaseHandler databaseHandler;

    public BadgeManager(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public void createBadge(String name, String hoverText, String displayText, boolean isLocked) {
        String sql = "INSERT INTO badges(name, hover_text, display_text, is_locked) VALUES (?, ?, ?, ?) "
                + "ON CONFLICT(name) DO UPDATE SET hover_text = excluded.hover_text, display_text = excluded.display_text, is_locked = excluded.is_locked;";

        executeUpdate(sql, name, hoverText, displayText, isLocked);
    }

    public void removeBadge(String name) {
        String sql = "DELETE FROM badges WHERE name = ?";
        executeUpdate(sql, name);
    }

    public boolean isBadgeLocked(String name) {
        String sql = "SELECT is_locked FROM badges WHERE name = ?";
        try (Connection conn = databaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("is_locked");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public void awardBadge(String uuid, String badgeName) {
        String sql = "INSERT INTO player_badges(uuid, badge_name) VALUES (?, ?) "
                + "ON CONFLICT(uuid, badge_name) DO NOTHING;";

        executeUpdate(sql, uuid, badgeName);
    }

    public void unawardBadge(String uuid, String badgeName) {
        String sql = "DELETE FROM player_badges WHERE uuid = ? AND badge_name = ?";
        executeUpdate(sql, uuid, badgeName);
    }

    public String[] getBadgesForPlayer(String uuid) {
        String sql = "SELECT badge_name FROM player_badges WHERE uuid = ?";
        return getStringsFromSQL(sql, uuid);
    }

    public String[] getAllBadges() {
        String sql = "SELECT name FROM badges";
        return getStringsFromSQL(sql);
    }

    public boolean badgeExists(String name) {
        String sql = "SELECT COUNT(*) FROM badges WHERE name = ?";
        try (Connection conn = databaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

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

    private String[] getStringsFromSQL(String sql, Object... params) {
        try (Connection conn = databaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            ResultSet rs = pstmt.executeQuery();
            StringBuilder result = new StringBuilder();
            while (rs.next()) {
                if (result.length() > 0) {
                    result.append(", ");
                }
                result.append(rs.getString(1));
            }
            return result.toString().split(", ");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return new String[0];
    }
}
