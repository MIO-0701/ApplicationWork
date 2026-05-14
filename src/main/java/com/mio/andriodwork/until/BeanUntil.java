package com.mio.andriodwork.until;

import com.mio.andriodwork.entity.MangRen;
import com.mio.andriodwork.entity.YuYue;
import com.mio.andriodwork.entity.ZhiYuan;
import com.mio.andriodwork.entity.request.MangRenUpdataRequest;
import com.mio.andriodwork.entity.request.YuYueRequest;
import com.mio.andriodwork.entity.response.MangRenResponse;
import com.mio.andriodwork.entity.response.YuYueResponse;
import com.mio.andriodwork.entity.response.ZhiYuanResponse;
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

    public static YuYue getYuYue(YuYueRequest yuYueRequest){
        YuYue yuYue = new YuYue();
        BeanUtils.copyProperties(yuYueRequest,yuYue);
        return yuYue;
    }

    public static YuYueResponse getYuYueResponse(YuYue yuYue) {
        YuYueResponse yuYueResponse = new YuYueResponse();
        yuYueResponse.setYuYueID(yuYue.getId());
        yuYueResponse.setZhiYuanId(yuYue.getZhiYuanId());
        yuYueResponse.setDiDian(yuYue.getDiDian());
        yuYueResponse.setCreateTime(yuYue.getCreateTime());
        return yuYueResponse;
    }

    public static ZhiYuanResponse getZhiYuanResponse(ZhiYuan zhiYuan) {
        ZhiYuanResponse zhiYuanResponse = new ZhiYuanResponse();
        BeanUtils.copyProperties(zhiYuan,zhiYuanResponse);
        return zhiYuanResponse;
    }

    public static Map<String,Object> getZhiYuanMap(ZhiYuan zhiYuan){
        Map<String,Object> map = new HashMap<>();
        map.put("id",zhiYuan.getId());
        return map;
    }
}
