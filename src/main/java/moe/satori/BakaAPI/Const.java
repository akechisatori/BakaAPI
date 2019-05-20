package moe.satori.BakaAPI;

import java.util.HashMap;
import java.util.Map;

public class Const {
    public static Map<String, Object> USER_NOT_ONLINE() {
        return Map.of(
                "status", 200,
                "message", "User Not Online",
                "result", Map.of(
                        "online", false
                )
        );
    }
    public static Map<String,Object> SUCCESS() {
        return SUCCESS(null);
    }
    public static Map<String,Object> SUCCESS(Map<String, Object> data) {
        return Map.of(
                "status", 200,
                "message", "OK",
                "data", data
        );
    }
}
