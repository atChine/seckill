package com.atgao.seckill.service.impl;

import com.atgao.seckill.exception.GlobalException;
import com.atgao.seckill.pojo.SeckillGoods;
import com.atgao.seckill.pojo.SeckillOrder;
import com.atgao.seckill.pojo.SysUser;
import com.atgao.seckill.service.GoodsService;
import com.atgao.seckill.service.SeckillGoodsService;
import com.atgao.seckill.service.SeckillOrderService;
import com.atgao.seckill.utils.MD5Util;
import com.atgao.seckill.utils.UUIDUtil;
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
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //查出秒杀商品表
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new LambdaQueryWrapper<SeckillGoods>()
                .eq(SeckillGoods::getGoodsId, goods.getId()));
        //更新库存信息
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        boolean result = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>()
                .setSql("stock_count = stock_count - 1")
                .eq("goode_id", goods.getId()).gt("stack_count", 0));
        //判断库存是否充足
        if(seckillGoods.getStockCount() < 1){
            valueOperations.set("isStockEmpty",0);
            return null;
        }
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

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public String createPath(SysUser user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        //将秒杀地址存入redis
        redisTemplate.opsForValue().set("seckill:"+user.getId()+":"+goodsId,str,60, TimeUnit.SECONDS);
        return str;
    }

    /**
     * 校验秒杀地址
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    @Override
    public boolean checkPath(SysUser user, Long goodsId, String path) {
        if(path == null || path.equals("") ||user == null || goodsId == null){
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("seckill:"+user.getId()+":"+goodsId);
        return path.equals(redisPath);
    }
}




