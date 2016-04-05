/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aniuska.akioskoinf.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 *
 * @author hventura@citrus.com.do
 */
public class GsonUtils {

    private static final Gson GSON = new Gson();

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static <T extends Object> T from(String json, Class<T> classOf) {
        return GSON.fromJson(json, classOf);
    }

    public static <T extends Object> T from(JsonObject json, Class<T> classOf) {
        return GSON.fromJson(json, classOf);
    }

    public static JsonElement toJsonTree(Object obj) {
        return GSON.toJsonTree(obj);
    }

}
