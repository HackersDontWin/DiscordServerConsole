package me.hackersdontwin.discordserverconsole;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Config {

	DiscordServerConsole plugin;

	private final File file;
	private JsonObject config;

	public Config(DiscordServerConsole plugin) {
		this.plugin = plugin;
		this.file = new File(plugin.getDataFolder(), "config.json");
		loadConfig();
	}

	public void loadConfig() {
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}

			if(file.exists()) {
				this.config = plugin.getGson().fromJson(new FileReader(this.file), JsonObject.class);
				addNewConfigSections();
			} else {
				setupConfig();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setupConfig() {
		config = new JsonObject();
		config.addProperty("token", "Token goes in here");
		JsonArray channelIDs = new JsonArray();
		channelIDs.add(new JsonPrimitive("Add the discord channel ID here!"));
		channelIDs.add(new JsonPrimitive("You can also add multiple channels!"));
		config.add("channelIDs", channelIDs);
		JsonArray excludedUserIDs = new JsonArray();
		excludedUserIDs.add("Add any user ID here to exclude their messages from being read!");
		config.add("excludedUserIDs", excludedUserIDs);
		save();
	}

	private void addNewConfigSections() {
		if(!config.has("excludedUserIDs")) {
			JsonArray excludedUserIDs = new JsonArray();
			excludedUserIDs.add("Add any user ID here to exclude their messages from being read!");
			config.add("excludedUserIDs", excludedUserIDs);
			save();
		}
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

	public List<Long> getChannelIDs() {
		List<Long> ids = new ArrayList<>();
		for(JsonElement elm : config.getAsJsonArray("channelIDs")) {
			try {
				ids.add(Long.parseLong(elm.getAsString()));
			} catch (NumberFormatException e) {
				Bukkit.getLogger().severe("[DiscordServerConsole] The channel ID '" + elm.getAsString() + "' is invalid!");
			}
		}
		return ids;
	}

	public List<Long> getExcludedUserIDs() {
		List<Long> ids = new ArrayList<>();
		for(JsonElement elm : config.getAsJsonArray("excludedUserIDs")) {
			try {
				ids.add(Long.parseLong(elm.getAsString()));
			} catch (NumberFormatException e) {
				Bukkit.getLogger().warning("[DiscordServerConsole] The user ID '" + elm.getAsString() + "' is invalid!");
			}
		}
		return ids;
	}

}
