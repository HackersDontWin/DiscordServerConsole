package me.hackersdontwin.discordserverconsole;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class DiscordEvents extends ListenerAdapter {

    private FileManager fm;
    private JDA jda;

    public DiscordEvents(FileManager f, JDA j) {
    	this.fm = f;
    	this.jda = j;
	}

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if(e.getAuthor() == e.getJDA().getSelfUser()) {
            return;
        }

        if(fm.getConfig().getLong("Important.ChannelID") == e.getChannel().getIdLong()) {

            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), e.getMessage().getContentRaw());
                }
            }.runTask(DiscordServerConsole.getPlugin());

        }
    }

    @Override
    public void onReady(ReadyEvent e) {
        try {
            jda.getTextChannelById(fm.getChannelID()).sendMessage("```ini\n[Successfully connected the Minecraft server to the Discord text channel!]```").queue();
        } catch (Exception ex) {
            ex.printStackTrace();
            Bukkit.getLogger().severe("Invalid channel ID! Make sure to put a channel ID in the config file! Without this the plugin won't work! Shutting down the server...");
            System.exit(0);
        }

    }

}