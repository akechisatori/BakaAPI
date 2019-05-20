package moe.satori.BakaAPI.Controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import moe.satori.BakaAPI.Const;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import static org.bukkit.attribute.Attribute.*;

public class players {
	public static Map<String, Object> online_list(Map<String, String> params) {
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
		return Const.SUCCESS(result);
	}

	public static Map<String, Object> kick(Map<String, String> params) {
		final String playerName = params.get("username");
		final String message = params.get("message");
		final Player player = Bukkit.getPlayer(playerName);

		if (player == null) {
			return Const.USER_NOT_ONLINE();
		}
		Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("BakaAPI"), new Runnable() {
			public void run() {
				if (player.isOnline()) {
					player.kickPlayer(message);
				}
			}
		});
		return Const.SUCCESS();
	}

	public static Map<String, Object> send_message(Map<String, String> params) {
		String playerName = params.get("username");
		String content = params.get("message");
		Player player = Bukkit.getPlayer(playerName);

		if (player == null) {
			return Const.USER_NOT_ONLINE();
		}

		player.sendMessage(content);
		return Const.SUCCESS();
	}

	public static Map<String, Object> info(Map<String, String> params) {
		HashMap<String,Object> exp = new HashMap<>();
		ArrayList<Object> itemlist = new ArrayList<>();
		ArrayList<Map> potion_effect = new ArrayList<>();
		String playerName = params.get("username");
		Player player = Bukkit.getPlayer(playerName);

		if (player == null) {
			return Const.USER_NOT_ONLINE();
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
				"stats", Map.of(
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
				),
				"ip", player.getAddress().getHostString(),
				"uuid", player.getUniqueId().toString(),
				"display", player.getDisplayName(),
				"world",player.getWorld().getName(),
				"inventory", itemlist,
				"location", Map.of(
						"x", player.getLocation().getX(),
						"y",player.getLocation().getY(),
						"z",player.getLocation().getZ()
				)
		);
		return Const.SUCCESS(result);
	}
}
