package com.lee.seckill.service;

import com.lee.seckill.bean.Seckill;
import com.lee.seckill.dto.Exposer;
import com.lee.seckill.dto.SeckillExecution;
import com.lee.seckill.exception.RepeatKillException;
import com.lee.seckill.exception.SeckillCloseException;
import com.lee.seckill.exception.SeckillException;

import java.util.List;

/**
 * 秒杀业务逻辑接口,需要站在接口使用方角度设计
 */
public interface SeckillService {

    /**
     * 查询所有秒杀商品
     */
    List<Seckill> getAllsecSeckills();

    /**
     * 查询单个秒杀商品
     */
    Seckill getById(Long seckillId);

    /**
     * 秒杀进行时输出秒杀接口地址
     * 未开启则输出系统时间和秒杀开启时间
     * 会生成加盐MD5标识此用户
     */
    Exposer exportSeckillUrl(Long seckillId);

    /**
     * 执行秒杀操作,校验MD5,若被篡改则秒杀失败
     * 抛出的不同异常使spring声明式事务回滚 并 使Controller层返回不同错误结果
     */
    SeckillExecution executeSeckill(Long seckillId, Long userPhone, String md5)
            throws SeckillCloseException, RepeatKillException, SeckillException;


    /**
     * 使用mysql存储过程执行秒杀操作
     */
    SeckillExecution executeSeckillProcedure(Long seckillId, Long userPhone, String md5);

}
