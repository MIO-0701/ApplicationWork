package com.mio.andriodwork.entity.request;

import lombok.Data;

@Data
public class MangRenUpdataRequest {
    private int id;
    private String name;
    private int sex;
    private int suDu;
    private int gongLi;
}
