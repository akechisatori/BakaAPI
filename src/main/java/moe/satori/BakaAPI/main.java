package moe.satori.BakaAPI;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {
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
	public void onDisable() {
		Bukkit.getLogger().info("Shutdowning HTTPD Task");
		Bukkit.getScheduler().cancelTask(app.httpd.getTaskId());
	}
}
