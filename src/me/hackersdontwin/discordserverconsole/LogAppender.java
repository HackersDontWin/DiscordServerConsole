package me.hackersdontwin.discordserverconsole;

import com.google.gson.JsonElement;
import net.dv8tion.jda.api.JDA;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class LogAppender extends AbstractAppender {

    private final DiscordServerConsole plugin;
    private String messages = "";
    private final JDA jda;

    public LogAppender(DiscordServerConsole plugin, JDA j) {
		super("MyLogAppender" + System.currentTimeMillis(), null, null);
    	this.plugin = plugin;
    	this.jda = j;
        // do your calculations here before starting to capture
        start();
    }

    @Override
    public void append(LogEvent event) {
        // you can get only the log message like this:
        String message = event.getMessage().getFormattedMessage();

        // and you can construct your whole log message like this:
        message = "[" + java.time.LocalTime.now() + " " + event.getLevel().toString() + "]: " + message + "\n";
        messages += message;
    }


    public void sendMessages() {

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (messages.length() != 0) {
                        messages = messages.replaceAll("\u001B\\[[;\\d]*m", "");
                        if(messages.length() > 2000) {
                            String messageTooLong = "\n\nThis message has exceeded the discord message limit (2000 characters) so the rest has been cut out. To see it completely please check the console itself!";
                            messages = messages.substring(0, (1999-messageTooLong.length())-6);
                            messages += messageTooLong;
                        }
                        for(JsonElement element : plugin.getBotConfig().getConfig().get("channelIDs").getAsJsonArray()) {
                            try {
                                jda.getTextChannelById(element.getAsString()).sendMessage("```" + messages + "```").queue();
                            } catch (NumberFormatException numberFormatException) {
                                Bukkit.getLogger().severe("[DiscordServerConsole] Invalid channel ID '" + element.getAsString() + "'! Make sure to put a valid channel ID in the config file! Without this the plugin won't work properly! If you're sure you've done this correctly, please contact plugin support on the Discord server: https://discord.gg/d3ac2tJ . Disabling plugin...");
                                cancel();
                                break;
                            }
                        }
                    }
                } catch (NullPointerException e) {

                }
                messages = "";
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

}