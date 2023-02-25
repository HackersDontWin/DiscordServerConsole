package me.hackersdontwin.discordserverconsole;

import com.google.gson.JsonElement;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class DiscordEvents extends ListenerAdapter {

    private final DiscordServerConsole plugin;
    private final JDA jda;

    public DiscordEvents(DiscordServerConsole plugin, JDA j) {
        this.plugin = plugin;
        this.jda = j;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getAuthor() == e.getJDA().getSelfUser()) {
            return;
        }

        if(plugin.getBotConfig().getExcludedUserIDs().contains(e.getAuthor().getIdLong())) {
            return;
        }

        if (plugin.getBotConfig().getChannelIDs().contains(e.getChannel().getIdLong())) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), e.getMessage().getContentRaw());
                }
            }.runTask(plugin);
        }
    }

    @Override
    public void onReady(ReadyEvent e) {
        for (JsonElement element : plugin.getBotConfig().getConfig().get("channelIDs").getAsJsonArray()) {
            try {
                jda.getTextChannelById(element.getAsString()).sendMessage("```ini\n[Successfully connected the Minecraft server to the Discord text channel!]```").queue();
            } catch (NumberFormatException exception) {
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }
    }

}