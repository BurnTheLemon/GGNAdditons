package de.burnthelemon.ggnadditons.commands;

import de.burnthelemon.ggnadditons.util.DirectMessageComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DirectMessageCommand implements CommandExecutor {
    private final DirectMessageComponent directMessageComponent = new DirectMessageComponent();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length < 2) {
                sender.sendMessage("Usage: /msg <player> <message>");
                return false;
            }

            String receiverName = args[0];
            // Join all arguments from index 1 to the end into a single string with spaces
            String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));

            directMessageComponent.sendMessage(player, receiverName, message);
            return true;
        } else {
            sender.sendMessage("This command can only be used by players.");
            return false;
        }
    }
}
