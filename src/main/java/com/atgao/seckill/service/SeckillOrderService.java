package com.atgao.seckill.service;

import com.atgao.seckill.pojo.SeckillOrder;
import com.atgao.seckill.pojo.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author apple
* @description 针对表【t_seckill_order(秒杀订单表)】的数据库操作Service
* @createDate 2022-03-26 15:27:37
*/
public interface SeckillOrderService extends IService<SeckillOrder> {
    /**
     * 根据用户id和商品id查询订单，获取秒杀结果
     * @param user
     * @param goodsId
     * @return
     */
    Long getResult(SysUser user, Long goodsId);
}
