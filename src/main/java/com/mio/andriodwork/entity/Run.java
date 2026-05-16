package com.mio.andriodwork.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class Run {
    @TableId(type = IdType.AUTO)
    private int id;
    private int userType;// 0为盲人，1为志愿者
    private int userId;
    private int suDu;//跑步平均速度
    private int shiChang;//此次跑步时长
    private int juLi;//此次跑步公里数
    private int isDel;
    private Date createTime;

}
