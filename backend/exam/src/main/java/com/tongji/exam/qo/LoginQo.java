package com.tongji.exam.qo;

import com.google.gson.annotations.JsonAdapter;
import com.tongji.exam.utils.Audit.EncryptSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginQo {
    //1表示用户名，2表示邮箱
    private Integer loginType;
    //用户名邮箱的字符串
    private String userInfo;
    //用户密码
    @JsonAdapter(EncryptSerializer.class)
    private String password;
}
