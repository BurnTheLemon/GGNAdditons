package de.burnthelemon.zestkit.features.nicknames;

import de.burnthelemon.zestkit.hooks.database.PlayerDatabase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static de.burnthelemon.zestkit.Main.PLUGINPREFIX;

public class RenamePlayerCommand implements CommandExecutor {
    private final PlayerDatabase playerDataManager = new PlayerDatabase();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Usage: /name <newName> or /name delete <username>");
            return true;
        }

        if (args[0].equalsIgnoreCase("delete")) {
            // Check if the sender is an operator (admin)
            if (!sender.hasPermission(Permissions.DELETE.getNode())) {
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
                UUID targetUUID = targetPlayer.getUniqueId();
                playerDataManager.deletePlayerNickname(targetUUID); // Delete the name from the database
                targetPlayer.displayName(targetPlayer.name());
                targetPlayer.sendMessage("Your custom username has been deleted.");
                sender.sendMessage("Deleted custom username for " + targetUsername);
            } else {
                sender.sendMessage("Player not found.");
            }
            return true;
        }

        // Handle the rename functionality
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("Usage: /name <newName>");
            return true;
        }

        String newName = args[0];
        UUID playerUUID = player.getUniqueId();

        // Store the new name in the database
        playerDataManager.setPlayerNickname(playerUUID, newName);

        // Use MiniMessage to format the new name
        Component displayName = MiniMessage.miniMessage().deserialize(newName);
        player.displayName(displayName);

        player.sendMessage("Your name has been changed to " + MiniMessage.miniMessage().stripTags(MiniMessage.miniMessage().serialize(player.displayName())) );
        return true;
    }


    private enum Permissions {
        DELETE(PLUGINPREFIX + ".delete");

        private final String node;

        Permissions(String node) {
            this.node = node;
        }

        public String getNode() {
            return node;
        }
    }
}
