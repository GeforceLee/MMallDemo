package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author geforce
 * @date 2018/4/9
 */
public class RedisPool {
    /**
     * jedis连接池
     */
    private static JedisPool pool;

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
     * redis ip
     */
    private static String  redisIP = PropertiesUtil.getProperty("redis.ip");


    /**
     * redis 端口
     */
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.port","2"));

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        //连接耗尽时候是否阻塞,false会抛出异常,true阻塞知道超时.默认为true
        config.setTestOnBorrow(true);


        pool = new JedisPool(config,redisIP,redisPort,1000*2);
    }

    static {
        initPool();
    }


    public static Jedis getJedis() {
        return pool.getResource();
    }

    public static void returnResource(Jedis jedis) {
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(Jedis jedis) {
        pool.returnBrokenResource(jedis);
    }


    public static void main(String[] args) {
        Jedis jedis = pool.getResource();
        jedis.set("lylkey","lylvalue");
        returnResource(jedis);

        //临时调用,所有连接池的连接
        pool.destroy();
    }

}
