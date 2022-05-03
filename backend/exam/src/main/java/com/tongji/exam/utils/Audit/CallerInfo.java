package com.tongji.exam.utils.Audit;

import lombok.Data;

/**
 * created by kz on
 */

@Data
public class CallerInfo {

    Integer TraceId;
    Object userId;
    String userName;
}
