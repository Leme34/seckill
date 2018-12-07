package com.lee.seckill.bean;

import lombok.Data;

/**
 * 秒杀成功明细表主键类
 */
@Data
public class SuccessKilledKey {
    private Long seckillId;  //秒杀商品id
    private Long userPhone;  //用户手机号

}