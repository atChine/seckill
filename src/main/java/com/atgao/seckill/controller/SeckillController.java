package com.atgao.seckill.controller;

import com.atgao.seckill.pojo.Order;
import com.atgao.seckill.pojo.SeckillOrder;
import com.atgao.seckill.pojo.SysUser;
import com.atgao.seckill.service.GoodsService;
import com.atgao.seckill.service.OrderService;
import com.atgao.seckill.service.SeckillOrderService;
import com.atgao.seckill.vo.GoodsVo;
import com.atgao.seckill.vo.RespBean;
import com.atgao.seckill.vo.RespBeanEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ：atGaoJu
 * @date ：Created in 2022/3/26 5:08 PM
 * @GetHub：https://github.com/atChine
 * @Blog：https://blog.csdn.net/qq_43649569?spm=1000.2115.3001.5343
 */
@Controller
@RequestMapping("seckill")
public class SeckillController {
    @Autowired
    private GoodsService goodsService;
    private SeckillOrderService seckillOrderService;
    private OrderService orderService;
    private RedisTemplate redisTemplate;

    @RequestMapping("/doSeckill")
    public RespBean doSeckill(Model model, SysUser user, Long goodsId){
        //判断是否登录
        if (user == null){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        model.addAttribute("user",user);
        //判断库存
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        if(goods.getStockCount() < 1){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //判断是否重复抢购
//        SeckillOrder seckillOrder = seckillOrderService.getOne(new LambdaQueryWrapper<SeckillOrder>()
//                .eq(SeckillOrder::getUserId, user.getId())
//                .eq(SeckillOrder::getGoodsId, goodsId));
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goods.getId());
        if (seckillOrder != null){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        Order order = orderService.seckill(user,goods);
        model.addAttribute("order",order);
        model.addAttribute("goods",goods);
        return RespBean.success(order);
    }


















}
