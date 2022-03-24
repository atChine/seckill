package com.atgao.seckill.service.impl;

import com.atgao.seckill.utils.MD5Util;
import com.atgao.seckill.vo.LoginVo;
import com.atgao.seckill.vo.RespBean;
import com.atgao.seckill.vo.RespBeanEnum;
import com.atgao.seckill.vo.ValidatorUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atgao.seckill.pojo.SysUser;
import com.atgao.seckill.service.SysUserService;
import com.atgao.seckill.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
* @author apple
* @description 针对表【t_user(用户表)】的数据库操作Service实现
* @createDate 2022-03-24 10:59:24
*/
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
    implements SysUserService{
    @Autowired
    private SysUserMapper userMapper;
    @Override
    public RespBean doLogin(LoginVo loginVo) {
            String mobile = loginVo.getMobile();
            String password = loginVo.getPassword();
            if (StringUtils.isEmpty(mobile)||StringUtils.isEmpty(password)){
                return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            }
            if (!ValidatorUtil.isMobile(mobile)){
                return RespBean.error(RespBeanEnum.MOBILE_ERROR);
            }
//根据手机号获取用户
            SysUser user = userMapper.selectById(mobile); if (null==user){
                return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            }
//校验密
            if(!MD5Util.formPassToDBPass(password,user.getSalt()).equals(user.getPassword())){
                return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            }
            return RespBean.success();
        }

    }




