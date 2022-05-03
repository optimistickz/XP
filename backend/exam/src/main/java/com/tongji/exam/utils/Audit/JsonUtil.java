package com.tongji.exam.utils.Audit;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tongji.exam.annotation.ExcludeField;

/**
 * created by kz on
 */
public class JsonUtil {

    private static ExclusionStrategy exclusionStrategy = new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            return fieldAttributes.getAnnotation(ExcludeField.class)!=null;
        }

        @Override
        public boolean shouldSkipClass(Class<?> aClass) {
            return false;
        }
    };
    private static Gson gson = new GsonBuilder().addSerializationExclusionStrategy(exclusionStrategy).create();

    public static String toJsonString(Object object)
    {
        return gson.toJson(object);
    }
}
