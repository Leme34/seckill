package com.lee.seckill.dao;

import com.lee.seckill.bean.Seckill;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 操作redis
 */
public class RedisDao {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private final JedisPool jedisPool;

    @Value("${REDIS_KEY_PREFIX}")
    private String REDIS_KEY_PREFIX;
    @Value("${REDIS_EXPIRE_TIME}")
    private String REDIS_EXPIRE_TIME;

    //在构造方法初始化redis连接池
    public RedisDao(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }

    //根据Seckill.class字节码使用protostuff把对象序列化,比json快很多倍
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);


    /**
     * 若缓存中有则返回反序列化后的对象,否则返回null
     * 反序列化：byte[] -> Object
     */
    public Seckill getSeckill(Long seckillId) {
        try {
            //从连接池取出连接对象
            Jedis jedis = jedisPool.getResource();
            try {
                String key = REDIS_KEY_PREFIX + ":" + seckillId;
                //返回序列化的字节数据
                byte[] bytes = jedis.get(key.getBytes());
                //若缓存中有
                if (bytes != null) {
                    //protostuff反序列化seckill对象
                    Seckill seckill = schema.newMessage();
                    ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
                    //返回已被反序列化的对象
                    return seckill;
                }
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        //否则缓存中没有返回null
        return null;
    }

    /**
     * 把对象序列化为byte[]后放入redis缓存,设置过期时间
     * 序列化：Object -> byte[]
     * @return 返回redis操作结果码
     */
    public String putSeckill(Seckill seckill) {
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                //序列化对象为byte[]
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));//分配默认大小的缓冲区
//缓存到redis
                String key = REDIS_KEY_PREFIX + ":" + seckill.getSeckillId();
                //返回结果码
                String result = jedis.setex(key.getBytes(), Integer.parseInt(REDIS_EXPIRE_TIME), bytes);
                return result;
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

}
