package com.atgao.seckill.mapper;

import com.atgao.seckill.pojo.SeckillOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author apple
* @description 针对表【t_seckill_order(秒杀订单表)】的数据库操作Mapper
* @createDate 2022-03-26 15:27:37
* @Entity com.atgao.seckill.pojo.SeckillOrder
*/
@Mapper
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {

}




