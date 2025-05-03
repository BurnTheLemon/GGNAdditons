package de.burnthelemon.zestkit.util;

import de.burnthelemon.zestkit.Main;

import java.util.logging.Level;

public class LoggerUtility {

    public static void log(Level level, String message) {
        Main.getPlugin().getServer().getLogger().log(level, Main.PLUGINPREFIX + ": " + message);
    }
}
