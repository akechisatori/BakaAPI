package moe.satori.BakaAPI.Controller;

import moe.satori.BakaAPI.Const;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class server {
    public static HashMap<String, Object> status(Map<String, String> params) {
        HashMap<String, Object> result = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        ArrayList<Object> worlds = new ArrayList<>();
        HashMap<String, Object> player = new HashMap<>();
        HashMap<String, Object> memory = new HashMap<>();

        Bukkit.getWorlds().forEach((world) -> {
            ArrayList<Object> world_players = new ArrayList<>();

            world.getPlayers().forEach((current_world_player) -> {
                HashMap<String, Object> world_player = new HashMap<>();
                world_player.put("name", current_world_player.getName());
                world_player.put("uuid", current_world_player.getUniqueId().toString());
                world_player.put("ip", current_world_player.getAddress().getHostString());
                world_players.add(world_player);
            });
            HashMap<String, Object> world_result = new HashMap<>();
            world_result.put("type", world.getWorldType().getName());
            world_result.put("enviroment", world.getEnvironment().name());
            world_result.put("name", world.getName());
            world_result.put("uuid", world.getUID().toString());
            world_result.put("seed", world.getSeed());
            world_result.put("time", world.getFullTime());
            world_result.put("difficult", world.getDifficulty());
            world_result.put("players", world_players);
            worlds.add(world_result);
        });
        player.put("max", Bukkit.getMaxPlayers());
        player.put("online", Bukkit.getOnlinePlayers().size());
        player.put("count",Bukkit.getOfflinePlayers().length);

        memory.put("total", runtime.totalMemory());
        memory.put("free", runtime.freeMemory());

        result.put("player", player);
        result.put("memory", memory);
        result.put("worlds", worlds);
        result.put("version", Bukkit.getVersion());

        return Const.SUCCESS(result);
    }
}
