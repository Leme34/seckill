package com.lee.seckill.bean;

import lombok.Data;

import java.beans.Transient;
import java.util.Date;

/**
 * 秒杀成功明细表
 */
@Data
public class SuccessKilled extends SuccessKilledKey {

    private Byte state;   //状态标识：-1无效 0成功 1已付款
    private Date createTime;

    //Seckill与SuccessKilled是一对多关系
    private Seckill seckill;

}