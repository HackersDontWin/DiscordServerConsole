package me.hackersdontwin.discordserverconsole;

import com.google.gson.Gson;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;

public class DiscordServerConsole extends JavaPlugin {

	Gson gson = new Gson();

	public Config config;
    private static Plugin plugin;
    private JDA jda;
    private LogAppender appender;

    @Override
    public void onEnable() {
        plugin = this;

        if(!this.getDataFolder().exists()) {
			System.out.println("[DiscordServerConsole] The plugin folder wasn't found! Creating one...");
            this.getDataFolder().mkdir();
			System.out.println("[DiscordServerConsole] Plugin folder was created!");
        }
		System.out.println("[DiscordServerConsole] The plugin folder was found!");

        File file = new File(this.getDataFolder().getPath() + "/libs");
        if(!file.exists()) {
			System.out.println("[DiscordServerConsole] Libs folder wasn't found! Creating one...");
            file.mkdir();
			System.out.println("[DiscordServerConsole] Libs folder was created!");
        }
		System.out.println("[DiscordServerConsole] The libs folder was found!");

        Http http = new Http();
        File jdaJarfile = new File(this.getDataFolder().getPath() + "/libs/JDA-4.2.0_168-withDependencies.jar");
        File slfJarfile = new File(this.getDataFolder().getPath() + "/libs/slf4j-nop-1.7.26.jar");
        if(!jdaJarfile.exists()) {
			System.out.println("[DiscordServerConsole] JDA API wasn't found! Downloading JDA API...");
            http.get("https://github.com/HackersDontWin/Dependencies/releases/download/DSC2.0/JDA-4.2.0_168-withDependencies.jar", this.getDataFolder().getPath().toString() + "/libs/JDA-4.2.0_168-withDependencies.jar");
			System.out.println("[DiscordServerConsole] JDA API was downloaded successfully!");
        }
        if(!slfJarfile.exists()) {
			System.out.println("[DiscordServerConsole] SLF4J wasn't found! Downloading SLF4J...");
            http.get("https://github.com/HackersDontWin/Dependencies/releases/download/DSC2.0/slf4j-nop-1.7.26.jar", this.getDataFolder().getPath().toString() + "/libs/slf4j-nop-1.7.26.jar");
			System.out.println("[DiscordServerConsole] SLF4J was downloaded successfully!");
        }
		System.out.println("[DiscordServerConsole] All the dependencies were found!");

		System.out.println("[DiscordServerConsole] Loading the config file...");
		config = new Config(this);

		System.out.println("[DiscordServerConsole] The config file has been loaded! Attempting to start up the bot...");

        try {
            jda = JDABuilder.createDefault(config.getToken())
                    .build();
            jda.addEventListener(new DiscordEvents(this, this.jda));
			System.out.println("[DiscordServerConsole] The bot is now online!");
        } catch (LoginException e) {
            System.out.println("[DiscordServerConsole] Invalid bot token! Make sure to put a bot token in the config file! Without this the plugin won't work! Shutting down the server...");
            System.exit(0);
        }

        appender = new LogAppender(this, this.jda);
        try {
            final org.apache.logging.log4j.core.Logger logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
            logger.addAppender(appender);
        } catch(Exception e) {

        }

        appender.sendMessages();
    }

    public void onDisable() {
        plugin = null;
        final org.apache.logging.log4j.core.Logger logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        appender.stop();
        logger.removeAppender(appender);
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public JDA getJDA() {
        return jda;
    }

    public Gson getGson() {
    	return gson;
	}

}