package de.burnthelemon.ggnadditons.features.chatSystem;

import de.burnthelemon.ggnadditons.Main;
import de.burnthelemon.ggnadditons.hooks.database.DatabaseHandler;
import de.burnthelemon.ggnadditons.hooks.database.PlayerDataManager;
import de.burnthelemon.ggnadditons.hooks.discordBridge.DiscordManager;
import de.burnthelemon.ggnadditons.util.*;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ChatExecutor implements Listener {

    private final FileConfiguration config;
    private static LuckPerms api;
    private final DirectMessageComponent directMessageComponent = new DirectMessageComponent();
    private final DiscordManager discordManager = DiscordManager.getInstance();
    private final PlayerDataManager playerDataManager = new PlayerDataManager(new DatabaseHandler());

    public ChatExecutor() {
        this.config = Main.getPlugin().getDefaultConfig().getCfg();
        this.api = LuckPermsProvider.get();

        // Scheduler to update status every 30 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                discordManager.updateDiscordStatus();
            }
        }.runTaskTimer(Main.getPlugin(), 0L, 20L * 60 * 10);
        Main.getPlugin().getLogger().log(Level.INFO, "=> Discord Scheduler Started for every 10 Minutes");
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        Player player = e.getPlayer();
        String stringMessage = LegacyComponentSerializer.legacyAmpersand().serialize(e.message());
        e.viewers().clear();
        e.viewers().add(Bukkit.getConsoleSender());
        e.viewers().addAll(getMessage(player,stringMessage).getValue());

        if(e.viewers().size() == 2 && Arrays.stream(e.viewers().toArray()).toList().getFirst().equals(e.getPlayer()))
            e.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<white><hover:show_text:'Servermessage'><red><b>!</b></red><gray> No one has heard your message.<reset>"));


        String discordMessage = stringMessage.replaceAll("<[^>]*>", "");
        if(!discordMessage.isEmpty())
            discordManager.sendDiscordMessage(player,discordMessage);

        if(!PlainTextComponentSerializer.plainText().serialize(MiniMessage.miniMessage().deserialize(stringMessage)).isEmpty()) {
            e.renderer((cplayer, cdisplayName, coreMessage, cviewer) -> getMessage(player,stringMessage).getKey());

            Bukkit.getConsoleSender().sendMessage(
                    MiniMessage.miniMessage().deserialize(
                            "Message Listeners: " +
                                    Arrays.stream(e.viewers().toArray())
                                            .map(viewer -> viewer instanceof Player
                                                    ? ((Player) viewer).getName()
                                                    : viewer.toString())
                                            .collect(Collectors.joining(", "))
                    )
            );
        }
        else {
            e.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<white><hover:show_text:'Servermessage'><red><b>!</b></red><gray> You screamed but no words came out.<reset>"));
            e.setCancelled(true);
        }

    }

    public static String getPlayerTeamPrefix(Player player) {
        return api.getGroupManager().getGroup(
                api.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup()).getDisplayName() == null ?
                api.getGroupManager().getGroup(api.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup()).getName() :
                api.getGroupManager().getGroup(api.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup()).getDisplayName();
    }

    public String getChannelGroupIcon(Player player) {
        return switch (playerDataManager.getPlayerChatMode(player.getUniqueId().toString())) {
            case "discord" -> "<white><hover:show_text:'Send via Fakechat'><gradient:#7289da:#1e2124><#7289da>\uE007</#7289da></gradient></hover></white><reset>";
            case "local" -> "<white><hover:show_text:'<gray>Local Chat</gray>'>\uE006</hover></white>";
            case "global" -> "<white><hover:show_text:'<green>Global Chat</green>'>\uE005</hover></white>";
            case null, default -> "<white><hover:show_text:'<green>Global Chat</green>'>\uE005</hover></white>";
        };



    }

    private Map.Entry<Component, Set<Audience>> getMessage(Player player, String stringMessage) {
        Set<Audience> audience = new HashSet<>(Bukkit.getOnlinePlayers());
        Component sendMessage;

        int messageRange = stringMessage.startsWith("!") ? 100 : 50;

        if(playerDataManager.getPlayerChatMode(player.getUniqueId().toString()) == null) playerDataManager.setPlayerChatMode(player.getUniqueId().toString(),"global");

        if (Objects.equals(playerDataManager.getPlayerChatMode(player.getUniqueId().toString()), "local")) {
            audience.clear();
            audience.addAll(getPlayersInRange(player.getLocation(), messageRange));
        }

        sendMessage = MiniMessage.miniMessage().deserialize(
                getChannelGroupIcon(player) +
                        "<hover:show_text:'" + player.getName() + "'> " +
                        MiniMessage.miniMessage().serialize(player.displayName()) + "</hover><reset> " +
                        MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(
                                getPlayerTeamPrefix(player) == null ? "" : getPlayerTeamPrefix(player))) +
                        "<reset>> " +
                        stringMessage,
                CustomFormattingTags.getCustomTags()
        );

        return Map.entry(sendMessage, audience);
    }




    public static Player getPlayerByDisplayName(String displayName) {
        // Iterate over all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Check if the player's display name matches the given display name
            if (PlainTextComponentSerializer.plainText().serialize(player.displayName()).equalsIgnoreCase(PlainTextComponentSerializer.plainText().serialize(player.displayName())) ) {
                return player;
            }
        }
        return null;
    }

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
