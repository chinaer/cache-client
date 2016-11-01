package com.nonobank.architecture.cache;

import com.nonobank.architecture.enumeration.ListPosition;
import io.codis.jodis.JedisResourcePool;
import io.codis.jodis.RoundRobinJedisPool;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.nonobank.architecture.enumeration.CacheEnvironment;

import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;

public class CacheClient implements AbstractCacheClient {

    private Logger log = LoggerFactory.getLogger(getClass());

    private static JedisResourcePool jedisPool = null;

    private CacheConfig config;

    public CacheClient(CacheConfig config) {
        this.config = config;
        fresh();
    }

    protected JedisResourcePool fresh() {
        if (jedisPool == null) {
            synchronized (CacheClient.class) {
                if (jedisPool == null) {
                    jedisPool = RoundRobinJedisPool.create().poolConfig(config.CacheConfig2JedisPoolConfig())
                            .curatorClient(config.getZkAddressAndPort(), config.getZkSessionTimeOutMs())
                            .zkProxyDir(config.getZkProxyDir()).build();
                }
            }
        }
        return jedisPool;
    }

    public Boolean set(String key, String value) {
//		key=keyWapper(key);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value).equals("OK") ? true : false;
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    public String get(String key) {
//		key=keyWapper(key);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * key 不存在时，为 key 设置指定的值
     *
     * @param key
     * @param value
     * @return 设置成功，返回 1 。 设置失败，返回 0 。
     */
    public Long setnx(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.setnx(key, value);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 设置指定的在Redis的键的字符串值，并返回其原来的值
     *
     * @param key
     * @param value
     * @return 返回字符串，键的旧值,并设置新值。如果键不存在，那么返回null
     */
    public String getSet(String key, String value) {
//		key=keyWapper(key);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.getSet(key, value);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 测试key是否存在
     *
     * @param key
     * @return true:存在,false:不存在
     */
    public Boolean exists(String key) {
//		key=keyWapper(key);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 设定键有效期,到期时间后键从Redis中移除
     *
     * @param key
     * @param seconds
     * @return true:设置键超时成功,false:键不存在，或者未设置超时
     */
    public Boolean expire(String key, int seconds) {
//		key=keyWapper(key);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.expire(key, seconds) == 1 ? true : false;
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 以Unix时间戳格式设置键的到期时间,到期时间后键从Redis中移除
     *
     * @param key
     * @param unixTime
     * @return 1:设置键超时成功,0:键不存在，或者未设置超时
     */
    public Long expireAt(String key, long unixTime) {
        //key=keyWapper(key);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.expireAt(key, unixTime);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }


    /**
     * 获取键到期的剩余时间(秒)
     *
     * @param key
     * @return 以秒为单位的整数值TTL或负值,-1:key没有到期超时,-2:键不存在
     */
    @Override
    public Long ttl(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.ttl(key);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 追加键的一些值
     *
     * @param key
     * @param value
     * @return 追加操作后的字符串的长度
     */
    public Long append(String key, String value) {
//		key=keyWapper(key);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.append(key, value);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 存储在key字符串值的长度
     * 当key持有非字符串值则返回一个错误
     * @param key
     * @return 字符串key长度，或0表示key不存在
     */
    @Override
    public Long strlen(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.strlen(key);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }


    /**
     * key的整数值减1,如果该键不存在时，它初始被设置为0
     * 如果键包含了错误类型的值或包含不能被表示为整数，字符串，则返回错误。
     * 本操作的值限制在 64 位(bit)有符号数字表示之内
     * @param key
     * @return
     */
    public Long decr(String key) {
//		key=keyWapper(key);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.decr(key);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 将key 所储存的值减去指定的减量值
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECRBY 操作
     * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     * 本操作的值限制在 64 位(bit)有符号数字表示之内
     * @param key
     * @param integer
     * @return
     */
    public Long decrBy(String key, long integer) {
//		key=keyWapper(key);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.decrBy(key, integer);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 用于删除已存在的键。不存在的 key 会被忽略
     * @param keys
     * @return 被删除的键的数目
     */
    public Long del(String... keys) {
//		for(int i=0;i<keys.length;i++){
//			keys[i]=keyWapper(keys[i]);
//		}
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(keys);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 获取存储在键中的字符串值，由偏移确定的子串的开始和结束(两者都包括)。
     * 负偏移可以提供从字符串的末尾的偏移开始被使用
     *
     * @param key
     * @param startOffset
     * @param endOffset
     * @return 截取后的字符串
     */
    public String getrange(String key, long startOffset, long endOffset) {
//		key=keyWapper(key);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.getrange(key, startOffset, endOffset);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 指定的字符串覆盖给定 key 所储存的字符串值，覆盖的位置从偏移量 offset 开始
     *
     * @param key
     * @param offset
     * @param value
     * @return 被修改后的字符串长度
     */
    @Override
    public Long setrange(String key, long offset, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.setrange(key, offset, value);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 为哈希表中的字段赋值(特别适用于存储对象)
     * 如果哈希表不存在，一个新的哈希表被创建并进行 HSET 操作。
     * 如果字段已经存在于哈希表中，旧值将被覆盖。
     *
     * @param key
     * @param field
     * @param value
     * @return 1:插入字段为哈希表中的新建字段，并且值设置成功; 0:哈希表中域字段已经存在且旧值已被新值覆盖
     */
    public Long hset(String key, String field, String value) {
//		key=keyWapper(key);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hset(key, field, value);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 返回哈希表(key)中指定字段(field)的值
     *
     * @param key
     * @param field
     * @return 返回给定字段的值。如果给定的字段或 key 不存在时，返回null
     */
    public String hget(String key, String field) {
//		key=keyWapper(key);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hget(key, field);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 用于获取存储在哈希表(key)中的所有字段和值
     *
     * @param key
     * @return
     */
    public Map<String, String> hgetAll(String key) {
//		key=keyWapper(key);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hgetAll(key);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 用于获取哈希表中的所有字段名。
     *
     * @param key
     * @return 包含哈希表中所有字段的列表。 当 key 不存在时，返回一个空列表。
     */
    public Set<String> hkeys(String key) {
//		key=keyWapper(key);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hkeys(key);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 获取哈希表中字段的数量
     *
     * @param key
     * @return 哈希表中字段的数量, 当key不存在时，返回 0
     */
    public Long hlen(String key) {
//		key=keyWapper(key);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hlen(key);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 返回哈希表(key)中，一个或多个给定字段的值
     *
     * @param key
     * @param fields
     * @return 如果指定的字段不存在于哈希表，那么返回一个null值
     */
    public List<String> hmget(String key, String... fields) {
//		key=keyWapper(key);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hmget(key, fields);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 同时将多个 field-value (字段-值)对设置到哈希表(key)中
     * 它会覆盖哈希表中已存在的字段
     * 如果哈希表不存在，会创建一个空哈希表
     *
     * @param key
     * @param hash
     * @return ok:执行成功
     */
    public String hmset(String key, Map<String, String> hash) {
//		key=keyWapper(key);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hmset(key, hash);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 返回所有(一个或多个)给定 key 的值
     * 给定的 key 里面，有某个 key 不存在，那么这个 key 返回特殊值 null
     *
     * @param keys
     * @return
     */
    public List<String> mget(String... keys) {
//		for(int i=0;i<keys.length;i++){
//			keys[i]=keyWapper(keys[i]);
//		}
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.mget(keys);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 同时设置一个或多个 key-value 对
     *
     * @param keysvalues
     * @return
     */
    public Boolean mset(String... keysvalues) {
//		for(int i=0;i<keysvalues.length;i++){
//			keysvalues[i]=keyWapper(keysvalues[i]);
//		}
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.mset(keysvalues).equals("OK") ? true : false;
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @SuppressWarnings("unused")
    private String keyWapper(String key) {
        if (!Strings.isNullOrEmpty(key)) {
            key = CacheEnvironment.env(config.getEnvrionment()).encode(key);
            return key;
        }
        return null;
    }

    /**
     * ------------------new--------------------
     */
    /**
     * 由一个递增key的整数值+1操作
     * 该key不存在，它初始被设置为0,然后+1操作
     * 如果key包含了错误类型的值或包含不能被表示为整数，字符串，则返回错误(抛异常)
     * 该操作被限制为64位带符号整数
     *
     * @param key
     * @return 增量后的 key 的值
     */
    @Override
    public Long incr(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.incr(key);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 由一个递增key的整数值按照步长增加
     *
     * @param key
     * @param integer
     * @return 加上指定的增量值之后， key 的值
     */
    @Override
    public Long incrBy(String key, long integer) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.incrBy(key, integer);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 存储在键散列删除指定的字段
     * 如果没有这个哈希中存在指定的字段将被忽略
     * 如果键不存在，它将被视为一个空的哈希与此命令将返回0
     *
     * @param key
     * @param fields
     * @return 从散列中删除的字段的数量
     */
    @Override
    public Long hdel(String key, String... fields) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hdel(key, fields);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 检查哈希表(key)的字段(field)是否存在
     *
     * @param key
     * @param field
     * @return true:包含字段,false:不包含字段，或key不存在
     */
    @Override
    public Boolean hexists(String key, String field) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hexists(key, field);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 增加存储在哈希表中的字段值加上指定增量值。
     * 如果键不存在，新的key被哈希创建。
     * 如果字段不存在，值被设置为0之前进行操作。
     * 本操作的值被限制在 64 位(bit)有符号数字表示之内
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    @Override
    public Long hincrBy(String key, String field, long value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hincrBy(key, field, value);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 哈希表中不存在的的字段赋值
     * 如果哈希表不存在，一个新的哈希表被创建并进行 HSET 操作。
     * 如果字段已经存在于哈希表中，操作无效
     *
     * @param key
     * @param field
     * @param value
     * @return 1:设置成功 0:字段已经存在且没有操作被执行
     */
    @Override
    public Long hsetnx(String key, String field, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hsetnx(key, field, value);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 哈希表(key)所有字段(fields)的值
     *
     * @param key
     * @return 一个包含哈希表(key)中所有值的表。 当 key 不存在时，返回一个空表
     */
    @Override
    public List<String> hvals(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hvals(key);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 获取在存储于列表(key)的索引(index)的元素
     * 索引是从0开始的,所以0表示第一个元素，1第二个元素等等。
     * 负指数可用于指定开始在列表的尾部元素。这里，-1表示最后一个元素，-2指倒数第二个等等。
     *
     * @param key
     * @param index
     * @return 请求的元素，或者null当索引超出范围
     */
    @Override
    public String lindex(String key, long index) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lindex(key, index);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 在列表的元素前或者后插入元素
     * 当指定元素不存在于列表中时，不执行任何操作,当列表不存在时，被视为空列表，不执行任何操作
     * 如果 key 不是列表类型，返回一个错误
     *
     * @param key
     * @param where BEFORE/AFTER
     * @param pivot EXISTING_VALUE(需要插入的相对位置元素)
     * @param value NEW_VALUE
     * @return
     */
    @Override
    public Long linsert(String key, ListPosition where, String pivot, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.linsert(key, where.warp(), pivot, value);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 返回存储在key列表的长度
     * 如果key不存在，它被解释为一个空列表，则返回0
     * 当存储在关key的值不是一个列表，则会返回错误
     *
     * @param key
     * @return 返回整数为列表长度
     */
    @Override
    public Long llen(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.llen(key);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 删除，并返回保存列表(key)的第一个元素
     * @param key
     * @return 返回字符串，第一个元素的值，或者为null 如果key不存在
     */
    @Override
    public String lpop(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lpop(key);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 命令将一个或多个值插入到列表头部,如果 key 不存在，一个空列表会被创建并执行lpush 操作
     * 当 key 存在但不是列表类型时，返回一个错误。
     *
     * @param key
     * @param strings
     * @return 列表的长度
     */
    @Override
    public Long lpush(String key, String... strings) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lpush(key, strings);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 将一个或多个值插入到已存在的列表头部，列表不存在时操作无效
     *
     * @param key
     * @param string
     * @return 操作后的列表的长度
     */
    @Override
    public Long lpushx(String key, String... string) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lpushx(key, string);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 返回列表中指定区间内的元素，区间以偏移量 START 和 END 指定,0 表示列表的第一个元素
     * -1 表示列表的最后一个元素
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Override
    public List<String> lrange(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 根据参数 COUNT 的值，移除列表中与参数 VALUE 相等的元素
     *
     * @param key
     * @param count (>0 表头开始向表尾搜索,从表尾开始向表头搜,count = 0 : 表中所有与 VALUE 相等)，移除与 VALUE 相等的元素，数量为 COUNT
     * @param value
     * @return 被移除元素的数量。 列表不存在时返回 0
     */
    @Override
    public Long lrem(String key, long count, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrem(key, count, value);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 对一个列表进行修剪(trim),让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除
     * 下标 0 表示列表的第一个元素,-1 表示列表的最后一个元素
     *
     * @param key
     * @param start
     * @param end
     * @return ok:命令执行成功
     */
    @Override
    public String ltrim(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.ltrim(key, start, end);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 将列表 key 下标为 index 的元素的值设置为 value
     * 当 index 参数超出范围，或对一个空列表( key 不存在)进行 LSET 时，返回一个错误
     *
     * @param key
     * @param index
     * @param value
     * @return ok:操作成功返回
     */
    @Override
    public String lset(String key, long index, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lset(key, index, value);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 删除，并返回列表保存在key的最后一个元素
     * @param key
     * @return 返回最后一个元素的值，或者key不存在返回null
     */
    @Override
    public String rpop(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.rpop(key);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 将一个或多个值插入到列表的尾部(最右边)
     * 如果列表不存在，一个空列表会被创建并执行 RPUSH 操作。 当列表存在但不是列表类型时，返回一个错误
     *
     * @param key
     * @param strings
     * @return 执行操作后，列表的长度
     */
    @Override
    public Long rpush(String key, String... strings) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.rpush(key, strings);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 将值 value 插入到列表 key 的表尾，当且仅当 key 存在并且是一个列表
     * 和 RPUSH 命令相反，当 key 不存在时， rpushx 命令什么也不做
     *
     * @param key
     * @param string
     * @return 执行操作后，列表的长度
     */
    @Override
    public Long rpushx(String key, String string) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.rpushx(key, string);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 集合Set
     * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略。
     * 假如集合 key 不存在，则创建一个只包含添加的元素作成员的集合
     * 当集合 key 不是集合类型时，返回一个错误
     *
     * @param key
     * @param members
     * @return 被添加到集合中的新元素的数量，不包括被忽略的元素。
     */
    @Override
    public Long sadd(String key, String... members) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sadd(key, members);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 返回集合中元素的数量
     *
     * @param key
     * @return 集合的数量, 当集合 key 不存在时，返回 0
     */
    @Override
    public Long scard(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.scard(key);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 获取set集合
     * 返回集合 key 中的所有成员
     * 不存在的 key 被视为空集合
     *
     * @param key
     * @return 集合中的所有成员
     */
    @Override
    public Set<String> smembers(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.smembers(key);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 指定的 key 设置值及其过期时间,如果 key 已经存在,会替换旧的值
     * @param key
     * @param seconds
     * @param value
     * @return OK:设置成功
     */
    @Override
    public String setExpire(String key, int seconds, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.setex(key, seconds, value);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 移除并返回集合中的一个随机元素
     *
     * @param key
     * @return 被删除的元素, 当键时不存在时返回null
     */
    @Override
    public String spop(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.spop(key);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 判断元素是否是集合的成员
     *
     * @param key
     * @param member
     * @return true:元素是集合的成员，false:元素不是集合的成员，或 key 不存在
     */
    @Override
    public Boolean sismember(String key, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sismember(key, member);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 排序是基于数字的，各个元素将会被转化成双精度浮点数来进行大小比较
     * 如果key是一个包含了数字元素的列表，那么上面的命令将会返回升序排列的一个列表
     * @param key
     * @return
     */
    @Override
    public List<String> sort(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sort(key);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public List<String> sort(String key, SortingParams sortingParameters) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sort(key, sortingParameters);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Long sort(String key, SortingParams sortingParameters, String dstkey) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sort(key, sortingParameters, dstkey);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Long sort(String key, String dstkey) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sort(key, dstkey);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }


    /**
     * 只提供了 key 参数，那么返回集合中的一个随机元素
     * @param key
     * @return
     */
    @Override
    public String srandmember(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.srandmember(key);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 如果 count 为正数，且小于集合基数，那么命令返回一个包含 count 个元素的数组，数组中的元素各不相同
     * 如果 count 大于等于集合基数，那么返回整个集合
     * 如果 count 为负数，那么命令返回一个数组，数组中的元素可能会重复出现多次，而数组的长度为 count 的绝对值
     * @param key
     * @param count
     * @return
     */
    @Override
    public List<String> srandmember(String key,int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.srandmember(key,count);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     *移除集合中的一个或多个成员元素，不存在的成员元素会被忽略。
     * 当 key 不是集合类型，返回一个错误
     * @param key
     * @param members
     * @return 被成功移除的元素的数量，不包括被忽略的元素
     */
    @Override
    public Long srem(String key, String... members) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.srem(key, members);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * getrange替代返回key对应的字符串value的子串,这个子串是由start和end位移决定的(两者都在string内)
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Override
    @Deprecated
    public String substr(String key, int start, int end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.substr(key, start, end);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 返回 key 所储存的值的类型
     * @param key
     * @return none(key不存在),string (字符串),list (列表),set (集合),zset (有序集),hash (哈希表)
     */
    @Override
    public String type(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.type(key);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**Sorted Set 有序集合 **/

    /**
     *将一个元素及其分数值加入到有序集当中
     * 如果某个成员已经是有序集的成员，那么更新这个成员的分数值，并通过重新插入这个成员元素，来保证该成员在正确的位置上
     * 分数值可以是整数值或双精度浮点数,当 key 存在但不是有序集类型时，返回一个错误。
     * @param key
     * @param score
     * @param member
     * @return  被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
     */
    @Override
    public Long zadd(String key, double score, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zadd(key, score, member);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 将多个元素及其分数值加入到有序集当中
     * @param key
     * @param scoreMembers
     * @return
     */
    @Override
    public Long zadd(String key, Map<String, Double> scoreMembers) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zadd(key, scoreMembers);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 返回在指定的键存储在有序集合中的元素的数量
     * @param key
     * @return 返回整数，有序集合元素的数量，或者如果键不存在则返回0
     */
    @Override
    public Long zcard(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zcard(key);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 计算有序集合中指定分数区间的成员数量
     * @param key
     * @param min
     * @param max
     * @return 分数值在 min 和 max 之间的成员的数量
     */
    @Override
    public Long zcount(String key, double min, double max) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zcount(key, min, max);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Long zcount(String key, String min, String max) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zcount(key, min, max);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 为有序集 key 的成员 member 的 score 值加上增量 increment
     * 当 key 不是有序集类型时，返回一个错误。
     * score 值可以是整数值或双精度浮点数。
     * @param key
     * @param score
     * @param member
     * @return member 成员的新 score 值
     */
    @Override
    public Double zincrby(String key, double score, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zincrby(key, score, member);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 返回有序集 key 中，指定区间内的成员
     * 其中成员的位置按 score 值递增(从小到大)来排序
     * 以 0 表示有序集第一个成员,以 -1 表示最后一个成员
     * 超出范围的下标并不会引起错误
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Override
    public Set<String> zrange(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrange(key, start, end);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员
     * 有序集成员按 score 值递增(从小到大)次序排列。
     * @param key
     * @param min
     * @param max
     * @return
     */
    @Override
    public Set<String> zrangeByScore(String key, double min, double max) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByScore(key, min, max);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     *
     * @param key
     * @param min    最小分数
     * @param max    最大分数
     * @param offset 返回启示偏移量
     * @param count  返回个数
     * @return
     */
    @Override
    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByScore(key, min, max, offset, count);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByScore(key, min, max);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByScore(key, min, max, offset, count);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByScoreWithScores(key, min, max);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByScoreWithScores(key, min, max);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Override
    public Set<Tuple> zrangeWithScores(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeWithScores(key, start, end);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 返回有序集中指定成员的排名。其中有序集成员按分数值递增(从小到大)顺序排列(从0开始)。
     * @param key
     * @param member
     * @return  如果成员是有序集 key 的成员，返回 member 的排名。 如果成员不是有序集 key 的成员，返回 null 。
     */
    @Override
    public Long zrank(String key, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrank(key, member);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 移除有序集中的一个或多个成员，不存在的成员将被忽略。
     * 当 key 存在但不是有序集类型时，返回一个错误
     * @param key
     * @param members
     * @return 被成功移除的成员的数量，不包括被忽略的成员
     */
    @Override
    public Long zrem(String key, String... members) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrem(key, members);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 删除有序集合保存在key开始和结束的排序所有元素
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Override
    public Long zremrangeByRank(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zremrangeByRank(key, start, end);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Long zremrangeByScore(String key, double start, double end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zremrangeByScore(key, start, end);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Long zremrangeByScore(String key, String start, String end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zremrangeByScore(key, start, end);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Set<String> zrevrange(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeByScore(key, max, min);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeByScore(key, max, min, offset, count);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeByScore(key, max, min);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeByScore(key, max, min, offset, count);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeByScoreWithScores(key, max, min);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeByScoreWithScores(key, max, min);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeWithScores(key, start, end);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 返回有序集中成员的排名。其中有序集成员按分数值递减(从大到小)排序
     * 排名以 0 为底，也就是说， 分数值最大的成员排名为 0
     * @param key
     * @param member
     * @return
     */
    @Override
    public Long zrevrank(String key, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrank(key, member);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }

    /**
     *返回有序集中，成员的分数值,如果成员元素不是有序集 key 的成员，或 key 不存在，返回 null
     * @param key
     * @param member
     * @return
     */
    @Override
    public Double zscore(String key, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zscore(key, member);
        } catch (Exception e) {
            if (config.getDebug()) {
                log.info(e.getMessage());
            }
            throw e;
        }
    }
}
