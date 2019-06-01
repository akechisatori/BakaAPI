package moe.satori.BakaAPI.Controller;

import moe.satori.BakaAPI.consts;
import moe.satori.BakaAPI.main;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Map;

public class server {
    public static Map status(Map<String, Object> params) {
        Runtime runtime = Runtime.getRuntime();
        ArrayList<Object> worlds = new ArrayList<>();

        Bukkit.getWorlds().forEach((world) -> {
            ArrayList<Object> world_players = new ArrayList<>();
            world.getPlayers().forEach((current_world_player) -> {
                Map<String, Object> world_player = Map.of(
                        "name", current_world_player.getName(),
                        "uuid", current_world_player.getUniqueId().toString(),
                        "ip", current_world_player.getAddress().getHostString()
                );
                world_players.add(world_player);
            });
            Map<String, Object> world_result = Map.of(
                    "type", world.getWorldType().getName(),
                    "enviroment", world.getEnvironment().name(),
                    "name", world.getName(),
                    "uuid", world.getUID().toString(),
                    "seed",world.getSeed(),
                    "time",world.getFullTime(),
                    "difficult", world.getDifficulty(),
                    "players", world_players
            );
            worlds.add(world_result);
        });
        return consts.SUCCESS(Map.of(
                "player", Map.of(
                        "max", Bukkit.getMaxPlayers(),
                        "online", Bukkit.getOnlinePlayers().size(),
                        "count", Bukkit.getOfflinePlayers().length
                ),
                "memory", Map.of(
                        "total", runtime.totalMemory(),
                        "free", runtime.freeMemory()
                ),
                "start_time", main.start_time,
                "worlds", worlds,
                "version", Bukkit.getVersion()
        ));
    }
}
