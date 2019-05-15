package moe.satori.BakaAPI;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.scheduler.BukkitTask;

import javax.rmi.CORBA.Util;

public class App extends NanoHTTPD {
	Plugin plugin;
	String password;
	Boolean auth;
	public static BukkitTask httpd;
	
	public App(Plugin plugin, HashMap<String, Object> ServerConfig) {
		super((int) ServerConfig.get("port"));
		System.out.println("BakaAPI Port: " + ServerConfig.get("port"));
		System.out.println("Use Authorize: " + ServerConfig.get("auth"));
		this.plugin = plugin;
		this.password = (String) ServerConfig.get("password");
		this.auth = (Boolean) ServerConfig.get("auth");
	}

	public void startService() {
		Runnable httpd = new Runnable() {
			public void run() {
				try {
					System.out.println("BakaAPI Service Running..");
					App.super.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
				} catch (IOException ioe) {
					System.err.println("Couldn't start server:\n" + ioe);
				}

			}
		};
		BukkitTask httpd_task = Bukkit.getScheduler().runTaskAsynchronously(plugin, httpd);
		this.httpd = httpd_task;
	}

	@Override
	public Response serve(IHTTPSession session) {

		Map<String, String> parms = session.getParms();
		Map<String, String> headers = session.getHeaders();
		Method method = session.getMethod();
		if (Method.POST.equals(method)) {
			try {
				session.parseBody(parms);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String response = "";
		try {
			if (this.auth == true) {
				HashMap<String, Object> map = new HashMap<>();
				if (!headers.containsKey("x-authorizetoken")) {
					map.put("status", 401);
					map.put("message", "Empty Token");
					return newFixedLengthResponse(Response.Status.OK, "application/json", Utils.toJSON(map));
				}
				if (!Utils.checkToken(parms,this.password, headers)) {
					map.put("status", 403);
					map.put("message", "Token Verify Fail");
					return newFixedLengthResponse(Response.Status.OK, "application/json", Utils.toJSON(map));
				}
			}
			Object result = Utils.invokeController(parms.get("action"), parms.get("method"),
					parms);
			response = Utils.toJSON(result);

		} catch (Exception e) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("status", 500);
			Throwable error = e.getCause();
			if (error == null) {
				map.put("exception",e.toString());
				map.put("stack", e.getStackTrace());
			} else {
				map.put("exception",e.getCause().toString());
				map.put("stack", e.getCause().getStackTrace());
			}

			response = Utils.toJSON(map);

		}
		return newFixedLengthResponse(Response.Status.OK, "application/json", response);
	}

}
