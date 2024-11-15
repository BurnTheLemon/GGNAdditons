package de.burnthelemon.ggnadditons.commands;

import de.burnthelemon.ggnadditons.util.CustomFormattingTags;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ModifyItem implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType().isAir()) {
            player.sendMessage("You must be holding an item to modify.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("Usage: /modifyitem <name|lore|custommodeldata> <args...>");
            return true;
        }

        String subCommand = args[0];
        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, subArgs.length);

        switch (subCommand.toLowerCase()) {
            case "name":
                modifyItemName(player, item, subArgs);
                break;
            case "lore":
                modifyItemLore(player, item, subArgs);
                break;
            case "custommodeldata":
                modifyItemCustomModelData(player, item, subArgs);
                break;
            default:
                player.sendMessage("Invalid sub-command. Usage: /modifyitem <name|lore|custommodeldata> <args...>");
        }

        return true;
    }

    private void modifyItemName(Player player, ItemStack item, String[] args) {
        if (args.length == 0) {
            player.sendMessage("Usage: /modifyitem name <name>");
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            meta = item.getItemMeta();
        }

        StringBuilder nameBuilder = new StringBuilder();
        for (String arg : args) {
            nameBuilder.append(arg).append(" ");
        }
        String name = nameBuilder.toString().trim();
        String nonItalicName = "<italic:false>" + name + "</italic:false>";
        Component displayName = MiniMessage.miniMessage().deserialize(nonItalicName, CustomFormattingTags.getCustomTags());
        meta.displayName(displayName);
        item.setItemMeta(meta);
        player.sendMessage("Item name modified.");
    }

    private void modifyItemLore(Player player, ItemStack item, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /modifyitem lore <add|set|remove> <args...>");
            return;
        }

        String action = args[0];
        String[] actionArgs = new String[args.length - 1];
        System.arraycopy(args, 1, actionArgs, 0, actionArgs.length);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            meta = item.getItemMeta();
        }
        List<Component> lore = meta.lore();
        if (lore == null) lore = new ArrayList<>();

        switch (action.toLowerCase()) {
            case "add":
                if (actionArgs.length == 0) {
                    player.sendMessage("Usage: /modifyitem lore add <lore>");
                    return;
                }
                StringBuilder addLoreBuilder = new StringBuilder();
                for (String arg : actionArgs) {
                    addLoreBuilder.append(arg).append(" ");
                }
                String addLore = addLoreBuilder.toString().trim();
                String nonItalicAddLore = "<italic:false>" + addLore + "</italic:false>";
                Component addComponent = MiniMessage.miniMessage().deserialize(nonItalicAddLore);
                lore.add(addComponent);
                meta.lore(lore);
                item.setItemMeta(meta);
                player.sendMessage("Lore added.");
                break;
            case "set":
                if (actionArgs.length < 2) {
                    player.sendMessage("Usage: /modifyitem lore set <line> <lore>");
                    return;
                }
                try {
                    int line = Integer.parseInt(actionArgs[0]) - 1;
                    StringBuilder setLoreBuilder = new StringBuilder();
                    for (int i = 1; i < actionArgs.length; i++) {
                        setLoreBuilder.append(actionArgs[i]).append(" ");
                    }
                    String setLore = setLoreBuilder.toString().trim();
                    String nonItalicSetLore = "<italic:false>" + setLore + "</italic:false>";
                    Component setComponent = MiniMessage.miniMessage().deserialize(nonItalicSetLore);
                    if (line >= 0 && line < lore.size()) {
                        lore.set(line, setComponent);
                        meta.lore(lore);
                        item.setItemMeta(meta);
                        player.sendMessage("Lore set.");
                    } else {
                        player.sendMessage("Invalid lore line number.");
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid line number.");
                }
                break;
            case "remove":
                if (actionArgs.length != 1) {
                    player.sendMessage("Usage: /modifyitem lore remove <line>");
                    return;
                }
                try {
                    int line = Integer.parseInt(actionArgs[0]) - 1;
                    if (line >= 0 && line < lore.size()) {
                        lore.remove(line);
                        meta.lore(lore);
                        item.setItemMeta(meta);
                        player.sendMessage("Lore removed.");
                    } else {
                        player.sendMessage("Invalid lore line number.");
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid line number.");
                }
                break;
            default:
                player.sendMessage("Invalid lore action. Usage: /modifyitem lore <add|set|remove> <args...>");
        }
    }

    private void modifyItemCustomModelData(Player player, ItemStack item, String[] args) {
        if (args.length != 1) {
            player.sendMessage("Usage: /modifyitem custommodeldata <number>");
            return;
        }

        try {
            int customModelData = Integer.parseInt(args[0]);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                meta = item.getItemMeta();
            }
            if (customModelData == 0) {
                meta.setCustomModelData(null);
            } else {
                meta.setCustomModelData(customModelData);
            }
            item.setItemMeta(meta);
            player.sendMessage("Custom model data modified.");
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid number.");
        }
    }
}
