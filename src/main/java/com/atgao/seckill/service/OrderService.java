package com.atgao.seckill.service;

import com.atgao.seckill.pojo.Order;
import com.atgao.seckill.pojo.SysUser;
import com.atgao.seckill.vo.GoodsVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author apple
* @description 针对表【t_order】的数据库操作Service
* @createDate 2022-03-26 15:27:28
*/
public interface OrderService extends IService<Order> {

    Order seckill(SysUser user, GoodsVo goods);
}
