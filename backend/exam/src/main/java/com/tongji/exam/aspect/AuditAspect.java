package com.tongji.exam.aspect;


import com.tongji.exam.annotation.MyLog;
import com.tongji.exam.entity.Record;
import com.tongji.exam.repository.RecordRepository;
import com.tongji.exam.utils.Audit.AuditUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 created by kz on 
 */
@Aspect
@Component
public class AuditAspect {


    /**
     * 操作数据库
     */
    @Autowired
    private RecordRepository recordRepository;

    //定义切点 @Pointcut
    //在注解的位置切入代码
    @Pointcut("@annotation(com.tongji.exam.annotation.MyLog)")
    public void logPointCut() {
    }

    //切面 配置通知
    @Before("logPointCut()&&@annotation(annotation)")         //AfterReturning
    public void saveOperation(JoinPoint joinPoint, MyLog annotation) {
        if("off".equals(annotation.value()))
        {
            return;
        }
        //用于保存日志
        Record record = new Record();
        Integer traceId = AuditUtil.getTraceId();
        if(traceId==null)
        {
            record.setTraceId(0);
        }
        else
        {
            record.setTraceId(AuditUtil.getTraceId());
        }
        record.setCaller(AuditUtil.getUserName());
        recordRepository.save(record);
    }

}
