package com.atgao.seckill.service;

import com.atgao.seckill.pojo.SysUser;
import com.atgao.seckill.vo.LoginVo;
import com.atgao.seckill.vo.RespBean;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author apple
* @description 针对表【t_user(用户表)】的数据库操作Service
* @createDate 2022-03-24 10:59:24
*/
public interface SysUserService extends IService<SysUser> {
    /**
     * 登录功能
     * @param loginVo
     * @return
     */
    RespBean doLogin(LoginVo loginVo);
}
