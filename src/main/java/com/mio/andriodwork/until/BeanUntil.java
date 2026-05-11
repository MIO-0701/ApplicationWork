package com.mio.andriodwork.until;

import com.mio.andriodwork.entity.MangRen;
import com.mio.andriodwork.entity.response.MangRenResponse;
import org.springframework.beans.BeanUtils;

public class BeanUntil {
    public static MangRenResponse getMangRenResponse(MangRen mangRen){
        MangRenResponse mangRenResponse = new MangRenResponse();
        BeanUtils.copyProperties(mangRen,mangRenResponse);
        return mangRenResponse;
    }
}
