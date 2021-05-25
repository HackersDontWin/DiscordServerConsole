package me.hackersdontwin.discordserverconsole;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {

	DiscordServerConsole plugin;

	private File file;
	private File oldConfigFile;
	private FileConfiguration oldConfig;
	private JsonObject config;

	public Config(DiscordServerConsole plugin) {
		this.plugin = plugin;
		this.file = new File(plugin.getDataFolder(), "config.json");
		this.oldConfigFile = new File(plugin.getDataFolder(), "config.yml");
		reload();
	}

	public void reload() {
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}

			if(file.exists()) {
				this.config = plugin.getGson().fromJson(new FileReader(this.file), JsonObject.class);
			} else {
				setupConfig();
			}

			if(oldConfigFile.exists()) {
				System.out.println("[DiscordServerConsole] Old configuration file found! Converting to new configuration file...");
				oldConfig = new YamlConfiguration();
				oldConfig.load(oldConfigFile);
				setupConfig(oldConfig.getString("Important.Token"), oldConfig.getString("Important.ChannelID"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setupConfig() {
		config = new JsonObject();
		config.addProperty("token", "Token goes in here");
		JsonArray channelIDs = new JsonArray();
		channelIDs.add("Add the discord channel ID here!");
		channelIDs.add("You can also add multiple channels!");
		config.add("channelIDs", channelIDs);
		save();
	}

	public void setupConfig(String token, String channelID) {
		config = new JsonObject();
		config.addProperty("token", token);
		JsonArray channelIDs = new JsonArray();
		channelIDs.add(channelID);
		config.add("channelIDs", channelIDs);

		try {
			oldConfigFile.delete();
		} catch (Exception e) {
			System.out.println("[DiscordServerConsole] Conversion of old configuration file failed! Please contact plugin support on the Discord server: https://discord.gg/d3ac2tJ");
		}
		System.out.println("[DiscordServerConsole] Conversion of old configuration file finished!");
		save();
	}

	public boolean save() {
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(plugin.getGson().toJson(config));
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public JsonObject getConfig() {
		return config;
	}

	public String getToken() {
		return config.get("token").getAsString();
	}

}
