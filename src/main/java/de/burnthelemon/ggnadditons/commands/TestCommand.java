package de.burnthelemon.ggnadditons.commands;

import de.burnthelemon.ggnadditons.commands.commandhandler.*;
import org.bukkit.entity.Player;

@CMDPermission("test.raw")
@CommandName("test")
@CommandSource(player = true, console = true, commandBlock = false)
public class TestCommand {
    // Players and console can execute this command.
    // Command blocks cannot execute this command.
    // The /test command uses "test.raw" permission.
    // Console and command blocks skip all permissions.

    private void onDefaultCommandExecution(Player p) {
        // This should link to the permission above the class "test.raw"
        // This should link to the commandname /test with no added arguments
        // This should link to the class CommandSource, players and the console are allowed to execute this
        p.sendMessage("Default command executed!");
    }

    @SubCMD("lols")
    @CMDPermission("test.test")
    @CMDAlias("test")
    @CommandSource(player = true, console = false, commandBlock = false)
    private void openScoreMenu(Player p) {
        // Logic for /test settings command
        // The alias /test test works too
        // Console cannot execute /test test or /test settings
        p.sendMessage("Settings menu opened!");
    }

    @SubCMD({"lols", "wale"})
    @CMDPermission("test.test.wale")
    @CommandSource(player = true, console = true, commandBlock = false)
    private void openScoreMenuWale(Player p) {
        // Logic for /test settings wale command
        // The command uses the permission test.test.wale.
        // Non-players ignore the permission.
        // Command blocks cannot execute this command.
        p.sendMessage("Wale settings menu opened!");
    }
}
