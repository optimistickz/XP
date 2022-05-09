package com.tongji.exam.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.tongji.exam.annotation.ExcludeField;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
@DynamicUpdate
public class User {
    @Id
    private String userId;
    private String userUsername;
    private String userNickname;
    private String userPassword;
    private Integer userRoleId;
    @ExcludeField
    private String userAvatar;
    private String userDescription;
    private String userEmail;
    private String userPhone;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
