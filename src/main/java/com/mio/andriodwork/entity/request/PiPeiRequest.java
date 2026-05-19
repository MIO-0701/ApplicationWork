package com.mio.andriodwork.entity.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PiPeiRequest {
    private int usertype;
    private int userId;
    private String diDian;
}
