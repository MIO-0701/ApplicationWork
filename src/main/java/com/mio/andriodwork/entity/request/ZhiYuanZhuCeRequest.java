package com.mio.andriodwork.entity.request;

import lombok.Data;

@Data
public class ZhiYuanZhuCeRequest {
    private Integer id;
    private String name;
    private String zhangHao;
    private String password;
    private Integer sex;
    private Integer suDu;
    private Integer gongLi;
    private String zaiXian;//平时在线时间XXDD  XX-DD
    private String renZhen;
}
