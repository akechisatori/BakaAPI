package moe.satori.BakaAPI.Controller;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

import com.google.gson.JsonObject;
import moe.satori.BakaAPI.app;
import moe.satori.BakaAPI.consts;
import moe.satori.BakaAPI.gson;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import static org.bukkit.attribute.Attribute.*;

public class players {
	public static Map<String, Object> online_list(Map<String, List<String>> params) {
		ArrayList<Object> playerlist = new ArrayList<>();
		Bukkit.getOnlinePlayers().forEach((player) -> {
			Map<String,Object> p = Map.of(
					"name", player.getName(),
					"uuid",player.getUniqueId().toString(),
					"level", player.getLevel(),
					"health", player.getHealth(),
					"display", player.getDisplayName(),
					"ip", player.getAddress().getHostString()
			);
			playerlist.add(p);
		});
		Map<String, Object> result = Map.of(
			"online", playerlist
		);
		return consts.SUCCESS(result);
	}

	public static Map<String, Object> kick(Map<String, List<String>> params) {
		final String playerName = params.get("username").get(0);
		final String message = params.get("message").get(0);
		final Player player = Bukkit.getPlayer(playerName);

		if (player == null) {
			return consts.USER_NOT_ONLINE();
		}
		Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("BakaAPI"), new Runnable() {
			public void run() {
				if (player.isOnline()) {
					player.kickPlayer(message);
				}
			}
		});
		return consts.SUCCESS();
	}

	public static Map<String, Object> send_message(Map<String, List<String>> params) {
		String playerName = params.get("username").get(0);
		String content = params.get("message").get(0);
		Player player = Bukkit.getPlayer(playerName);

		if (player == null) {
			return consts.USER_NOT_ONLINE();
		}

		player.sendMessage(content);
		return consts.SUCCESS();
	}

	public static Map<String,Object> batch_stats(Map<String, List<String>> params) {
		String[] uuids = params.get("uuids").get(0).split(",");
		ArrayList<Object> stats_list = new ArrayList<>();
		for (String uuid : uuids) {
			Map result = stats(Map.of(
					"uuid", uuid
			));
            if (result == null) {
                stats_list.add(Map.of(
                        "exists", false,
                        "uuid", uuid

                ));
                continue;
            }
            stats_list.add(Map.of(
                    "exists", true,
                    "uuid", uuid,
                    "data", result

            ));
		}
		return consts.SUCCESS(Map.of(
				"results", stats_list
		));
	}
	public static Map<String,Object> batch_info(Map<String, List<String>> params) {
		String[] uuids = params.get("uuids").get(0).split(",");
		ArrayList<Object> stats_list = new ArrayList<>();
		for (String uuid : uuids) {
			Map result = info(Map.of(
					"uuid", uuid
			));
			if (result == null) {
                stats_list.add(Map.of(
                        "exists", false,
                        "uuid", uuid
                ));
			    continue;
            }
            stats_list.add(Map.of(
                    "exists", true,
                    "uuid", uuid,
                    "data", result

            ));
		}
		return consts.SUCCESS(Map.of(
				"results", stats_list
		));
	}

	private static Map<String, Object> stats(Map<String, String> params) {
		String playerUUID = params.get("uuid");
		OfflinePlayer offline_player = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));

		String folder = Bukkit.getWorld(app.main_world).getWorldFolder().getPath();
		String stats_path = folder + "/stats/" + playerUUID + ".json";
		File stats_file = new File(stats_path);
		if (!stats_file.exists()) {
			return null;
		}
		try {
			FileInputStream file = new FileInputStream(stats_file);
			BufferedReader buf = new BufferedReader(new InputStreamReader(file));
			String line = buf.readLine(); StringBuilder sb = new StringBuilder();
			while(line != null){
				sb.append(line).append("\n"); line = buf.readLine();
			}
			JsonObject json = gson.parseJSON(sb.toString());
			return Map.of(
			        "name", info(Map.of("uuid", playerUUID)).get("username"),
                    "stats", json.get("stats").getAsJsonObject(),
                    "version", json.get("DataVersion").getAsInt()
            );
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Map<String, Object> info(Map<String, String> params) {
		ArrayList<Object> itemlist = new ArrayList<>();
		ArrayList<Map> potion_effect = new ArrayList<>();
		String playerUUID = params.get("uuid");
		OfflinePlayer offline_player = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
		Player player = offline_player.getPlayer();

		if (!offline_player.hasPlayedBefore()) {
			return null;
		}

		if (offline_player.isBanned()) {
			return Map.of(
                    "firstlogin", offline_player.getFirstPlayed(),
                    "lastlogin", offline_player.getLastPlayed(),
                    "banned", offline_player.isBanned(),
                    "username", offline_player.getName()
            );
		}

		if (player == null) {
			return Map.of(
                    "lastlogin", offline_player.getLastPlayed(),
                    "firstlogin", offline_player.getFirstPlayed(),
                    "banned", offline_player.isBanned(),
                    "username", offline_player.getName()
            );
		}

		BigDecimal next_level_exp = new BigDecimal(player.getExp());

		for (ItemStack item : player.getInventory().getContents()) {
			if (item != null && item.getType() != Material.AIR) {
				ArrayList<Map> enchantment = new ArrayList<>();
				HashMap<String,Object> temp_item = new HashMap<>();
				temp_item.put("name", item.getType().name());
				temp_item.put("material", item.getType().getKey());
				temp_item.put("count", item.getAmount());
				temp_item.put("durability", Map.of(
						"current", item.getDurability(),
						"max", item.getType().getMaxDurability()
				));
				item.getEnchantments().forEach((enchant, level) -> {
					enchantment.add(Map.of(
							"name", enchant.getKey(),
							"level", level

					));
				});
				temp_item.put("enchantments", enchantment);
				itemlist.add(temp_item);
			}
		}
		for (PotionEffect effect : player.getActivePotionEffects()) {
			potion_effect.add(Map.of(
					"type", effect.getType().getName(),
					"instant", effect.getType().isInstant(),
					"time", effect.getDuration()
			));
		}
		Map result = Map.of(
				"online", player.isOnline(),
				"lastlogin",offline_player.getLastPlayed(),
				"banned", offline_player.isBanned(),
				"firstlogin", offline_player.getFirstPlayed(),
				"ip", player.getAddress().getHostString(),
				"username", offline_player.getName(),
				"display", player.getDisplayName(),
				"world",player.getWorld().getName(),
				"inventory", itemlist,
				"stats", Map.of(
						"location", Map.of(
								"x", player.getLocation().getX(),
								"y",player.getLocation().getY(),
								"z",player.getLocation().getZ()
						),
						"health",Map.of(
								"current", player.getHealth(),
								"max", player.getAttribute(GENERIC_MAX_HEALTH).getValue()
						),
						"exp", Map.of(
								"total", player.getTotalExperience(),
								"next_level", next_level_exp.toPlainString(),
								"level", player.getLevel()
						),
						"potion_effect", potion_effect,
						"exhaustion",player.getExhaustion(),
						"food", player.getFoodLevel(),
						"hungry", player.getSaturation(),
						"flying", player.isFlying(),
						"canfly", player.getAllowFlight(),
						"gamemode",player.getGameMode().toString()
				)
		);
		return result;
	}
}
