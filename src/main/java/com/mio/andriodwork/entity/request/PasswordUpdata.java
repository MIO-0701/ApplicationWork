package com.mio.andriodwork.entity.request;

import lombok.Data;

@Data
public class PasswordUpdata {
    int id;// 用户id
    String password; // 新密码
}
