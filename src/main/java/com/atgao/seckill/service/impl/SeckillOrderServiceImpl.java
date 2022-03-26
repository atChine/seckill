package com.atgao.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atgao.seckill.pojo.SeckillOrder;
import com.atgao.seckill.service.SeckillOrderService;
import com.atgao.seckill.mapper.SeckillOrderMapper;
import org.springframework.stereotype.Service;

/**
* @author apple
* @description 针对表【t_seckill_order(秒杀订单表)】的数据库操作Service实现
* @createDate 2022-03-26 15:27:37
*/
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder>
    implements SeckillOrderService{

}




