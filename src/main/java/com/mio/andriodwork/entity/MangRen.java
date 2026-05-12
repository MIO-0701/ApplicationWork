package com.mio.andriodwork.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MangRen {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String zhangHao;
    private String password;
    private Integer sex;
    private Integer suDu;
    private Integer gongLi;
    private Integer isDel;
    private String createTime;
    private String updataTime;
}
