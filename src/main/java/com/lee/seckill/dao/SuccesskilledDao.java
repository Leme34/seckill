package com.lee.seckill.dao;

import com.lee.seckill.bean.SuccessKilled;
import org.apache.ibatis.annotations.Param;

public interface SuccesskilledDao {

    /**
     * 插入秒杀成功明细，数据库端使用主键(seckillId,userPhone)去重
     * @return 受影响行数,若成功返回 >0 的值
     */
    int insertSuccessKilled(@Param("seckillId") Long seckillId,@Param("userPhone") Long userPhone);

    /**
     * 根据主键查询秒杀成功明细，并关联查询出秒杀商品信息seckill
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId")Long seckillId,@Param("userPhone")Long userPhone);


}
