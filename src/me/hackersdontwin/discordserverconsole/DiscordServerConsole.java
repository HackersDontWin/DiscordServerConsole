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
        Bukkit.getLogger().info("Loading the config file...");

        if(!this.getDataFolder().exists()) {
            Bukkit.getLogger().info("The plugin folder wasn't found! Creating one...");
            this.getDataFolder().mkdir();
            Bukkit.getLogger().info("PLUGIN FOLDER WAS CREATED!");
        }
        Bukkit.getLogger().info("The plugin folder was found!");

        File file = new File(this.getDataFolder().getPath() + "/libs");
        if(!file.exists()) {
            Bukkit.getLogger().info("Libs folder wasn't found! Creating one...");
            file.mkdir();
            Bukkit.getLogger().info("LIBS FOLDER WAS CREATED!");
        }
        Bukkit.getLogger().info("The libs folder was found!");

        Http http = new Http();
        File jdaJarfile = new File(this.getDataFolder().getPath() + "/libs/JDA-4.2.0_168-withDependencies.jar");
        File slfJarfile = new File(this.getDataFolder().getPath() + "/libs/slf4j-nop-1.7.26.jar");
        if(!jdaJarfile.exists()) {
            Bukkit.getLogger().info("JDA API wasn't found! Downloading JDA API...");
            http.get("https://github.com/HackersDontWin/Dependencies/releases/download/DSC2.0/JDA-4.2.0_168-withDependencies.jar", this.getDataFolder().getPath().toString() + "/libs/JDA-4.2.0_168-withDependencies.jar");
            Bukkit.getLogger().info("JDA API was downloaded successfully!");
        }
        if(!slfJarfile.exists()) {
            Bukkit.getLogger().info("SLF4J wasn't found! Downloading SLF4J...");
            http.get("https://github.com/HackersDontWin/Dependencies/releases/download/DSC2.0/slf4j-nop-1.7.26.jar", this.getDataFolder().getPath().toString() + "/libs/slf4j-nop-1.7.26.jar");
            Bukkit.getLogger().info("SLF4J was downloaded successfully!");
        }
        Bukkit.getLogger().info("All the dependencies were found! Loading the config file...");

		config = new Config(this);

        Bukkit.getLogger().info("The config file has been loaded! Attempting to start up the bot...");

        try {
            jda = JDABuilder.createDefault(config.getToken())
                    .build();
            jda.addEventListener(new DiscordEvents(this, this.jda));
            Bukkit.getLogger().info("The bot is now online!");
        } catch (LoginException e) {
            Bukkit.getLogger().severe("Invalid bot token! Make sure to put a bot token in the config file! Without this the plugin won't work! Shutting down the server...");
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
        appender.stop();
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