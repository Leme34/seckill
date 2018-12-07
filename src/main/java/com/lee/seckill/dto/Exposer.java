package com.lee.seckill.dto;

import lombok.Data;

/**
 * 暴露秒杀接口地址的DTO( 数据库层与 表现层 之间的数据传输对象 )
 */
@Data
public class Exposer {

    private Long seckillId; //秒杀商品id

    private boolean exposed;  //是否已开启秒杀

    private Long now; //系统当前时间(ms)

    private Long start;  //秒杀开始时间

    private Long end;  //秒杀结束时间

    private String md5; //此商品id生成的md5,加密措施

    /**
     * 该秒杀商品不存在的构造方法
     */
    public Exposer(Long seckillId, boolean exposed) {
        this.seckillId = seckillId;
        this.exposed = exposed;
    }

    /**
     * 秒杀未开始或已结束的构造方法
     */
    public Exposer(Long seckillId, boolean exposed, Long now, Long start, Long end) {
        this.seckillId = seckillId;
        this.exposed = exposed;
        this.now = now;
        this.start = start;
        this.end = end;
    }

    /**
     * 秒杀进行中的构造方法
     */
    public Exposer(Long seckillId, boolean exposed, String md5) {
        this.seckillId = seckillId;
        this.exposed = exposed;
        this.md5 = md5;
    }

}
