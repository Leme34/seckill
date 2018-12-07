package com.lee.seckill.exception;

/**
 * 重复秒杀异常
 */
public class RepeatKillException extends SeckillException{


    public RepeatKillException(String message) {
        super(message);
    }
}
