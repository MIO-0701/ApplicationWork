package com.mio.andriodwork.entity.response;

import lombok.Data;

import java.util.Date;

@Data
public class YuYueResponse {
    private int yuYueID;
    private int zhiYuanId;
    private int mangRenId;
    private String diDian;
    private Date createTime;
}
