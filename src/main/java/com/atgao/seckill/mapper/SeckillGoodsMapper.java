package com.atgao.seckill.mapper;

import com.atgao.seckill.pojo.SeckillGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author apple
* @description 针对表【t_seckill_goods(秒杀商品表)】的数据库操作Mapper
* @createDate 2022-03-26 15:27:33
* @Entity com.atgao.seckill.pojo.SeckillGoods
*/
@Mapper
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {

}




