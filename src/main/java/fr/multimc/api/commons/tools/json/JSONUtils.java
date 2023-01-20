package fr.multimc.api.commons.tools.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONUtils {
    public static String toJson(Object object){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(object);
    }

    public static <T> T fromJson(Class<T> clazz, String json){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(json, clazz);
    }
}
