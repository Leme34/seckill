package com.lee.seckill.dto;

import lombok.Data;

/**
 * 封装ajax请求返回的json结果
 */
@Data
public class SeckillResult<T> {

    private boolean success;  //请求是否成功,若cookie中没有killPhone则失败
    private T data;
    private String error;

    //成功的构造方法
    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    //失败的构造方法
    public SeckillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

}
