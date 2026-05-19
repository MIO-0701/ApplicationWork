package com.mio.andriodwork.service;

import com.mio.andriodwork.entity.request.PiPeiRequest;
import com.mio.andriodwork.entity.response.PiPeiResponse;
import com.mio.andriodwork.mapper.MangRenMapper;
import com.mio.andriodwork.mapper.ZhiYuanZheMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class PiPeiService {
    private HashMap<Integer,PiPeiResponse> mangRenMap = new HashMap<>();
    private HashMap<Integer,PiPeiResponse> zhiYuanMap = new HashMap<>();
    @Autowired
    MangRenMapper mangRenMapper;
    @Autowired
    ZhiYuanZheMapper zhiYuanZheMapper;
    public PiPeiResponse piPei(PiPeiRequest request) {
        if(request== null){
            return null;
        }
        if(request.getUsertype()==0){
            if(!mangRenMap.containsKey(request.getUserId())){
                mangRenMap.put(request.getUserId(),new PiPeiResponse(request.getUserId(),request.getUsertype(),request.getDiDian(),mangRenMapper.selectById(request.getUserId())));
                return null;
            }
            for(PiPeiResponse response:zhiYuanMap.values()){
                if(response.getDiDian().equals(request.getDiDian())){
                    zhiYuanMap.remove(response.getUserId());
                    return response;
                }
            }
        }
        else if(request.getUsertype()==1){
            if(!zhiYuanMap.containsKey(request.getUserId())) {
                zhiYuanMap.put(request.getUserId(), new PiPeiResponse(request.getUserId(), request.getUsertype(), request.getDiDian(), zhiYuanZheMapper.selectById(request.getUserId())));
                return null;
            }
            for(PiPeiResponse response:mangRenMap.values()){
                if(response.getDiDian().equals(request.getDiDian())){
                    mangRenMap.remove(response.getUserId());
                    return response;
                }
            }
        }
        return null;
    }

    public Boolean delPiPei(PiPeiRequest request) {
        if(request== null){
            return null;
        }
        if(request.getUsertype()==0){
            mangRenMap.remove(request.getUserId());
            return true;
        }
        else if(request.getUsertype()==1){
            zhiYuanMap.remove(request.getUserId());
            return true;
        }
        return false;
    }
}
