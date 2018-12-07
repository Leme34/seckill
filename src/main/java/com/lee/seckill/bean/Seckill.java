package com.lee.seckill.bean;

import lombok.Data;
import java.util.Date;

/**
 * 秒杀库存表
 */
@Data
public class Seckill {
    private Long seckillId;  //商品库存id

    private String name;  //商品名称

    private Integer number;  //库存数量

    private Date startTime;  //秒杀开始时间

    private Date endTime;  //秒杀结束时间

    private Date createTime;

}