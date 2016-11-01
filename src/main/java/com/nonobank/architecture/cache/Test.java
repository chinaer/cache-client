package com.nonobank.architecture.cache;

import redis.clients.jedis.Jedis;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by geyingchao on 16/10/31.
 */
public class Test {


    public static void main(String[] args) {
        AtomicInteger count=new AtomicInteger(0);//11+9+14
        Jedis jedis=new Jedis("172.16.0.171",6579);
        Set<String> sets=jedis.keys("*.lock.cache.key");
        for(String key:sets){
            if(Integer.valueOf(jedis.get(key))>=5){

                count.addAndGet(1);
            }
        }
        System.out.println(count.get());
    }


}
