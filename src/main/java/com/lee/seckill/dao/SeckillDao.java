package com.lee.seckill.dao;

import com.lee.seckill.bean.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SeckillDao {

    /**
     * 减库存
     *
     * @param seckillId
     * @param killTime  用户秒杀时间,必须在(秒杀开始时间,秒杀结束时间)之间
     * @return 受影响行数,若成功返回 >0 的值
     */
    int reduceNumber(@Param("seckillId") Long seckillId,@Param("killTime")  Date killTime);

    /**
     * 根据id查询秒杀对象
     */
    Seckill queryById(Long seckillId);


    /**
     * 根据偏移量查询秒杀商品列表
     */
    List<Seckill> queryAll(@Param("offset") Integer offset,@Param("limit") int limit);


    /**
     * 封装数据到Map中传入并执行mysql的秒杀存储过程
     */
    void killByProcedure(Map paramMap);

}
