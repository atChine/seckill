package com.atgao.seckill.service;

import com.atgao.seckill.pojo.Goods;
import com.atgao.seckill.vo.GoodsVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author apple
* @description 针对表【t_goods(商品表)】的数据库操作Service
* @createDate 2022-03-26 15:27:18
*/
public interface GoodsService extends IService<Goods> {

    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
