package moe.satori.BakaAPI;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {
	@Override
	public void onEnable() {
		saveDefaultConfig();
		HashMap<String, Object> ServerConfig = new HashMap<>();
		ServerConfig.put("port", this.getConfig().getInt("port"));
		ServerConfig.put("auth", this.getConfig().getBoolean("auth"));
		ServerConfig.put("password", this.getConfig().getString("password"));
		app app = new app(this, ServerConfig);
		app.startService();
	}

	@Override
	public void onDisable() {
		Bukkit.getLogger().info("Shutdowning HTTPD Task");
		Bukkit.getScheduler().cancelTask(app.httpd.getTaskId());
	}
}
