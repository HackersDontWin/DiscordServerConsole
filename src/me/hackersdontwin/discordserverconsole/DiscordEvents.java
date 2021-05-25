package me.hackersdontwin.discordserverconsole;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class DiscordEvents extends ListenerAdapter {

    private DiscordServerConsole plugin;
    private JDA jda;

    public DiscordEvents(DiscordServerConsole plugin, JDA j) {
    	this.plugin = plugin;
    	this.jda = j;
	}

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if(e.getAuthor() == e.getJDA().getSelfUser()) {
            return;
        }

        if(plugin.config.getConfig().get("channelIDs").getAsJsonArray().contains(new JsonPrimitive(e.getChannel().getId()))) {
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
		for(JsonElement element : plugin.config.getConfig().get("channelIDs").getAsJsonArray()) {
			try {
				jda.getTextChannelById(element.getAsString()).sendMessage("```ini\n[Successfully connected the Minecraft server to the Discord text channel!]```").queue();
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("[DiscordServerConsole] Invalid channel ID '" + element.getAsString() + "'! Make sure to put a valid channel ID in the config file! Without this the plugin won't work! If you're sure you've done this correctly, please contact plugin support on the Discord server: https://discord.gg/d3ac2tJ . Shutting down the server...");
				System.exit(0);
			}
		}


    }

}