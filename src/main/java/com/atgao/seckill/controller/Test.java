package com.atgao.seckill.controller;

import com.atgao.seckill.rabbitmq.MQsender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author ：atGaoJu
 * @date ：Created in 2022/3/28 12:11 PM
 * @GetHub：https://github.com/atChine
 * @Blog：https://blog.csdn.net/qq_43649569?spm=1000.2115.3001.5343
 */
@Controller
@RequestMapping("test")
public class Test {

    @Autowired
    private MQsender mqsender;

    @RequestMapping("hello")
    @ResponseBody
    public void mq(){
        mqsender.send("hello");
    }
}
