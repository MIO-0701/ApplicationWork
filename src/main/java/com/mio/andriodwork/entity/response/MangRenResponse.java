package com.mio.andriodwork.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MangRenResponse {

    private int id;
    private String name;
    private int sex;
    private int suDu;
    private int gongLi;
    private String createTime;
}
