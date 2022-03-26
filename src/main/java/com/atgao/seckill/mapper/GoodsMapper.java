package com.atgao.seckill.mapper;

import com.atgao.seckill.pojo.Goods;
import com.atgao.seckill.vo.GoodsVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author apple
* @description 针对表【t_goods(商品表)】的数据库操作Mapper
* @createDate 2022-03-26 15:27:18
* @Entity com.atgao.seckill.pojo.Goods
*/
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}




