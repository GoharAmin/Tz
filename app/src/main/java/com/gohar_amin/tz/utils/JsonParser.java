package com.gohar_amin.tz.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class JsonParser {
    public static <T>String  toJson(T t){
        return new Gson().toJson(t);
    }
    public static <T> T toObject(String json,Class clazz){
        return (T)new Gson().fromJson(json,clazz);
    }
    public static <T> T toList(String json, Class clazz){
        Type type = TypeToken.getParameterized(ArrayList.class, clazz).getType();
        return (T)new Gson().fromJson(json,type);
    }

}
