package com.mio.andriodwork.entity.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PingFenRequest {
    private int zhiYuanId;
    private int pingFen;
}
