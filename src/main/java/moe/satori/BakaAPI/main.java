package moe.satori.BakaAPI;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {
	public static long start_time;
	@Override
	public void onEnable() {
		saveDefaultConfig();
		HashMap<String, Object> ServerConfig = new HashMap<>();
		ServerConfig.put("port", this.getConfig().getInt("port"));
		ServerConfig.put("auth", this.getConfig().getBoolean("auth"));
		ServerConfig.put("main_world", this.getConfig().getString("main_world"));
		ServerConfig.put("password", this.getConfig().getString("password"));
		app app = new app(this, ServerConfig);
		app.startService();
	}

	public static void main(String args[]) {
		HashMap<String,Object> result = new HashMap<>();

	}

	@Override
	public void onLoad() {
		start_time = System.currentTimeMillis() / 1000L;
	}

	@Override
	public void onDisable() {
		Bukkit.getLogger().info("Shutdowning HTTPD Task");
		Bukkit.getScheduler().cancelTask(app.httpd.getTaskId());
	}
}
