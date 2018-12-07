package com.lee.seckill.exception;

/**
 * 秒杀已关闭异常
 */
public class SeckillCloseException extends SeckillException{
    public SeckillCloseException(String message) {
        super(message);
    }
}
