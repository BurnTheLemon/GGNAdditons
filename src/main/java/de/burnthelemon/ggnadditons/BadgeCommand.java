package de.burnthelemon.ggnadditons;

import de.burnthelemon.ggnadditons.config.BadgeConfig;
import de.burnthelemon.ggnadditons.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Map;

public class BadgeCommand implements CommandExecutor {

    private final BadgeConfig badgeConfig  = new BadgeConfig();
    private final Permission adminPermission;

    public BadgeCommand() {
        this.adminPermission = new Permission("ggn.badge.admin");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission(adminPermission)) {
            player.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("Usage: /badge <create|remove|list|award|unaward|select|unselect> ...");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                return handleCreateBadge(player, args);
            case "remove":
                return handleRemoveBadge(player, args);
            case "list":
                return handleListBadges(player, args);
            case "award":
                return handleAwardBadge(player, args);
            case "unaward":
                return handleUnawardBadge(player, args);
            case "select":
                return handleSelectBadge(player, args);
            case "unselect":
                return handleUnselectBadge(player, args);
            default:
                player.sendMessage("Unknown subcommand. Usage: /badge <create|remove|list|award|unaward|select|unselect> ...");
                return true;
        }
    }

    private boolean handleCreateBadge(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage("Usage: /badge create <badgeName> <badgeHoverText> <badgeText> <isLocked>");
            return true;
        }
        String badgeName = args[1];
        String hoverText = args[2];
        String badgeText = args[3];
        boolean isLocked = Boolean.parseBoolean(args[4]);

        badgeConfig.addBadge(badgeName, hoverText, badgeText, isLocked);
        player.sendMessage("Badge created successfully.");
        return true;
    }

    private boolean handleRemoveBadge(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /badge remove <badgeName>");
            return true;
        }
        String badgeName = args[1];

        if (!badgeConfig.badgeExists(badgeName)) {
            player.sendMessage("Badge does not exist.");
            return true;
        }

        badgeConfig.removeBadge(badgeName);
        player.sendMessage("Badge removed successfully.");
        return true;
    }

    private boolean handleListBadges(Player player, String[] args) {
        if (args.length > 1 && args[1].equalsIgnoreCase("all")) {
            // List all badges
            Map<String, Map<String, Object>> allBadges = badgeConfig.getAllBadges();
            if (allBadges.isEmpty()) {
                player.sendMessage("No badges available.");
                return true;
            }
            player.sendMessage("Available badges:");
            for (Map.Entry<String, Map<String, Object>> entry : allBadges.entrySet()) {
                String badgeName = entry.getKey();
                Map<String, Object> badgeData = entry.getValue();
                player.sendMessage(String.format("Badge Name: %s, Hover Text: %s, Text: %s, Locked: %s",
                        badgeName,
                        badgeData.get("hoverText"),
                        badgeData.get("text"),
                        badgeData.get("locked")));
            }
        } else {
            // List awarded/owned badges (This would need additional implementation to track individual player badges)
            player.sendMessage("Listing awarded badges is not implemented.");
        }
        return true;
    }

    private boolean handleAwardBadge(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /badge award <badgeName> <player>");
            return true;
        }
        String badgeName = args[1];
        String targetPlayerName = args[2];

        if (!badgeConfig.badgeExists(badgeName)) {
            player.sendMessage("Badge does not exist.");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            player.sendMessage("Player not found.");
            return true;
        }

        // Award badge to player (requires additional implementation for tracking player badges)
        player.sendMessage("Badge awarded successfully.");
        return true;
    }

    private boolean handleUnawardBadge(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /badge unaward <badgeName> <player>");
            return true;
        }
        String badgeName = args[1];
        String targetPlayerName = args[2];

        if (!badgeConfig.badgeExists(badgeName)) {
            player.sendMessage("Badge does not exist.");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            player.sendMessage("Player not found.");
            return true;
        }

        // Unaward badge from player (requires additional implementation for tracking player badges)
        player.sendMessage("Badge unawarded successfully.");
        return true;
    }

    private boolean handleSelectBadge(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /badge select <badgeName>");
            return true;
        }
        String badgeName = args[1];

        if (!badgeConfig.badgeExists(badgeName)) {
            player.sendMessage("Badge does not exist.");
            return true;
        }

        Map<String, Object> badgeData = badgeConfig.getBadge(badgeName);
        boolean isLocked = (boolean) badgeData.get("locked");

        if (isLocked) {
            player.sendMessage("This badge is locked and cannot be selected.");
            return true;
        }

        // Set the badge in the playerlist (requires additional implementation for playerlist display)
        player.sendMessage("Badge selected successfully.");
        return true;
    }

    private boolean handleUnselectBadge(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /badge unselect <badgeName>");
            return true;
        }
        String badgeName = args[1];

        // Reset the selected badge (requires additional implementation for playerlist display)
        player.sendMessage("Badge unselected successfully.");
        return true;
    }
}
