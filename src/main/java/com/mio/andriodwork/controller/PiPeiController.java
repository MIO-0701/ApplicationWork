package com.mio.andriodwork.controller;

import com.mio.andriodwork.entity.request.PiPeiRequest;
import com.mio.andriodwork.entity.response.PiPeiResponse;
import com.mio.andriodwork.service.PiPeiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/piPei")
public class PiPeiController  {
    @Autowired
    PiPeiService piPeiService;

    @RequestMapping("/piPei")
    public PiPeiResponse piPei(PiPeiRequest request){
        return piPeiService.piPei(request);
    }

    @RequestMapping("/delPiPei")
    public Boolean delPiPei(PiPeiRequest request){
        return piPeiService.delPiPei(request);
    }
}
