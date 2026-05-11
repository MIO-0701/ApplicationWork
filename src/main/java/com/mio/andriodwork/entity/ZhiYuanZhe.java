package com.mio.andriodwork.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ZhiYuanZhe {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String name;
    private String zhangHao;
    private String password;
    private Integer sex;
    private Integer suDu;
    private Integer gongLi;
    private String zaiXian;
    private Integer pingFen;
    private Integer isRenZhen;
    private Integer isDel;
    private String createTime;
    private String updataTime;
}
