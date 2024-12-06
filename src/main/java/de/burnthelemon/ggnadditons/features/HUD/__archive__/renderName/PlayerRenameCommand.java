package de.burnthelemon.ggnadditons.features.HUD.__archive__.renderName;

import de.burnthelemon.ggnadditons.hooks.database.DatabaseHandler;
import de.burnthelemon.ggnadditons.hooks.database.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerRenameCommand implements CommandExecutor {
    private final PlayerDataManager playerDataManager;

    public PlayerRenameCommand() {
        this.playerDataManager = new PlayerDataManager(new DatabaseHandler()); // Instantiate DatabaseManager here
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Usage: /name <newName> or /name delete <username>");
            return true;
        }

        if (args[0].equalsIgnoreCase("delete")) {
            if (!sender.hasPermission("ggn.name.delete")) {
                sender.sendMessage("You do not have permission to delete usernames.");
                return true;
            }

            if (args.length != 2) {
                sender.sendMessage("Usage: /name delete <username>");
                return true;
            }

            String targetUsername = args[1];
            Player targetPlayer = Bukkit.getPlayer(targetUsername);

            if (targetPlayer != null) {
                String targetUUID = targetPlayer.getUniqueId().toString();
                playerDataManager.deletePlayerName(targetUUID); // Delete the name from the database
                targetPlayer.displayName(targetPlayer.name()); // Reset to default name
                targetPlayer.sendMessage("Your custom username has been deleted.");
                sender.sendMessage("Deleted custom username for " + targetUsername);
            } else {
                sender.sendMessage("Player not found.");
            }
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("Usage: /name <newName>");
            return true;
        }

        String newName = args[0];
        String playerUUID = player.getUniqueId().toString();

        // Store the new name in the database
        playerDataManager.updatePlayerName(playerUUID, newName);

        // Apply the name pattern
        player.sendMessage("Your name has been changed to " + newName);
        return true;
    }
}
