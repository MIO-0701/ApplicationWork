package com.mio.andriodwork.until;

import com.mio.andriodwork.entity.MangRen;
import com.mio.andriodwork.entity.request.MangRenUpdataRequest;
import com.mio.andriodwork.entity.response.MangRenResponse;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;

public class BeanUntil {
    public static MangRenResponse getMangRenResponse(MangRen mangRen){
        MangRenResponse mangRenResponse = new MangRenResponse();
        BeanUtils.copyProperties(mangRen,mangRenResponse);
        return mangRenResponse;
    }

    public static Map<String,Object> getMangRenMap(MangRen mangRen){
        Map<String,Object> map = new HashMap<>();
        map.put("id",mangRen.getId());
        return map;
    }

    public static MangRen getMangRen(MangRenUpdataRequest mangRenRequest){
        MangRen mangRen = new MangRen();
        BeanUtils.copyProperties(mangRenRequest,mangRen);
        return mangRen;
    }
}
