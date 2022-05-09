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

    private String description;

    private Integer limit;

    private String type;

    private Integer expire;

    private String uri;

    private String ip;

    private String method;

    private String reqTime;

    private String respTime;

    private Object param;

    private Object result;

}
