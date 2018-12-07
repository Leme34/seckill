package com.lee.seckill.dao;

import com.lee.seckill.bean.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccesskilledDaoTest {

    @Resource
    private SuccesskilledDao successkilledDao;

    @Test
    public void insertSuccessKilled() throws Exception {
        int effectRows = successkilledDao.insertSuccessKilled(1000L, 123456L);
        System.out.println(effectRows);
    }

    @Test
    public void queryByIdWithSeckill() throws Exception {
        SuccessKilled successKilled = successkilledDao.queryByIdWithSeckill(1000L, 123456L);
        System.out.println(successKilled);
    }

}