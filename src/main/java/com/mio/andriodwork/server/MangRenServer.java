package com.mio.andriodwork.server;

import com.mio.andriodwork.mapper.MangRenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MangRenServer {
    @Autowired
    MangRenMapper mangRenMapper;
}
