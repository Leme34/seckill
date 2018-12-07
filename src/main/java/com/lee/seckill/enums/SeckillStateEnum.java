package com.lee.seckill.enums;

import lombok.Getter;

/**
 * 秒杀结果状态枚举类
 */
@Getter
public enum SeckillStateEnum {

    SUCCESS(1, "秒杀成功"),
    END(0, "秒杀结束"),
    REPEAT_KILL(-1, "您已经抢购过了哦~"),
    INNER_ERROR(-2, "系统内部异常"),
    DATA_REWROTE(-3, "请求数据被篡改"),;

    private int state;
    private String stateInfo;

    SeckillStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    /**
     * @return 根据传入的state属性返回对应的枚举对象
     */
    public static SeckillStateEnum stateOf(int state) {
        for (SeckillStateEnum stateEnum : values()) {
            if (stateEnum.getState() == state) {
                return stateEnum;
            }
        }
        return  null;
    }

}
