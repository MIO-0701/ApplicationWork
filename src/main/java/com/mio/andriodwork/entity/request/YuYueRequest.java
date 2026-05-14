package com.mio.andriodwork.entity.request;

import lombok.Data;

import java.util.Date;

@Data
public class YuYueRequest {
    private int id;
    private String diDian;
    private Date createTime;//yyyy-mm-dd hh:mm:ss
}
