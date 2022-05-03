package com.tongji.exam.aspect;

import cn.hutool.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tongji.exam.annotation.ApiCallMonitor;
import com.tongji.exam.utils.Audit.AuditUtil;
import com.tongji.exam.utils.Audit.CallEvent;
import com.tongji.exam.utils.Audit.JsonUtil;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponseWrapper;
import java.text.Annotation;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * created by kz on
 */
@Aspect
@Component
public class ApiCallAspect {
    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    private static SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //定义切点 @Pointcut
    //在注解的位置切入代码
    @Pointcut("@annotation(com.tongji.exam.annotation.ApiCallMonitor)")
    public void logPointCut() {
    }

    @Around("logPointCut()&&@annotation(annotation)")
    public Object sendCallRecord(ProceedingJoinPoint joinPoint, ApiCallMonitor annotation) throws Throwable {
        CallEvent callEvent = new CallEvent();
        callEvent.setTraceId(AuditUtil.getTraceId());
        callEvent.setType(annotation.value());
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
        try {
            // 下面两个数组中，参数值和参数名的个数和位置是一一对应的。
            Object[] objs = joinPoint.getArgs();
            String[] argNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
            Map<String, Object> paramMap = new HashMap<String, Object>(4);
            for (int i = 0; i < objs.length; i++) {
                if (!(objs[i] instanceof ExtendedServletRequestDataBinder) && !(objs[i] instanceof HttpServletResponseWrapper)
                &&!(objs[i] instanceof HttpServletRequest)) {
                    paramMap.put(argNames[i], objs[i]);
                }
            }
            if (paramMap.size() > 0) {
                callEvent.setParam(JsonUtil.toJsonString(paramMap));
                System.out.println("param: "+callEvent.getParam());
            }
        } catch (Exception e) {
            System.out.println("AOP methodBefore:error");
        }
        callEvent.setIp(httpServletRequest.getRemoteAddr());
        callEvent.setMethod(joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName());
        callEvent.setReqTime(sdf.format(System.currentTimeMillis()));
        callEvent.setUri(httpServletRequest.getRequestURI());

        Object res = joinPoint.proceed();
        callEvent.setRespTime(sdf.format(System.currentTimeMillis()));
        callEvent.setResult(JsonUtil.toJsonString(res));

        kafkaTemplate.send("api-call", JsonUtil.toJsonString(callEvent));
        AuditUtil.getTraceIdLocal().remove();
        AuditUtil.getUserIdLocal().remove();
        AuditUtil.getUserNameLocal().remove();
        return res;
    }
}
