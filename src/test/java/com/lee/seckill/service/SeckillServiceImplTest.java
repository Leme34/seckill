package com.lee.seckill.service;

import com.lee.seckill.bean.Seckill;
import com.lee.seckill.dto.Exposer;
import com.lee.seckill.dto.SeckillExecution;
import com.lee.seckill.exception.RepeatKillException;
import com.lee.seckill.exception.SeckillCloseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-*.xml"})
public class SeckillServiceImplTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getAllsecSeckills() throws Exception {
        List<Seckill> seckillList = seckillService.getAllsecSeckills();
        logger.info("seckillList={}", seckillList);
    }

    @Test
    public void getById() throws Exception {
        Seckill seckill = seckillService.getById(1000L);
        logger.info("seckill={}", seckill);
    }

    //exportSeckillUrl与executeSeckill集成测试
    @Test
    public void testSeckill() throws Exception {
        long id = 1000L;
        long phone = 12345678;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer={}", exposer);
        //秒杀已开始
        if (exposer.isExposed()) {
            String md5 = exposer.getMd5();
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
                logger.info("seckillExecution={}", seckillExecution);
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            } catch (SeckillCloseException e) {
                logger.error(e.getMessage());
            }
        } else {
            //不在秒杀进行时间内
            logger.warn("exposer={}", exposer);
        }
    }

    @Test
    public void testSeckillByProcedure() {
        long id = 1002L;
        long phone = 12345678;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.isExposed()) {
            String md5 = exposer.getMd5();
            SeckillExecution seckillExecution = seckillService.executeSeckillProcedure(id, phone, md5);
            logger.info(seckillExecution.getStateInfo());
        }

    }

}