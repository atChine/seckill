package com.atgao.seckill.rabbitmq;

import com.atgao.seckill.pojo.Seckillmessage;
import com.atgao.seckill.pojo.SysUser;
import com.atgao.seckill.service.GoodsService;
import com.atgao.seckill.service.OrderService;
import com.atgao.seckill.utils.JsonUtil;
import com.atgao.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author ：atGaoJu
 * @date ：Created in 2022/3/28 12:08 PM
 * @GetHub：https://github.com/atChine
 * @Blog：https://blog.csdn.net/qq_43649569?spm=1000.2115.3001.5343
 */
@Service
@Slf4j
public class MQreceiver {

//    @RabbitListener(queues = "queue")
//    public void receive(Object msg){
//        log.info("接收到消息："+ msg);
//    }
    /**
     * 下单操作
     */
    @Autowired
    private GoodsService goodsService;
    private RedisTemplate redisTemplate;
    private OrderService orderService;
    @RabbitListener(queues = "seckillQueue")
    public void receive(String message){
        log.info("接收到的消息："+message);
        Seckillmessage seckillmessage = JsonUtil.jsonStr2Object(message, Seckillmessage.class);
        SysUser user = seckillmessage.getUser();
        Long goodsId = seckillmessage.getGoodsId();
        //查询库存
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if (goodsVo.getStockCount() <= 0){
            log.info("商品已经被抢完");
            return;
        }
        //判断是否重复秒杀
        Seckillmessage seckillmessage1 =
                (Seckillmessage) redisTemplate.opsForValue().get("seckillmessage"+user.getId()+":"+goodsId);
        if (seckillmessage1 != null){
            log.info("重复秒杀");
            return;
        }
        //执行秒杀操作
        orderService.seckill(user,goodsVo);
    }
}
