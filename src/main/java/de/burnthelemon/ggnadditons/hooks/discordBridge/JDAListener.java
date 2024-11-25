package de.burnthelemon.ggnadditons.hooks.discordBridge;

import de.burnthelemon.ggnadditons.Main;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.GuildUnavailableEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class JDAListener extends ListenerAdapter {

    private final Main plugin;

    public JDAListener(Main plugin) {
        this.plugin = plugin;
    }

    @Override // we can use any of JDA's events through ListenerAdapter, just by overriding the methods
    public void onGuildUnavailable(@NotNull GuildUnavailableEvent event) {
        plugin.getLogger().severe("Oh no " + event.getGuild().getName() + " went unavailable :(");
    }

}