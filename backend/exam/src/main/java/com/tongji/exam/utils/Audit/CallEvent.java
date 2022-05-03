package com.tongji.exam.utils.Audit;

import lombok.Data;

import java.util.Date;

/**
 * created by kz on
 * @author
 */
@Data
public class CallEvent {

    private Integer traceId;

    private Object userId;

    private String userName;

    private String type;

    private String uri;

    private String ip;

    private String method;

    private String reqTime;

    private String respTime;

    private String param;

    private Object result;

}
