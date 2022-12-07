package me.hackersdontwin.discordserverconsole;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;

public class DiscordServerConsole extends JavaPlugin {

	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private Config config;
    private JDA jda;
    private LogAppender appender;
    private org.apache.logging.log4j.core.Logger logger;

    private DiscordEvents discordEvents;

    @Override
    public void onEnable() {
        if(!this.getDataFolder().exists()) {
            Bukkit.getLogger().info("[DiscordServerConsole] The plugin folder wasn't found! Creating one...");
            this.getDataFolder().mkdir();
            Bukkit.getLogger().info("[DiscordServerConsole] Plugin folder was created!");
        }
        Bukkit.getLogger().info("[DiscordServerConsole] The plugin folder was found!");

        Bukkit.getLogger().info("[DiscordServerConsole] Loading the config file...");
		config = new Config(this);

        Bukkit.getLogger().info("[DiscordServerConsole] The config file has been loaded! Attempting to start up the bot...");

        try {
            jda = JDABuilder.createLight(config.getToken(), GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                    .build();

            discordEvents  = new DiscordEvents(this, this.jda);

            jda.addEventListener(discordEvents);
            Bukkit.getLogger().info("[DiscordServerConsole] The bot is now online!");

            appender = new LogAppender(this, this.jda);
            try {
                logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
                logger.addAppender(appender);
            } catch(Exception e) {
                e.printStackTrace();
            }

            appender.sendMessages();
        } catch (InvalidTokenException | IllegalArgumentException e) {
            Bukkit.getLogger().severe("[DiscordServerConsole] The token in the config file is invalid! Please change the token in the config and restart/reload the server to apply the changes.");
            Bukkit.getLogger().info("[DiscordServerConsole] Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if(jda != null) {
            jda.removeEventListener(discordEvents);
            logger.removeAppender(appender);
            appender.stop();
        }
    }

    public Config getBotConfig() {
        return config;
    }

    public Gson getGson() {
    	return gson;
	}

}