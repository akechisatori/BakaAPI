package moe.satori.BakaAPI;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.scheduler.BukkitTask;

public class app extends NanoHTTPD {
	Plugin plugin;
	String password;
	Boolean auth;
	public static BukkitTask httpd;
	
	public app(Plugin plugin, HashMap<String, Object> ServerConfig) {
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
					app.super.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
				} catch (IOException ioe) {
					System.err.println("Couldn't start server:\n" + ioe);
				}

			}
		};
		BukkitTask httpd_task = Bukkit.getScheduler().runTaskAsynchronously(plugin, httpd);
		this.httpd = httpd_task;
	}
	public Response responseJSON(Object map) {
		return newFixedLengthResponse(Response.Status.OK, "application/json", gson.toJSON(map));
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
		try {
			if (this.auth == true) {
				HashMap<String, Object> map = new HashMap<>();
				if (!headers.containsKey("x-authorizetoken")) {
					return responseJSON(Map.of(
							"status", 401,
							"message","Empty Token"
					));
				}
				if (!utils.checkToken(parms,this.password, headers)) {
					return responseJSON(Map.of(
							"status", 403,
							"message", "Token Verify Fail"
					));
				}
			}
			Object result = utils.invokeController(parms.get("action"), parms.get("method"),
					parms);
			return responseJSON(result);

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
			return responseJSON(map);
		}
	}

}
