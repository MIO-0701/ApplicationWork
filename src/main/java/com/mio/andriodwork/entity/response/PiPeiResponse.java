package com.mio.andriodwork.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PiPeiResponse {
    private int userId;
    private int usertype;
    private String diDian;
    private Object data;
}
