package com.mio.andriodwork.server;

import com.mio.andriodwork.mapper.ZhiYuanZheMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ZhiYuanZheServer {
    @Autowired
    ZhiYuanZheMapper zhiYuanZheMapper;
}
