package moe.satori.BakaAPI;

import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;

import moe.satori.BakaAPI.App;

public class Main extends JavaPlugin {
	@Override
	public void onEnable() {
		saveDefaultConfig();
		HashMap<String, Object> ServerConfig = new HashMap<>();
		ServerConfig.put("port", this.getConfig().getInt("port"));
		ServerConfig.put("auth", this.getConfig().getBoolean("auth"));
		ServerConfig.put("password", this.getConfig().getString("password"));
		App app = new App(this, ServerConfig);
		app.startService();
	}

	public static void main(String[] args) {

	}

	@Override
	public void onDisable() {

	}
}
