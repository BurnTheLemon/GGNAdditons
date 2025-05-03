package de.burnthelemon.zestkit.util;

import de.burnthelemon.zestkit.hooks.database.ChatChannelDatabase;
import de.burnthelemon.zestkit.hooks.database.MembershipDatabase;
import de.burnthelemon.zestkit.hooks.database.PlayerDatabase;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;

public class ChatUtil {

    public static Collection<Player> getPlayersInRange(Location location, double radius) {
        double sqrRadius = radius * radius;
        World world = location.getWorld();
        Collection<Player> result = new HashSet<>();
        for (Player player : world.getPlayers()) {
            if (player.getLocation().distanceSquared(location) <= sqrRadius) {
                result.add(player);
            }
        }
        return result;
    }
}
