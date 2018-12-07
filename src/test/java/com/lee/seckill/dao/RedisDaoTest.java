package com.lee.seckill.dao;

import com.lee.seckill.bean.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class RedisDaoTest {

    private Long id = 1001L;

    @Autowired
    private RedisDao redisDao;
    @Autowired
    private SeckillDao seckillDao;

    @Test
    public void testSeckill() throws Exception {
        //先从缓存中取
        Seckill seckill = redisDao.getSeckill(id);
        //没有再从数据库取
        if (seckill == null) {
            Seckill seckill1 = seckillDao.queryById(id);
            //若存在此商品,加入缓存
            if (seckill1 != null) {
                String result = redisDao.putSeckill(seckill1);
                System.out.println("result=" + result);
                //从redis中取出验证
                Seckill seckill2 = redisDao.getSeckill(id);
                System.out.println("seckill from redis = " + seckill2);
            }
        }
    }

}