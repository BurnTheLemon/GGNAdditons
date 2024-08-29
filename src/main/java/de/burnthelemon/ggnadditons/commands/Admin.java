package de.burnthelemon.ggnadditons.commands;

import de.burnthelemon.ggnadditons.Main;
import de.burnthelemon.ggnadditons.commands.commandhandler.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

@CMDPermission("ggn.admin")
@CommandName("admin")
@CommandSource(player = true, console = false, commandBlock = false)
public class Admin {

    FileConfiguration config = Main.getPlugin().getDefaultConfig().getCfg();

    private void onDefaultCommandExecution(Player p) {
        p.sendMessage(MiniMessage.miniMessage().deserialize("<white>Usage: <gray>/admin end_respawn"));
    }

    @CMDPermission("ggn.admin.end_respawn")
    @SubCMD("end_respawn")
    @CommandSource(player = true, console = false, commandBlock = false)
    private void endRespawn(Player p) {
        config.set("world.endreturnpoint", p.getLocation());
        p.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>End return point has been sucessfully set to: <newline><gray>" +
                        "World: <yellow>" + p.getLocation().getWorld().getName() + "</yellow><newline>" +
                        "X: <yellow>" + p.getLocation().getX() + "</yellow><newline>" +
                        "Y: <yellow>" + p.getLocation().getY() + "</yellow><newline>" +
                        "Z: <yellow>" + p.getLocation().getZ() + "</yellow><newline>"
        ));
        Main.getPlugin().getDefaultConfig().save();


    }

    @SubCMD("save_config")
    @CommandSource(player = true, console = false, commandBlock = false)
    private void saveCfg(Player player) {

        Main.getPlugin().getDefaultConfig().save();
        player.sendMessage("Done!");

    }
}
