package moe.satori.BakaAPI;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class gson {
    private static Gson gson;

    static {

        GsonBuilder gb =  new GsonBuilder();
        gb.serializeNulls();
        gson = gb.create();

    }

    public static String toJSON(Object map) {
        try {
            String result = gson.toJson(map);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
