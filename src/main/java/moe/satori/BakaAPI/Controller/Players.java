package moe.satori.BakaAPI.Controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import moe.satori.BakaAPI.Const;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Players {
	public static HashMap<String, Object> getOnline(Map<String, String> params) {
		ArrayList<Object> playerlist = new ArrayList<>();
		HashMap<String, Object> result = new HashMap<>();
		Bukkit.getOnlinePlayers().forEach((player) -> {
			HashMap<String,Object> p = new HashMap<>();
			p.put("name", player.getName());
			p.put("uuid", player.getUniqueId().toString());
			p.put("level", player.getLevel());
			p.put("health", player.getHealth());
			p.put("display", player.getDisplayName());
			p.put("ip", player.getAddress().getHostString());
			playerlist.add(p);
		});
		result.put("online", playerlist);

		return Const.SUCCESS(result);
	}

	public static HashMap<String, Object> kickPlayer(Map<String, String> params) {
		final String playerName = params.get("username");
		final String content = params.get("message");
		final Player player = Bukkit.getPlayer(playerName);

		if (player == null) {
			return Const.USER_NOT_ONLINE();
		}

		Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("BakaAPI"), new Runnable() {
			public void run() {
				if (player.isOnline()) {
					player.kickPlayer(content);
				}
			}
		});
		return Const.SUCCESS();
	}

	public static HashMap<String, Object> sendMessage(Map<String, String> params) {
		String playerName = params.get("username");
		String content = params.get("message");
		Player player = Bukkit.getPlayer(playerName);

		if (player == null) {
			return Const.USER_NOT_ONLINE();
		}

		player.sendMessage(content);
		return Const.SUCCESS();
	}

	public static HashMap<String, Object> getInfo(Map<String, String> params) {
		HashMap<String, Object> result = new HashMap<>();
		HashMap<String, Object> location = new HashMap<>();
		HashMap<String,Object> exp = new HashMap<>();
		ArrayList<Object> itemlist = new ArrayList<>();
		String playerName = params.get("username");
		Player player = Bukkit.getPlayer(playerName);

		if (player == null) {
			return Const.USER_NOT_ONLINE();
		}
//		HashMap<String, Object> bed_location = new HashMap<>();
//		Location bed = player.getBedSpawnLocation();

//		if (bed == null) {
//			result.put("bed", null);
//		} else {
//			bed_location.put("x", bed.getX());
//			bed_location.put("y", bed.getY());
//			bed_location.put("z", bed.getZ());
//		}

		location.put("x", player.getLocation().getX());
		location.put("y", player.getLocation().getY());
		location.put("z", player.getLocation().getZ());
		result.put("location", location);

		result.put("health", player.getHealth());
		result.put("food", player.getFoodLevel());
		result.put("hungry", player.getSaturation());
		result.put("ip", player.getAddress().getHostString());
		result.put("uuid", player.getUniqueId().toString());
		result.put("display", player.getDisplayName());
		result.put("fly", player.getAllowFlight());

		BigDecimal next_level_exp = new BigDecimal(player.getExp());
		exp.put("total", player.getTotalExperience());
		exp.put("next_level", next_level_exp.toPlainString());
		exp.put("level", player.getLevel());

		result.put("exp",exp);


		for (ItemStack item : player.getInventory().getContents()) {
			if (item != null && item.getType() != Material.AIR) {
				HashMap<Object, Object> temp_item = new HashMap<Object, Object>();
				temp_item.put("name", item.getType().name());
				temp_item.put("material", item.getType().getKey());
				HashMap<Object,Object> durability = new HashMap<>();

				durability.put("current", item.getDurability());
				durability.put("max", item.getType().getMaxDurability());
				temp_item.put("durability", durability);


				ArrayList<HashMap> enchantment = new ArrayList<>();

				item.getEnchantments().forEach((enchant, level) -> {
					HashMap<String, Object> enchants = new HashMap<>();
					enchants.put("name", enchant.getKey());
					enchants.put("level", level);
					enchantment.add(enchants);
				});
				temp_item.put("enchaments", enchantment);
				temp_item.put("count", item.getAmount());
				itemlist.add(temp_item);
			}
		}
		result.put("online", player.isOnline());
		result.put("inventory", itemlist);

		return Const.SUCCESS(result);
	}
}
