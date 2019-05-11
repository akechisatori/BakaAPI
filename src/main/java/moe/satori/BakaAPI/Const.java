package moe.satori.BakaAPI;

import java.util.HashMap;

public class Const {
    public static HashMap<String,Object> USER_NOT_ONLINE() {
        HashMap<String, Object> map = new HashMap<>();
        HashMap<String, Object> result = new HashMap<>();
        map.put("status", 200);
        map.put("message", "User Not Online");

        result.put("online", false);

        map.put("data", result);
        return map;
    };
    public static HashMap<String,Object> SUCCESS() {
        return SUCCESS(null);
    };
    public static HashMap<String,Object> SUCCESS(HashMap<String, Object> data) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("status", 200);
        result.put("message", "OK");
        result.put("data", data);

        return result;
    };
}
