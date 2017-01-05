package com.nonobank.architecture.cache;

import com.nonobank.architecture.enumeration.ListPosition;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AbstractCacheClient {

	public String setExpireWithRetry(String key, int seconds, String value, int retryTimes, int sleepSeconds);

	public Boolean setWithRetry(String key, String value, int retryTimes, int sleepSeconds);

	public Long delWithRetry(int retryTimes, int sleepSeconds, String... keys);

	public Boolean expireWithRetry(String key, int seconds, int retryTimes, int sleepSeconds);

	public String getWithRetry(String key, int retryTimes, int sleepSeconds);

	public Boolean set(String key, String value);

	public String get(String key);

	public Long setnx(String key,String value);

	public String getSet(String key, String value);

	public Boolean exists(String key);

	public Boolean expire(String key, int seconds);

	public Long expireAt(String key, long unixTime);

	public Long append(String key, String value);

	public Long decr(String key);

	public Long decrBy(String key, long integer);

	/** Increment the number stored at key by one.*/
	public Long	incr(String key);

	public  Long incrBy(String key, long integer);

	public Long del(String... keys);

	public String getrange(String key, long startOffset, long endOffset);

	public String hget(String key, String field);

	/**Remove the specified field from an hash stored at key.*/
	Long hdel(String key, String... fields);

	/**Test for existence of a specified field in a hash */

	Boolean	hexists(String key, String field);

	public Map<String, String> hgetAll(String key);

	/**increment the number stored at field in the hash at key by value.*/
	Long hincrBy(String key, String field, long value);

	/** Return all the fields in a hash.*/
	public Set<String> hkeys(String key);

	/**Return the number of items in a hash.*/
	public Long hlen(String key);

	/**Retrieve the values associated to the specified fields.*/
	public List<String> hmget(String key, String... fields);

	/** Set the respective fields to the respective values.*/
	public String hmset(String key, Map<String, String> hash);

	public  Long hset(String key, String field, String value);

	/**Set the specified hash field to the specified value if the field not exists.*/
	public  Long hsetnx(String key, String field, String value);

	/**Return all the values in a hash.*/
	public List<String>	hvals(String key);

	/** Return the specified element of the list stored at the specified key.*/
	public  String lindex(String key, long index);

	/****/
	public  Long linsert(String key, ListPosition where, String pivot, String value);


	public List<String> mget(String... keys);

	public Boolean mset(String... keysvalues);

	/**
	 * Return the length of the list stored at the specified key
     */
	public Long llen(String key);

	/**
	 *  Atomically return and remove the first (LPOP) or last (RPOP) element of the list.
     */
	public String	lpop(String key);

	/**
	 *  Add the string value to the head (LPUSH) or tail (RPUSH) of the list stored at key.
     */
	public Long lpush(String key, String... strings);

	/**将值 value 插入到列表 key 的表头，当且仅当 key 存在并且是一个列表。当 key 不存在时， LPUSHX 命令什么也不做**/
	public  Long lpushx(String key, String... string);

	/**
	 *  Return the specified elements of the list stored at the specified key.
     */
	public List<String> lrange(String key, long start, long end);

	/**Remove the first count occurrences of the value element from the list.**/
	public Long	lrem(String key, long count, String value);

	/**Trim an existing list so that it will contain only the specified range of elements specified.**/
	public String	ltrim(String key, long start, long end);


	/**
	 *Set a new value as the element at index position of the List at key.
     */
	public String	lset(String key, long index, String value);

	/**
	 * Atomically return and remove the first (LPOP) or last (RPOP) element of the list
     */
	public String	rpop(String key);

	/**
	 * Add the string value to the head (LPUSH) or tail (RPUSH) of the list stored at key.
     */
	public Long rpush(String key, String... strings);

	//将值 value 插入到列表 key 的表尾，当且仅当 key 存在并且是一个列表
	//和 RPUSH 命令相反，当 key 不存在时， RPUSHX 命令什么也不做。
	public Long rpushx(String key, String string);

	/**
	 * Add the specified member to the set value stored at key.
     */
	public Long sadd(String key, String... members);

	/**
	 * Return the set cardinality (number of elements).
     */
	public Long scard(String key);

	/**  Return all the members (elements) of the set value stored at key. **/
	public Set<String>	smembers(String key);


	/**The command is exactly equivalent to the following group of commands: SET + EXPIRE.**/
	public String	setExpire(String key, int seconds, String value);

	public String spop(String key);

	/****/
	public Long setrange(String key, long offset, String value);

	/** Return 1 if member is a member of the set stored at key, otherwise 0 is returned.**/
	public Boolean	sismember(String key, String member);

	/**返回key的value长度**/
	public Long strlen(String key);

	/**The TTL command returns the remaining time to live in seconds of a key that has an EXPIRE set.**/
	public Long ttl(String key);

	//Sort a Set or a List.
	public List<String> sort(String key);

	//Sort a Set or a List accordingly to the specified parameters.
	public List<String> sort(String key, SortingParams sortingParameters);

	//Sort a Set or a List accordingly to the specified parameters and store the result at dstkey.
	public Long	sort(String key, SortingParams sortingParameters, String dstkey);

	// Sort a Set or a List and Store the Result at dstkey.
	public Long sort(String key, String dstkey);

	//Return a random element from a Set, without removing the element.
	public  String	srandmember(String key);

	public List<String> srandmember(String key,int count);

	//Remove the specified member from the set value stored at key.
	public Long srem(String key, String... members);

	//Return a subset of the string from offset start to offset end (both offsets are inclusive).
	public String substr(String key, int start, int end);

	//Return the type of the value stored at key in form of a string.
	public String	type(String key);

	// Add the specified member having the specifeid score to the sorted set stored at key.
	public Long zadd(String key, double score, String member);

	public Long	zadd(String key, Map<String,Double> scoreMembers);

	// Return the sorted set cardinality (number of elements).
	public Long zcard(String key);

	public Long	zcount(String key, double min, double max);

	public Long zcount(String key, String min, String max);

	//If member already exists in the sorted set adds the increment to its score and updates the position of the element in the sorted set accordingly.
	public Double	zincrby(String key, double score, String member);

	public Set<String>	zrange(String key, long start, long end);

	//  Return the all the elements in the sorted set at key with a score between min and max (including elements with score equal to min or max).
	public  Set<String>	zrangeByScore(String key, double min, double max);

	//  Return the all the elements in the sorted set at key with a score between min and max (including elements with score equal to min or max).
	public Set<String>	zrangeByScore(String key, double min, double max, int offset, int count);

	public Set<String>	zrangeByScore(String key, String min, String max);

	public Set<String>	zrangeByScore(String key, String min, String max, int offset, int count);

	public Set<Tuple>	zrangeByScoreWithScores(String key, double min, double max);

	public  Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count);

	public Set<Tuple>	zrangeByScoreWithScores(String key, String min, String max);

	public Set<Tuple>	zrangeByScoreWithScores(String key, String min, String max, int offset, int count);

	public  Set<Tuple>	zrangeWithScores(String key, long start, long end);

	//  Return the rank (or index) or member in the sorted set at key, with scores being ordered from low to high.
	public Long	zrank(String key, String member);

	// Remove the specified member from the sorted set value stored at key.
	public  Long zrem(String key, String... members);

	//Remove all elements in the sorted set at key with rank between start and end.
	public  Long zremrangeByRank(String key, long start, long end);

	//  Remove all the elements in the sorted set at key with a score between min and max (including elements with score equal to min or max).
	public Long zremrangeByScore(String key, double start, double end);

	public Long	zremrangeByScore(String key, String start, String end);

	public Set<String>	zrevrange(String key, long start, long end);

	public Set<String>	zrevrangeByScore(String key, double max, double min);

	public Set<String>	zrevrangeByScore(String key, double max, double min, int offset, int count);

	public Set<String>	zrevrangeByScore(String key, String max, String min);

	public  Set<String>	zrevrangeByScore(String key, String max, String min, int offset, int count);

	public Set<Tuple>	zrevrangeByScoreWithScores(String key, double max, double min);

	public Set<Tuple>	zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count);

	public  Set<Tuple>	zrevrangeByScoreWithScores(String key, String max, String min);

	public Set<Tuple>	zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count);

	public  Set<Tuple>	zrevrangeWithScores(String key, long start, long end);

	public  Long zrevrank(String key, String member);

	public Double zscore(String key, String member);

}
