package com.atgao.seckill.controller;

import com.atgao.seckill.pojo.Order;
import com.atgao.seckill.pojo.SeckillOrder;
import com.atgao.seckill.pojo.Seckillmessage;
import com.atgao.seckill.pojo.SysUser;
import com.atgao.seckill.rabbitmq.MQsender;
import com.atgao.seckill.service.GoodsService;
import com.atgao.seckill.service.OrderService;
import com.atgao.seckill.service.SeckillOrderService;
import com.atgao.seckill.utils.JsonUtil;
import com.atgao.seckill.vo.GoodsVo;
import com.atgao.seckill.vo.RespBean;
import com.atgao.seckill.vo.RespBeanEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;

/**
 * @author ：atGaoJu
 * @date ：Created in 2022/3/26 5:08 PM
 * @GetHub：https://github.com/atChine
 * @Blog：https://blog.csdn.net/qq_43649569?spm=1000.2115.3001.5343
 */
@Controller
@RequestMapping("seckill")
public class SeckillController implements InitializingBean {
    HashMap<Long,Boolean> EmptySeckillMap = new HashMap<>();
    @Autowired
    private GoodsService goodsService;
    private SeckillOrderService seckillOrderService;
    private OrderService orderService;
    private RedisTemplate redisTemplate;
    private MQsender mQsender;
    @RequestMapping("/doSeckill")
    public RespBean doSeckill(Model model, SysUser user, Long goodsId){
        //判断是否登录
        if (user == null){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //判断是否重复秒杀
        SeckillOrder seckillOrder =
                (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //判断是否已经秒杀完毕,通过内存标记，减少redis访问
        if (EmptySeckillMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //预减库存
        Long stockCount = valueOperations.decrement("seckillGoods:" + goodsId);
        if (stockCount < 0){
            EmptySeckillMap.put(goodsId,true);
            valueOperations.increment("seckillGoods:" + goodsId);
            //库存不足
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        Seckillmessage seckillmessage = new Seckillmessage(user, goodsId);
        mQsender.sendSeckillMeassage(JsonUtil.object2JsonStr(seckillmessage));
        return RespBean.success(0);
    }

    /**
     * 初始化秒杀商品,将商品放入redis中
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.findGoodsVo();
        if (goodsVoList != null && goodsVoList.size() > 0){
            for (GoodsVo goodsVo : goodsVoList) {
                redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
                EmptySeckillMap.put(goodsVo.getId(),false);
            }
        }
    }


    /**
     * 获取秒杀结果信息
     * @param user
     * @param goodsId
     * @return orderId
     */
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(SysUser user, Long goodsId){
        if(user == null){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
    }
}
