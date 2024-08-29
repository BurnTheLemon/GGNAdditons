package de.burnthelemon.ggnadditons.listener;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ChatTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 0) {
            return suggestions;
        }

        // Check if the last argument starts with '@'
        String lastArg = args[args.length - 1];
        if (lastArg.startsWith("@")) {
            String prefix = lastArg.substring(1).toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(prefix)) {
                    suggestions.add("@" + player.getName());
                }
            }
        }

        return suggestions;
    }
}
