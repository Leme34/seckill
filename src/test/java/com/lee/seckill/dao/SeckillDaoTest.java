package com.lee.seckill.dao;

import com.lee.seckill.bean.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

//junit整合spring单元测试
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})  //加载spring上下文配置
public class SeckillDaoTest {

    //注入Dao实现类
    @Resource //J2EE的注解，当找不到与名称匹配的bean时才按照类型进行装配
    private SeckillDao seckillDao;

    @Test
    public void reduceNumber() throws Exception {
        Date killTime = new Date();
        int effectRows = seckillDao.reduceNumber(1000L, killTime);
        System.out.println("effectRows="+effectRows);
    }

    @Test
    public void queryById() throws Exception {
        long id =1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill);
    }

    @Test
    public void queryAll() throws Exception {
        List<Seckill> seckills = seckillDao.queryAll(0,5);
        System.out.println(seckills);
    }

}