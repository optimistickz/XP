package com.tongji.exam.utils.Audit;

import io.swagger.models.auth.In;

/**
 * created by kz on
 */
public class AuditUtil {

    private static ThreadLocal<Integer> traceIdLocal = new ThreadLocal<>();

    private static ThreadLocal<Object> userIdLocal = new ThreadLocal<>();

    public static ThreadLocal<Integer> getTraceIdLocal() {
        return traceIdLocal;
    }

    public static ThreadLocal<Object> getUserIdLocal() {
        return userIdLocal;
    }

    public static ThreadLocal<String> getUserNameLocal() {
        return userNameLocal;
    }

    private static ThreadLocal<String> userNameLocal = new ThreadLocal<>();

    public static void setTraceIdLocal(Integer traceId)
    {
        traceIdLocal.set(traceId);
    }

    public static Integer getTraceId()
    {
        return traceIdLocal.get();
    }

    public static void setUserIdLocal(Object userId)
    {
        userIdLocal.set(userId);
    }

    public static Integer getUserId()
    {
        return traceIdLocal.get();
    }

    public static void setUserNameLocal(String userName)
    {
        userNameLocal.set(userName);
    }

    public static Integer getUserName()
    {
        return traceIdLocal.get();
    }
}
