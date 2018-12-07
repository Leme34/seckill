package com.lee.seckill.service.impl;

import com.lee.seckill.bean.Seckill;
import com.lee.seckill.bean.SuccessKilled;
import com.lee.seckill.dao.RedisDao;
import com.lee.seckill.dao.SeckillDao;
import com.lee.seckill.dao.SuccesskilledDao;
import com.lee.seckill.dto.Exposer;
import com.lee.seckill.dto.SeckillExecution;
import com.lee.seckill.enums.SeckillStateEnum;
import com.lee.seckill.exception.RepeatKillException;
import com.lee.seckill.exception.SeckillCloseException;
import com.lee.seckill.exception.SeckillException;
import com.lee.seckill.service.SeckillService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeckillServiceImpl implements SeckillService {

    //md5盐值字符串
    private final String salt = "k12zk~*b/as-1`sahk3+5vcd-/*aa";
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccesskilledDao successkilledDao;
    @Autowired
    private RedisDao redisDao;

    @Override
    public List<Seckill> getAllsecSeckills() {
        return seckillDao.queryAll(0, 100);
    }

    @Override
    public Seckill getById(Long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(Long seckillId) {

        //TODO 优化点1、使用redis缓存,过期时间维护数据一致性
        Seckill seckill = null;
        seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            seckill = seckillDao.queryById(seckillId);
            //秒杀商品不存在
            if (seckill == null) {
                return new Exposer(seckillId, false);
            } else {  //缓存到redis中
                redisDao.putSeckill(seckill);
            }
        }
        long startTime = seckill.getStartTime().getTime();
        long endTime = seckill.getEndTime().getTime();
        //不在秒杀进行时间内
        long now = new Date().getTime();
        if (now < startTime || now > endTime) {
            return new Exposer(seckillId, false, now, startTime, endTime);
        }
        //秒杀进行时,生成加盐MD5,返回秒杀接口地址
        String md5 = geneMD5(seckillId);
        return new Exposer(seckillId, true, md5);
    }


    @Transactional  //spring控制事务回滚
    @Override
    public SeckillExecution executeSeckill(Long seckillId, Long userPhone, String md5)
            throws SeckillCloseException, RepeatKillException, SeckillException {
        //校验MD5,若被篡改则秒杀失败
        if (StringUtils.isBlank(md5) || !md5.equals(geneMD5(seckillId))) {
            throw new SeckillException("seckill data rewrote");
        }
        //执行秒杀 :
        Date now = new Date();
        try {
            //TODO seckill表的索引有：start_time、end_time、create_time， 所以insert语句没有索引条件 没有行级锁,而update减库存语句用到了时间比较作为条件 有行级锁
            //TODO 优化点2、先执行insert(不会获得行级锁),再执行update(获得行级锁),commit/rollback事务释放锁,从而减少等待释放锁的时间
            //1、记录秒杀成功详细
            int insertRows = successkilledDao.insertSuccessKilled(seckillId, userPhone);
            if (insertRows <= 0) {  //库存为0或不在秒杀时间内则失败,rollback
                throw new RepeatKillException("seckill repeat");
            } else {
                //2、减库存
                int updateRows = seckillDao.reduceNumber(seckillId, now);
                if (updateRows <= 0) {  //重复秒杀,rollback
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    //秒杀成功,返回秒杀成功详细,commit
                    SuccessKilled successKilled = successkilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
//            //1、减库存
//            int updateRows = seckillDao.reduceNumber(seckillId, now);
//            //库存为0或不在秒杀时间内则失败
//            if (updateRows <= 0) {
//                throw new SeckillCloseException("seckill is closed");
//            } else {
//                //2、记录秒杀成功详细
//                int insertRows = successkilledDao.insertSuccessKilled(seckillId, userPhone);
//                if (insertRows <= 0) {
//                    throw new RepeatKillException("seckill repeat");
//                } else {
//                    //秒杀成功,返回秒杀成功详细
//                    SuccessKilled successKilled = successkilledDao.queryByIdWithSeckill(seckillId, userPhone);
//                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
//                }
//            }
        } catch (SeckillCloseException e) {
            throw e;
        } catch (RepeatKillException e) {
            throw e;
        } catch (Exception e) { //最后统一接收其他RuntimeException,使spring帮我们回滚事务
            logger.error(e.getMessage(), e);
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }


    //TODO 优化点3、使用mysql存储过程执行秒杀操作，减少网络延时和GC造成的行级锁等待时间
    @Override
    public SeckillExecution executeSeckillProcedure(Long seckillId, Long userPhone, String md5) {
        //MD5校验
        if (StringUtils.isBlank(md5) || !md5.equals(geneMD5(seckillId))) {
            return new SeckillExecution(seckillId, SeckillStateEnum.DATA_REWROTE);
        }
        //数据封装Map传入mysql存储过程
        Map<Object, Object> map = new HashMap<>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", new Date());
        map.put("result", null);  //对应存储视图的OUT
        try {
            //执行存储过程后result被赋值
            seckillDao.killByProcedure(map);
            //取出存储过程执行返回的结果,若没有则是默认值-2(没有库存或不在秒杀进行时间内 或 sql出错)
            Integer result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {  //秒杀成功
                SuccessKilled successKilled = successkilledDao.queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
            } else {  //秒杀失败，返回result值对应的枚举类型到Controller
                return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
        }
    }

    /**
     * 根据seckillId生成加盐的MD5
     */
    private String geneMD5(Long seckillId) {
        String base = seckillId + "/" + salt;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }

}
