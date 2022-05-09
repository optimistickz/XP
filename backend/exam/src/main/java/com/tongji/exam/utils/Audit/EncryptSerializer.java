package com.tongji.exam.utils.Audit;

import cn.hutool.crypto.digest.DigestUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * created by kz on
 */
public class EncryptSerializer implements JsonSerializer<String> {
    @Override
    public JsonElement serialize(String src, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement jsonElement = null;
        try {
            jsonElement =   context.serialize(DigestUtil.md5Hex(src));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonElement;
    }
}
