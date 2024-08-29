package de.burnthelemon.ggnadditons.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:playerdata.db";

    public DatabaseManager() {
        createPlayerInformationTable();
        createBadgeTable();
        createPlayerBadgesTable();
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

    private void createPlayerInformationTable() {
        String sql = "CREATE TABLE IF NOT EXISTS player_information ("
                + "uuid TEXT PRIMARY KEY,"
                + "name TEXT NOT NULL"
                + ");";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createBadgeTable() {
        String sql = "CREATE TABLE IF NOT EXISTS badges ("
                + "name TEXT PRIMARY KEY,"
                + "hover_text TEXT,"
                + "display_text TEXT,"
                + "is_locked BOOLEAN"
                + ");";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createPlayerBadgesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS player_badges ("
                + "uuid TEXT,"
                + "badge_name TEXT,"
                + "FOREIGN KEY(uuid) REFERENCES player_information(uuid),"
                + "FOREIGN KEY(badge_name) REFERENCES badges(name),"
                + "PRIMARY KEY(uuid, badge_name)"
                + ");";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updatePlayerName(String uuid, String name) {
        String sql = "INSERT INTO player_information(uuid, name) VALUES(?, ?) "
                + "ON CONFLICT(uuid) DO UPDATE SET name = excluded.name;";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String getPlayerName(String uuid) {
        String sql = "SELECT name FROM player_information WHERE uuid = ?";

        try (Connection conn = this.connect();
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

    public void deletePlayerName(String uuid) {
        String sql = "DELETE FROM player_information WHERE uuid = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createBadge(String name, String hoverText, String displayText, boolean isLocked) {
        String sql = "INSERT INTO badges(name, hover_text, display_text, is_locked) VALUES (?, ?, ?, ?) "
                + "ON CONFLICT(name) DO UPDATE SET hover_text = excluded.hover_text, display_text = excluded.display_text, is_locked = excluded.is_locked;";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, hoverText);
            pstmt.setString(3, displayText);
            pstmt.setBoolean(4, isLocked);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void removeBadge(String name) {
        String sql = "DELETE FROM badges WHERE name = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean isBadgeLocked(String name) {
        String sql = "SELECT is_locked FROM badges WHERE name = ?";

        try (Connection conn = this.connect();
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

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            pstmt.setString(2, badgeName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void unawardBadge(String uuid, String badgeName) {
        String sql = "DELETE FROM player_badges WHERE uuid = ? AND badge_name = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            pstmt.setString(2, badgeName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String[] getBadgesForPlayer(String uuid) {
        String sql = "SELECT badge_name FROM player_badges WHERE uuid = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            ResultSet rs = pstmt.executeQuery();

            StringBuilder badges = new StringBuilder();
            while (rs.next()) {
                if (badges.length() > 0) {
                    badges.append(", ");
                }
                badges.append(rs.getString("badge_name"));
            }
            return badges.toString().split(", ");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return new String[0];
    }

    public String[] getAllBadges() {
        String sql = "SELECT name FROM badges";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            StringBuilder badges = new StringBuilder();
            while (rs.next()) {
                if (badges.length() > 0) {
                    badges.append(", ");
                }
                badges.append(rs.getString("name"));
            }
            return badges.toString().split(", ");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return new String[0];
    }

    public boolean badgeExists(String name) {
        String sql = "SELECT COUNT(*) FROM badges WHERE name = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
