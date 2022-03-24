package com.atgao.seckill.controller;

import com.atgao.seckill.pojo.SysUser;
import com.atgao.seckill.service.SysUserService;
import com.atgao.seckill.vo.LoginVo;
import com.atgao.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author ：atGaoJu
 * @date ：Created in 2022/3/24 3:56 PM
 * @GetHub：https://github.com/atChine
 * @Blog：https://blog.csdn.net/qq_43649569?spm=1000.2115.3001.5343
 */
@Controller
@RequestMapping("login")
@Slf4j
public class LoginController {

    @Autowired
    private SysUserService userService;

    @RequestMapping("toLogin")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("doLogin")
    @ResponseBody
    public RespBean doLogin(LoginVo loginVo){
        log.info("{}",loginVo);
        return userService.doLogin(loginVo);
    }
}
