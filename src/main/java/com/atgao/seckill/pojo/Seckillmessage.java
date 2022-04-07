package com.atgao.seckill.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seckillmessage {
    private SysUser user;
    private Long goodsId;
}
