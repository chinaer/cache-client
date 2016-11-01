package com.nonobank.architecture.controller;

import com.nonobank.architecture.cache.CacheClient;
import com.nonobank.architecture.enumeration.ListPosition;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Tuple;

import java.util.HashMap;
import java.util.Set;

/**
 * CacheClient Tester.
 *
 * @author geyingchao
 * @version 1.0
 * @since 十月 31, 2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/app-bootstrap.xml")
public class CacheClientTest {

    @Autowired
    private CacheClient cacheclient;

    /**
     * Method: set(String key, String value)
     */
    @Test
    public void testSet() throws Exception {
        System.out.println("--test Set return---"+cacheclient.set("testSetKey","testSetValue")+"--");
    }

    /**
     * Method: get(String key)
     */
    @Test
    public void testGet() throws Exception {
        System.out.println("--test Get return---"+cacheclient.get("testSetKey")+"--");
    }

    /**
     * Method: setnx(String key, String value)
     */
    @Test
    public void testSetnx() throws Exception {
        System.out.println("--test setnx first---"+cacheclient.setnx("testSetKey","testSetValue")+"--");
        System.out.println("--test setnx second---"+cacheclient.setnx("testSetKey1","tet")+"--");
    }

    /**
     * Method: getSet(String key, String value)
     */
    @Test
    public void testGetSet() throws Exception {
        System.out.println("--test getSet return:"+cacheclient.getSet("testSetKey123","zhang")+"--");
    }

    /**
     * Method: exists(String key)
     */
    @Test
    public void testExists() throws Exception {
        System.out.println("--test Exists return:"+cacheclient.exists("testSetKey123")+"--");
    }

    /**
     * Method: expire(String key, int seconds)
     */
    @Test
    public void testExpire() throws Exception {
        System.out.println("--test expire return:"+cacheclient.expire("testExpire",1)+"--");
    }

    /**
     * Method: expireAt(String key, long unixTime)
     */
    @Test
    public void testExpireAt() throws Exception {
        System.out.println("--test expireAt return:"+cacheclient.expireAt("testSetKey123",1)+"--");
    }

    /**
     * Method: append(String key, String value)
     */
    @Test
    public void testAppend() throws Exception {
        System.out.println("--test append before set [key:testAppend value:append]---return--"+cacheclient.set("testAppend","append")+"--");
        System.out.println("--test append return:"+cacheclient.append("testAppend","end")+"--");
    }

    /**
     * Method: decr(String key)
     */
    @Test
    public void testDecr() throws Exception {
        System.out.println("--test decr before set [key:testDecr value:5]---return--"+cacheclient.set("testDecr","5")+"--");
        System.out.println("--test decr return:"+cacheclient.decr("testDecr")+"--");
    }

    /**
     * Method: decrBy(String key, long integer)
     */
    @Test
    public void testDecrBy() throws Exception {
        System.out.println("--test decrBy before set [key:testDecr value:5]---return--"+cacheclient.set("testDecr","5")+"--");
        System.out.println("--test decrBy return:"+cacheclient.decrBy("testDecr",2)+"--");
    }

    /**
     * Method: del(String... keys)
     */
    @Test
    public void testDel() throws Exception {
        System.out.println("--test del before set [key:testDel1,testDel2]---return--"+cacheclient.set("testDel1","1")+"--");
        cacheclient.set("testDel2","asdf");
        System.out.println("--test del return:"+cacheclient.del("testDel1","testDel2")+"--");
    }

    /**
     * Method: getrange(String key, long startOffset, long endOffset)
     */
    @Test
    public void testGetrange() throws Exception {
        System.out.println("--test getRange before set [key:testRange,value:testRange]---return--"+cacheclient.set("testRange","testRange")+"--");
        System.out.println("--test getRange return:"+cacheclient.getrange("testRange",0,-3)+"--");
    }

    /**
     * Method: hset(String key, String field, String value)
     */
    @Test
    public void testHset() throws Exception {
        System.out.println("--test hset [key:testhset,field:field,value:value]---return--"+cacheclient.hset("testhset","field","value")+"--");
    }

    /**
     * Method: hget(String key, String field)
     */
    @Test
    public void testHget() throws Exception {
        System.out.println("--test hget [key:testhset,field:field,value:value]---return--"+cacheclient.hget("testhset","field")+"--");
    }

    /**
     * Method: hgetAll(String key)
     */
    @Test
    public void testHgetAll() throws Exception {
        System.out.println("--test hget [key:testhset,field:field,value:value]---return--"+cacheclient.hgetAll("testhset")+"--");
    }

    /**
     * Method: hkeys(String key)
     */
    @Test
    public void testHkeys() throws Exception {
        System.out.println("--test hKeys [key:testhset,field:field,value:value]---return--"+cacheclient.hkeys("testhset")+"--");
    }

    /**
     * Method: hlen(String key)
     */
    @Test
    public void testHlen() throws Exception {
        cacheclient.hset("testHlen","hlen","tes");
        System.out.println("--test hlen [key:testHlen,field:hlen,value:tes]---return--"+cacheclient.hlen("testHlen")+"--");
    }

    /**
     * Method: hmget(String key, String... fields)
     */
    @Test
    public void testHmget() throws Exception {
        cacheclient.hset("hmget","field1","value1");
        cacheclient.hset("hmget","field2","value2");
        System.out.println("--test hmget [key:hmget]---return--"+cacheclient.hmget("hmget","field1","field2")+"--");
    }

    /**
     * Method: hmset(String key, Map<String, String> hash)
     */
    @Test
    public void testHmset() throws Exception {
        HashMap<String ,String> map =new HashMap<>();
        map.put("field1","value1");
        map.put("field2","value2");
        cacheclient.hmset("hmset",map);
        System.out.println("--hmset[key:hmset,fields:field1,field2,value:value1,value2]--return--"+cacheclient.hmset("hmset",map)+"--");
    }

    /**
     * Method: mget(String... keys)
     */
    @Test
    public void testMget() throws Exception {
        cacheclient.set("mget1","mvalue");
        cacheclient.set("mset2","maf");
        System.out.println("--testmget--result:"+cacheclient.mget("mget1","mset2","m2")+"--");
    }

    /**
     * Method: mset(String... keysvalues)
     */
    @Test
    public void testMset() throws Exception {
        System.out.println("--testmset--result:"+cacheclient.mset("msetKey1","value1","msetKey2","value2")+"--");
    }

    /**
     * Method: incr(String key)
     */
    @Test
    public void testIncr() throws Exception {
        cacheclient.set("incr","3");
        System.out.println("--test incr before 3 result:"+cacheclient.incr("incr")+"--");
    }

    /**
     * Method: incrBy(String key, long integer)
     */
    @Test
    public void testIncrBy() throws Exception {
        cacheclient.set("incrBy","5");
        System.out.println("--test incrBy before 5 result:"+cacheclient.incrBy("incrBy",5)+"--");
    }

    /**
     * Method: hdel(String key, String... fields)
     */
    @Test
    public void testHdel() throws Exception {
        cacheclient.hset("hdel","delKey","value");
        cacheclient.hset("hdel","delKey2","value2");
        cacheclient.hset("hdel","delKey3","va3");
        System.out.println("--test hdel result:"+cacheclient.hdel("hdel","delKey","delKey2","d")+"--");
    }

    /**
     * Method: hexists(String key, String field)
     */
    @Test
    public void testHexists() throws Exception {
        cacheclient.hset("hexit","field","value");
        System.out.println("--test hexists result:"+cacheclient.hexists("hexit","field")+"--");
    }

    /**
     * Method: hincrBy(String key, String field, long value)
     */
    @Test
    public void testHincrBy() throws Exception {
        cacheclient.hset("hincrby","field","9");
        System.out.println("--test hincrBy before 9 add 5 result:"+cacheclient.hincrBy("hincrby","field",5)+"--");
    }

    /**
     * Method: hsetnx(String key, String field, String value)
     */
    @Test
    public void testHsetnx() throws Exception {
        cacheclient.hset("hsetnx","key","value");
        System.out.println("--test hsetnx result:"+cacheclient.hsetnx("hsetnx","key","val"));
    }

    /**
     * Method: hvals(String key)
     */
    @Test
    public void testHvals() throws Exception {
        cacheclient.hset("hvals","field3","value3");
        cacheclient.hset("hvals","field2","value2");
        cacheclient.hset("hvals","field1","value1");
        System.out.println("--test hvals result:"+cacheclient.hvals("hvals"));
    }

    /**
     * Method: lindex(String key, long index)
     */
    @Test
    public void testLindex() throws Exception {
        cacheclient.lpush("lindex","valdd");
        cacheclient.lpush("lindex","zdaf");
        System.out.println("--test lindex 队列[valdd->zdaf] result:"+cacheclient.lindex("lindex",0));
    }

    /**
     * Method: linsert(String key, BinaryClient.LIST_POSITION where, String pivot, String value)
     */
    @Test
    public void testLinsert() throws Exception {
        cacheclient.linsert("lindex", ListPosition.AFTER,"zdaf","adf");
        System.out.println(cacheclient.lrange("lindex",0,4));
    }

    /**
     * Method: llen(String key)
     */
    @Test
    public void testLlen() throws Exception {
        cacheclient.lpush("llen","valdd");
        cacheclient.lpush("llen","da");
        System.out.println("--test llen [valdd->da] result:"+cacheclient.llen("llen")+"--");
    }

    /**
     * Method: lpop(String key)
     */
    @Test
    public void testLpop() throws Exception {
        cacheclient.lpush("lpop","first");
        cacheclient.lpush("lpop","second");
        System.out.println("--test lpop [first,second] result:"+cacheclient.lpop("lpop")+"--");
        System.out.println("--after lpop "+cacheclient.lrange("lpop",0,cacheclient.llen("lpop").intValue()));
    }

    /**
     * Method: lpush(String key, String... strings)
     */
    @Test
    public void testLpush() throws Exception {
        System.out.println("---test lpush result:"+cacheclient.lpush("lpop","asdf")+"---");
    }

    /**
     * Method: lpushx(String key, String string)
     */
    @Test
    public void testLpushx() throws Exception {
        cacheclient.lpush("lpushx","sad");
        System.out.println("---test lpush result:"+cacheclient.lpushx("lpushx","asdf")+"---");
    }

    /**
     * Method: lrange(String key, long start, long end)
     */
    @Test
    public void testLrange() throws Exception {
        System.out.println("--test lrange result:"+cacheclient.lrange("lpushx",0,5));
    }

    /**
     * Method: lrem(String key, long count, String value)
     */
    @Test
    public void testLrem() throws Exception {
        System.out.println("--test lrem result:"+cacheclient.lrem("lpushx",5,"sad"));

    }

    /**
     * Method: ltrim(String key, long start, long end)
     */
    @Test
    public void testLtrim() throws Exception {
        System.out.println("--test ltrim result"+cacheclient.ltrim("lpushx",0,-1));
    }

    /**
     * Method: lset(String key, long index, String value)
     */
    @Test
    public void testLset() throws Exception {
        System.out.println("--test lset result:"+cacheclient.lset("lpushx",1,"zhangsan"));
    }

    /**
     * Method: rpop(String key)
     */
    @Test
    public void testRpop() throws Exception {
        System.out.println("--test rpop result:"+cacheclient.rpop("lpushx")+"--");
    }

    /**
     * Method: rpush(String key, String... strings)
     */
    @Test
    public void testRpush() throws Exception {
        System.out.println("--test rpush result:"+cacheclient.rpush("lpushx","adf")+"--");
    }

    /**
     * Method: rpushx(String key, String string)
     */
    @Test
    public void testRpushx() throws Exception {
        System.out.println("--test rpush result:"+cacheclient.rpushx("lpushx","adf")+"--");
    }

    /**
     * Method: sadd(String key, String... members)
     */
    @Test
    public void testSadd() throws Exception {
        cacheclient.sadd("sadd","asdf");
        System.out.println("--test sadd result"+cacheclient.sadd("sadd","str","dfe")+"--");
    }

    /**
     * Method: scard(String key)
     */
    @Test
    public void testScard() throws Exception {
        System.out.println("--test scard result:"+cacheclient.scard("sadd")+"--");
    }

    /**
     * Method: smembers(String key)
     */
    @Test
    public void testSmembers() throws Exception {
        System.out.println("--test smembers result:"+cacheclient.smembers("sadd")+"--");
    }

    /**
     * Method: setExpire(String key, int seconds, String value)
     */
    @Test
    public void testSetExpire() throws Exception {
        System.out.println("--test setExpire before result:"+cacheclient.setExpire("expire",10,"expire"));
        Thread.sleep(10000);
        System.out.println("--test setExpire after result:"+cacheclient.get("expire"));

    }

    /**
     * Method: spop(String key)
     */
    @Test
    public void testSpop() throws Exception {
       cacheclient.sadd("sadd","value1");
        cacheclient.sadd("sadd","value2");
        System.out.println("--test sadd before"+cacheclient.smembers("sadd")+"--");
        System.out.println("--test sadd result:"+cacheclient.spop("sadd")+"--");
        System.out.println("--test sadd after result:"+cacheclient.smembers("sadd")+"--");
    }

    /**
     * Method: setrange(String key, long offset, String value)
     */
    @Test
    public void testSetrange() throws Exception {
        cacheclient.set("setrange","ddd");
        System.out.println("--test setRange result:"+cacheclient.setrange("setrange",3,"orange"));
    }

    /**
     * Method: sismember(String key, String member)
     */
    @Test
    public void testSismember() throws Exception {
        System.out.println("--test sismenber result:"+cacheclient.sismember("sadd","asdf"));
    }

    /**
     * Method: strlen(String key)
     */
    @Test
    public void testStrlen() throws Exception {
        System.out.println("--test strlen result:"+cacheclient.strlen("setrange"));
    }

    /**
     * Method: ttl(String key)
     */
    @Test
    public void testTtl() throws Exception {
        cacheclient.setExpire("setrange",5,"value");
        System.out.println("--test ttl result:"+cacheclient.ttl("setrange"));
        Thread.sleep(5000);
        System.out.println("--test ttl result:"+cacheclient.ttl("setrange"));
    }

    /**
     * Method: sort(String key)
     */
    @Test
    public void testSortKey() throws Exception {
       cacheclient.sadd("sort","1","6","2","8","5");
        System.out.println("--test sort [1,6,2,8,5] result:"+cacheclient.sort("sort")+"--");
        cacheclient.del("sort");
        System.out.println("--"+cacheclient.sort("sort")+"--");

    }

    /**
     * Method: sort(String key, SortingParams sortingParameters)
     */
    @Test
    public void testSortForKeySortingParameters() throws Exception {
    }

    /**
     * Method: sort(String key, SortingParams sortingParameters, String dstkey)
     */
    @Test
    public void testSortForKeySortingParametersDstkey() throws Exception {
    }

    /**
     * Method: sort(String key, String dstkey)
     */
    @Test
    public void testSortForKeyDstkey() throws Exception {
    }

    /**
     * Method: srandmember(String key)
     */
    @Test
    public void testSrandmember() throws Exception {
        cacheclient.sadd("randmember","23","asdf","dd");
        System.out.println("--test srandmember result:"+cacheclient.srandmember("randmember")+"--");
        cacheclient.del("randmenber");
    }

    @Test
    public void testSrandmemberCount() throws Exception {
        cacheclient.sadd("randmember","23","asdf","dd");
        System.out.println("--test randmember result:"+cacheclient.srandmember("randmember",2)+"--");
        cacheclient.del("randmenber");
    }

    /**
     * Method: srem(String key, String... members)
     */
    @Test
    public void testSrem() throws Exception {
        cacheclient.sadd("randmember","23","asdf","dd");
        System.out.println("--test srem [23,asdf,dd] result:"+cacheclient.srem("randmember","23","sd")+"--");
        System.out.println("--test srem after result:"+cacheclient.smembers("randmember"));
        cacheclient.del("randmenber");

    }

    /**
     * Method: substr(String key, int start, int end)
     */
    @Test
    public void testSubstr() throws Exception {
        cacheclient.set("substr","zhangsan");
        System.out.println("--test substr [substr:zhangsan] result:"+cacheclient.substr("substr",0,3)+"--");;
        cacheclient.del("substr");
    }

    /**
     * Method: type(String key)
     */
    @Test
    public void testType() throws Exception {
        cacheclient.sadd("listKey","fad");
        cacheclient.set("stringKey","sdf");
        System.out.println("--test type set result:"+cacheclient.type("listKey")+"--");
        System.out.println("--test type String result:"+cacheclient.type("stringKey")+"--");
        cacheclient.del("listKey","stringKey");
    }

    /**
     * Method: zadd(String key, double score, String member)
     */
    @Test
    public void testZaddForKeyScoreMember() throws Exception {
            System.out.println("--test zadd result:"+ cacheclient.zadd("zadd",5,"fad")+"--");
            cacheclient.del("zadd");
    }

    /**
     * Method: zadd(String key, Map<String, Double> scoreMembers)
     */
    @Test
    public void testZaddForKeyScoreMembers() throws Exception {
        HashMap<String,Double> map=new HashMap<>();
        map.put("zhang",5d);
        map.put("li",3d);
        System.out.println("--test zadd result:"+ cacheclient.zadd("zadd",map)+"--");
        cacheclient.del("zadd");
    }

    /**
     * Method: zcard(String key)
     */
    @Test
    public void testZcard() throws Exception {
        HashMap<String,Double> map=new HashMap<>();
        map.put("zhang",5d);
        map.put("li",3d);
        cacheclient.zadd("zadd",map);
        System.out.println("--test zcard result:"+ cacheclient.zcard("zadd")+"--");
        cacheclient.del("zadd");
    }

    /**
     * Method: zcount(String key, double min, double max)
     */
    @Test
    public void testZcountForKeyMinMax() throws Exception {
        HashMap<String,Double> map=new HashMap<>();
        map.put("zhang",5d);
        map.put("li2",3d);
        map.put("lif",1d);
        map.put("lia",8d);
        cacheclient.zadd("zadd",map);
        System.out.println("--test zcount result:"+ cacheclient.zcount("zadd",0,5)+"--");
        cacheclient.del("zadd");
    }

    /**
     * Method: zincrby(String key, double score, String member)
     */
    @Test
    public void testZincrby() throws Exception {
        HashMap<String,Double> map=new HashMap<>();
        map.put("zhang",5d);
        map.put("li2",3d);
        map.put("lif",1d);
        map.put("lia",8d);
        cacheclient.zadd("zadd",map);
        System.out.println("--test zincrby result:"+ cacheclient.zincrby("zadd",2,"li2")+"--");
        cacheclient.del("zadd");
    }

    /**
     * Method: zrange(String key, long start, long end)
     */
    @Test
    public void testZrange() throws Exception {
        HashMap<String,Double> map=new HashMap<>();
        map.put("zhang",5d);
        map.put("li2",3d);
        map.put("lif",1d);
        map.put("lia",8d);
        cacheclient.zadd("zadd",map);
        System.out.println("--test zrange result:"+ cacheclient.zrange("zadd",2,5)+"--");
        cacheclient.del("zadd");
    }

    /**
     * Method: zrangeByScore(String key, double min, double max)
     */
    @Test
    public void testZrangeByScoreForKeyMinMax() throws Exception {
        HashMap<String,Double> map=new HashMap<>();
        map.put("zhang",5d);
        map.put("li2",3d);
        map.put("lif",1d);
        map.put("lia",8d);
        cacheclient.zadd("zadd",map);
        System.out.println("--test zrangebyscore result:"+ cacheclient.zrangeByScore("zadd",2,5)+"--");
        cacheclient.del("zadd");
    }

    /**
     * Method: zrangeByScore(String key, double min, double max, int offset, int count)
     */
    @Test
    public void testZrangeByScoreForKeyMinMaxOffsetCount() throws Exception {
        HashMap<String,Double> map=new HashMap<>();
        map.put("zhang",5d);
        map.put("li2",3d);
        map.put("lif",1d);
        map.put("lia",8d);
        cacheclient.zadd("zadd",map);
        System.out.println("--test zrangebyscore[lif(1),li2(3),zhang(5),lia(8)] result:"+ cacheclient.zrangeByScore("zadd", 2,5,0,1)+"--");
        cacheclient.del("zadd");
    }

    /**
     * Method: zrangeByScoreWithScores(String key, double min, double max)
     */
    @Test
    public void testZrangeByScoreWithScoresForKeyMinMax() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: zrangeByScoreWithScores(String key, double min, double max, int offset, int count)
     */
    @Test
    public void testZrangeByScoreWithScoresForKeyMinMaxOffsetCount() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: zrangeWithScores(String key, long start, long end)
     */
    @Test
    public void testZrangeWithScores() throws Exception {
        HashMap<String,Double> map=new HashMap<>();
        map.put("zhang",5d);
        map.put("li2",3d);
        map.put("lif",1d);
        map.put("lia",8d);
        cacheclient.zadd("zadd",map);
        System.out.println("--test zrangeWithScores[lif(1),li2(3),zhang(5),lia(8)] result:"+ cacheclient.zrangeWithScores("zadd", 2,5)+"--");
        Set<Tuple> tuples= cacheclient.zrangeWithScores("zadd", 2,5);
        System.out.println( tuples.iterator().next().getElement());
        cacheclient.del("zadd");
    }

    /**
     * Method: zrank(String key, String member)
     */
    @Test
    public void testZrank() throws Exception {
        HashMap<String,Double> map=new HashMap<>();
        map.put("zhang",5d);
        map.put("li2",3d);
        map.put("lif",1d);
        map.put("lia",8d);
        cacheclient.zadd("zadd",map);
        System.out.println("--test zrank [lif(1),li2(3),zhang(5),lia(8)] result:"+ cacheclient.zrank("zadd","li2")+"--");
        cacheclient.del("zadd");
    }

    /**
     * Method: zrem(String key, String... members)
     */
    @Test
    public void testZrem() throws Exception {
        HashMap<String,Double> map=new HashMap<>();
        map.put("zhang",5d);
        map.put("li2",3d);
        map.put("lif",1d);
        map.put("lia",8d);
        cacheclient.zadd("zadd",map);
        System.out.println("--test zrem [lif(1),li2(3),zhang(5),lia(8)] result:"+ cacheclient.zrem("zadd","li2")+"--");
        cacheclient.del("zadd");
    }

    /**
     * Method: zremrangeByRank(String key, long start, long end)
     */
    @Test
    public void testZremrangeByRank() throws Exception {
        HashMap<String,Double> map=new HashMap<>();
        map.put("zhang",5d);
        map.put("li2",3d);
        map.put("lif",1d);
        map.put("lia",8d);
        cacheclient.zadd("zadd",map);
        System.out.println("--test zremrangebyrank [lif(1),li2(3),zhang(5),lia(8)] result:"+ cacheclient.zremrangeByRank("zadd",0,2)+"--");
        System.out.println("--test zremrangebyrank [lif(1),li2(3),zhang(5),lia(8)] after:"+ cacheclient.zrevrange("zadd",0,100)+"--");
        cacheclient.del("zadd");
    }

    /**
     * Method: zremrangeByScore(String key, double start, double end)
     */
    @Test
    public void testZremrangeByScoreForKeyStartEnd() throws Exception {
        //TODO: Test goes here...
    }

    /**
     * Method: zrevrange(String key, long start, long end)
     */
    @Test
    public void testZrevrange() throws Exception {
        //TODO: Test goes here...
    }

    /**
     * Method: zrevrangeByScore(String key, double max, double min)
     */
    @Test
    public void testZrevrangeByScoreForKeyMaxMin() throws Exception {
    //TODO: Test goes here...
    }

    /**
     * Method: zrevrangeByScore(String key, double max, double min, int offset, int count)
     */
    @Test
    public void testZrevrangeByScoreForKeyMaxMinOffsetCount() throws Exception {
        //TODO: Test goes here...
    }

    /**
     * Method: zrevrangeByScoreWithScores(String key, double max, double min)
     */
    @Test
    public void testZrevrangeByScoreWithScoresForKeyMaxMin() throws Exception {
        //TODO: Test goes here...
    }

    /**
     * Method: zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count)
     */
    @Test
    public void testZrevrangeByScoreWithScoresForKeyMaxMinOffsetCount() throws Exception {
        //TODO: Test goes here...
    }

    /**
     * Method: zrevrangeWithScores(String key, long start, long end)
     */
    @Test
    public void testZrevrangeWithScores() throws Exception {
        //TODO: Test goes here...
    }

    /**
     * Method: zrevrank(String key, String member)
     */
    @Test
    public void testZrevrank() throws Exception {
        HashMap<String,Double> map=new HashMap<>();
        map.put("zhang",5d);
        map.put("li2",3d);
        map.put("lif",1d);
        map.put("lia",8d);
        cacheclient.zadd("zadd",map);
        System.out.println("--test zremrank [lif(1),li2(3),zhang(5),lia(8)] result:"+ cacheclient.zrevrank("zadd","lia")+"--");
        cacheclient.del("zadd");
    }

    /**
     * Method: zscore(String key, String member)
     */
    @Test
    public void testZscore() throws Exception {
        HashMap<String,Double> map=new HashMap<>();
        map.put("zhang",5d);
        map.put("li2",3d);
        map.put("lif",1d);
        map.put("lia",8d);
        cacheclient.zadd("zadd",map);
        System.out.println("--test zscore [lif(1),li2(3),zhang(5),lia(8)] result:"+ cacheclient.zscore("zadd","lia")+"--");
        cacheclient.del("zadd");
    }

} 
