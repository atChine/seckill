package com.atgao.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("demo")
public class TestController {
    /**
     * 测试页面跳转
     * @param model
     * @return
     */
    @RequestMapping("hello")
    public String hello(Model model){
        model.addAttribute("name","xxxx");
        return "hello";
    }
}
