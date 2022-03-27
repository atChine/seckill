package com.atgao.seckill.controller;

import com.atgao.seckill.pojo.SysUser;
import com.atgao.seckill.service.OrderService;
import com.atgao.seckill.vo.OrderDeatilVo;
import com.atgao.seckill.vo.RespBean;
import com.atgao.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前端控制器
 * @since 2022-03-03
 */
@RestController
@RequestMapping("/order")
//@Api(value = "订单", tags = "订单")
public class TOrderController {

    @Autowired
    private OrderService orderService;


    //@ApiOperation("订单")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public RespBean detail(SysUser user, Long orderId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDeatilVo orderDeatilVo = orderService.detail(orderId);
        return RespBean.success(orderDeatilVo);
    }
}
