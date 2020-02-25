package com.example.websocket;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * create by Daniel ON 2020/2/25  14:05
 */
@RestController
public class WebContronller {

    @RequestMapping(value = "/index")
    public void index(){
        MyWebsocket.pushMessage("owner","hello,every one!",null);
    }
}
