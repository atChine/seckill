package com.atgao.seckill.service;

import com.atgao.seckill.pojo.Order;
import com.atgao.seckill.pojo.SysUser;
import com.atgao.seckill.vo.GoodsVo;
import com.atgao.seckill.vo.OrderDeatilVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author apple
* @description 针对表【t_order】的数据库操作Service
* @createDate 2022-03-26 15:27:28
*/
public interface OrderService extends IService<Order> {

    Order seckill(SysUser user, GoodsVo goods);

    OrderDeatilVo detail(Long orderId);

    String createPath(SysUser user, Long goodsId);

    boolean checkPath(SysUser user, Long goodsId, String path);

    /**
     * 校验验证码
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    boolean checkCaptcha(SysUser user, Long goodsId, String captcha);
}
