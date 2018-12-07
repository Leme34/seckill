package com.lee.seckill.dto;

import com.lee.seckill.bean.SuccessKilled;
import com.lee.seckill.enums.SeckillStateEnum;
import lombok.Data;

/**
 * 秒杀执行后的结果DTO
 */
@Data
public class SeckillExecution {

    private Long seckillId; //秒杀商品id

    //为简化枚举转json操作直接引入SeckillStateEnum对象属性
    private Integer state; //秒杀结果状态
    private String stateInfo;  //秒杀结果状态说明

    private SuccessKilled successkilled; //秒杀成功对象,失败则为null

    /**
     * 秒杀成功时的构造函数
     */
    public SeckillExecution(Long seckillId, SeckillStateEnum stateEnum, SuccessKilled successkilled) {
        this.seckillId = seckillId;
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.successkilled = successkilled;
    }

    /**
     * 秒杀失败时的构造函数
     */
    public SeckillExecution(Long seckillId, SeckillStateEnum stateEnum) {
        this.seckillId = seckillId;
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }
}
