package com.atgao.seckill.controller;

import com.atgao.seckill.config.AccessLimit;
import com.atgao.seckill.exception.GlobalException;
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
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author ：atGaoJu
 * @date ：Created in 2022/3/26 5:08 PM
 * @GetHub：https://github.com/atChine
 * @Blog：https://blog.csdn.net/qq_43649569?spm=1000.2115.3001.5343
 */
@Slf4j
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
    private RedisScript<Long> redisScript;


    @RequestMapping("/{path}/doSeckill")
    public RespBean doSeckill(@PathVariable String path, SysUser user, Long goodsId){
        //判断是否登录
        if (user == null){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //校验秒杀地址
        boolean check = orderService.checkPath(user, goodsId, path);
        if (!check){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
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
//        Long stockCount =
//                (Long) redisTemplate.execute(redisScript, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);
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

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return orderId
     */
    @AccessLimit(second = 5,maxCount = 5,needLogin = true)
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(SysUser user, Long goodsId, String captcha, HttpServletRequest request){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        //计数器算法(使用注解)
        //在队列排队中时候，限流，限制访问次数，5秒最多访问5次
//        String uri = request.getRequestURI();
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        Integer count = (Integer) valueOperations.get(uri + ":" + user.getId());
//        if(count == null){
//            valueOperations.set(uri + ":" + user.getId(),1,5, TimeUnit.SECONDS);
//        }else if(count < 5){
//            valueOperations.increment(uri + ":" + user.getId());
//        }else {
//            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REAHCED);
//        }
//        boolean check = orderService.checkCaptcha(user,goodsId,captcha);
//        if (!check){
//            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
//        }
        String str = orderService.createPath(user,goodsId);
        return RespBean.success(str);
    }

    /**
     * 初始化验证码
     * @param user
     * @param goodsId
     * @param response
     */
    @RequestMapping(value = "/captcha",method = RequestMethod.GET)
    @ResponseBody
    public void captcha(SysUser user, Long goodsId, HttpServletResponse response){
        if (user == null || goodsId < 0){
            throw new GlobalException(RespBeanEnum.SESSION_ERROR);
        }
        //设置请求头为输出图片的类型
        response.setContentType("image/jpeg");
        //设置响应头为不缓存图片
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0);
        //生成验证码,放入redis中
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId,captcha.text(),100, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("获取验证码失败",e.getMessage());
        }
    }
}
