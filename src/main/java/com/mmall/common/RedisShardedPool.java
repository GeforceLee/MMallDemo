package com.mmall.common;

import com.google.common.collect.Lists;
import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.List;

/**
 * @author geforce
 * @date 2018/4/10
 */
public class RedisShardedPool {
    /**
     * ShardedJedis连接池
     */
    private static ShardedJedisPool pool;

    /**
     * 最大连接数
     */
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));

    /**
     * 最大空闲数
     */
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","10"));

    /**
     * 最小空闲数
     */
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","2"));

    /**
     * borrow一个jedis实例时,是否进行验证.是true得到的实例是可用
     */
    private static Boolean testOnBorrow  = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));

    /**
     * return一个jedis实例时,是否进行验证.如果是true则放回的实例是可用的
     */
    private static Boolean testOnReturn  = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","ture"));;

    /**
     * redis1 ip
     */
    private static String  redis1IP = PropertiesUtil.getProperty("redis1.ip");

    /**
     * redis1 端口
     */
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port","2"));



    /**
     * redis2 ip
     */
    private static String  redis2IP = PropertiesUtil.getProperty("redis2.ip");

    /**
     * redis2 端口
     */
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port","2"));

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        //连接耗尽时候是否阻塞,false会抛出异常,true阻塞知道超时.默认为true
        config.setTestOnBorrow(true);


        JedisShardInfo info1 = new JedisShardInfo(redis1IP,redis1Port,1000*2);

        JedisShardInfo info2 = new JedisShardInfo(redis2IP,redis2Port,1000*2);

        List<JedisShardInfo> jedisShardInfoList = Lists.newArrayList(info1,info2);

        pool = new ShardedJedisPool(config,jedisShardInfoList,Hashing.MURMUR_HASH,Sharded.DEFAULT_KEY_TAG_PATTERN);

    }

    static {
        initPool();
    }


    public static ShardedJedis getJedis() {
        return pool.getResource();
    }

    public static void returnResource(ShardedJedis jedis) {
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(ShardedJedis jedis) {
        pool.returnBrokenResource(jedis);
    }


    public static void main(String[] args) {
        ShardedJedis jedis = pool.getResource();
//        jedis.set("lylkey","lylvalue");
//        returnResource(jedis);

        for (int i = 0; i < 10; i++) {
            jedis.set("key"+i,"vaule"+i);
        }
        returnResource(jedis);
        //临时调用,所有连接池的连接
//        pool.destroy();
    }
}
