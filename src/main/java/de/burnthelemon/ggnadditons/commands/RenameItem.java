package de.burnthelemon.ggnadditons.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RenameItem implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("Please provide a new name for the item.");
            return true;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand == null) {
            player.sendMessage("You must be holding an item to rename it.");
            return true;
        }

        String input = String.join(" ", args);
        Component displayName = MiniMessage.miniMessage().deserialize(input);

        itemInHand.editMeta(meta -> meta.displayName(displayName));
        player.sendMessage("Item renamed successfully!");

        return true;
    }
}
