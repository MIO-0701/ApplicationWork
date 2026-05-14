package com.mio.andriodwork.entity.response;

import lombok.Data;

@Data
public class ZhiYuanResponse {
    private int id;
    private String name;
    private int sex;
    private int pingFen;
    private int suDu;
    private int gongLi;
    private String createTime;
    private String zaiXian;
}
