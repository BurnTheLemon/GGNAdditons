package de.burnthelemon.zestkit.util;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.entity.Player;

import java.util.Objects;

public class TeamPrefix {

    private static final LuckPerms api = LuckPermsProvider.get();

    public static String playerPrefix(Player player) {
        return api.getGroupManager().getGroup(
                Objects.requireNonNull(api.getUserManager().getUser(player.getUniqueId())).getPrimaryGroup()).getDisplayName() == null ?
                api.getGroupManager().getGroup(api.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup()).getName() :
                api.getGroupManager().getGroup(api.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup()).getDisplayName();
    }
}
