package me.hackersdontwin.discordserverconsole;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager {

    private File configFile;
    private FileConfiguration config;

    private String token;
    private Long channelID;

    public void loadConfigFile() {
        configFile = new File(DiscordServerConsole.getPlugin().getDataFolder(), "config.yml");
        if(!configFile.exists()) {
            DiscordServerConsole.getPlugin().saveResource("config.yml", false);
            DiscordServerConsole.getPlugin().getLogger().info("The config file has been created! Attempting to load it up...");
        }

        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (Exception e) {
            DiscordServerConsole.getPlugin().getLogger().info("There was an error while loading the config file!");
            e.printStackTrace();
        }
        setConfig();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    private void setConfig(){
        if(!getConfig().contains("Important")) {
            getConfig().createSection("Important");
            getConfig().createSection("Important.Token");
            getConfig().createSection("Important.ChannelID");

            getConfig().set("Important.Token", "Put your bot token here!");
            getConfig().set("Important.ChannelID", "Put the channel ID here of where you want the console to go!");
            saveConfig();
        }
        token = getConfig().getString("Important.Token");
        channelID = getConfig().getLong("Important.ChannelID");
    }

    public void saveConfig() {
        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getToken() {
        return token;
    }

    public Long getChannelID() {
        return channelID;
    }
}
