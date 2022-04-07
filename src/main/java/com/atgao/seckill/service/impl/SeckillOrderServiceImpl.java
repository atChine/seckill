package com.atgao.seckill.service.impl;

import com.atgao.seckill.pojo.SysUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atgao.seckill.pojo.SeckillOrder;
import com.atgao.seckill.service.SeckillOrderService;
import com.atgao.seckill.mapper.SeckillOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
* @author apple
* @description 针对表【t_seckill_order(秒杀订单表)】的数据库操作Service实现
* @createDate 2022-03-26 15:27:37
*/
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder>
    implements SeckillOrderService{
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    private RedisTemplate redisTemplate;

    /**
     * 根据用户id和商品id查询订单，获取秒杀结果
     * 1：成功
     * 0：排队中
     * -1：秒杀结束
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public Long getResult(SysUser user, Long goodsId) {
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new LambdaQueryWrapper<SeckillOrder>()
                .eq(SeckillOrder::getUserId, user.getId())
                .eq(SeckillOrder::getGoodsId, goodsId));
        if (seckillOrder != null) {
            return seckillOrder.getOrderId();
        }else if (redisTemplate.hasKey("isStockEmpty:" + goodsId)) {
            return -1L;
        }else {
            return 0L;
        }
    }
}




