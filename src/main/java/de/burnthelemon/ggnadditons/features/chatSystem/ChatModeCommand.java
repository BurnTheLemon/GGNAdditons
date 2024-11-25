package de.burnthelemon.ggnadditons.features.chatSystem;

import de.burnthelemon.ggnadditons.hooks.database.DatabaseHandler;
import de.burnthelemon.ggnadditons.hooks.database.PlayerDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChatModeCommand implements CommandExecutor {

    PlayerDataManager playerDataManager = new PlayerDataManager(new DatabaseHandler());

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(commandSender instanceof Player player)) return true;

        if(args.length != 1 || args[0].isEmpty()) {
            player.sendMessage("/chatmode <string>");
            return true;
        }

        if(args[0].equalsIgnoreCase("null"))
            playerDataManager.deletePlayerChatMode(player.getUniqueId().toString());
        else
            playerDataManager.setPlayerChatMode(player.getUniqueId().toString(),args[0]);

        return false;
    }
}
