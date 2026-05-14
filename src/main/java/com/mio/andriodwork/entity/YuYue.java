package com.mio.andriodwork.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class YuYue {
    @TableId(type = IdType.AUTO)
    private int id;
    private int mangRenId;
    private int zhiYuanId;
    private String diDian;
    private int isDel;
    private Date createTime;
}
