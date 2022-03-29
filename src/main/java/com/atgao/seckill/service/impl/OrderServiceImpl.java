package com.atgao.seckill.service.impl;

import com.atgao.seckill.exception.GlobalException;
import com.atgao.seckill.pojo.SeckillGoods;
import com.atgao.seckill.pojo.SeckillOrder;
import com.atgao.seckill.pojo.SysUser;
import com.atgao.seckill.service.GoodsService;
import com.atgao.seckill.service.SeckillGoodsService;
import com.atgao.seckill.service.SeckillOrderService;
import com.atgao.seckill.vo.GoodsVo;
import com.atgao.seckill.vo.OrderDeatilVo;
import com.atgao.seckill.vo.RespBeanEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atgao.seckill.pojo.Order;
import com.atgao.seckill.service.OrderService;
import com.atgao.seckill.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
* @author apple
* @description 针对表【t_order】的数据库操作Service实现
* @createDate 2022-03-26 15:27:28
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService{
    @Autowired
    private SeckillGoodsService seckillGoodsService;
    private SeckillOrderService seckillOrderService;
    private OrderMapper orderMapper;
    private GoodsService goodsService;
    private RedisTemplate redisTemplate;
    /**
     * 秒杀具体实现
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    @Override
    public Order seckill(SysUser user, GoodsVo goods) {
        //查出秒杀商品表
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new LambdaQueryWrapper<SeckillGoods>()
                .eq(SeckillGoods::getGoodsId, goods.getId()));
        //更新库存信息
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        boolean seckillGoodsResult = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().set("stock_count"
                , seckillGoods.getStockCount()).eq("id", seckillGoods.getId()).eq("stock_count", 0));
        if(!seckillGoodsResult){
            return null;
        }
        //生成订单,
        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
       // seckillOrderService.save(SeckillOrder);
        redisTemplate.opsForValue().set("order:"+user.getId()+":"+goods.getId(),seckillOrder);
        return order;
    }

    /**
     * 查询细节
     * @param orderId
     * @return
     */
    @Override
    public OrderDeatilVo detail(Long orderId) {
        if(orderId == null){
            throw new  GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDeatilVo detail = new OrderDeatilVo();
        detail.setOrder(order);
        detail.setGoodsVo(goodsVo);
        return detail;
    }
}




