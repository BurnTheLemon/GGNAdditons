package de.burnthelemon.ggnadditons.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckPingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ping")) {
            if (args.length == 0) {
                // No player specified, get the sender's ping if they are a player
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    int ping = player.getPing(); // Spigot/Paper method to get ping
                    player.sendMessage("Your ping is " + ping + " ms.");
                } else {
                    sender.sendMessage("Only players can check their own ping.");
                }
            } else if (args.length == 1) {
                // Player name specified, get that player's ping
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target != null) {
                    int ping = target.getPing();
                    sender.sendMessage(target.getName() + "'s ping is " + ping + " ms.");
                } else {
                    sender.sendMessage("Player not found.");
                }
            } else {
                sender.sendMessage("Usage: /ping [player]");
            }
            return true;
        }
        return false;
    }
}
