package com.atgao.seckill.service.impl;

import com.atgao.seckill.mapper.GoodsMapper;
import com.atgao.seckill.pojo.Goods;
import com.atgao.seckill.service.GoodsService;
import com.atgao.seckill.vo.GoodsVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

	@Autowired
	private GoodsMapper goodsMapper;

	/**
	 * 功能描述: 获取商品列表
	 */
	@Override
	public List<GoodsVo> findGoodsVo() {
		return goodsMapper.findGoodsVo();
	}



	/**
	 * 功能描述: 获取商品详情
	 */
	@Override
	public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
		return goodsMapper.findGoodsVoByGoodsId(goodsId);
	}
}
