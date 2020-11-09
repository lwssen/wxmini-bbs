package com.sss.sssforum.config.redisconfig;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redisTemplate封装
 *
 * @author yinxp@dist.com.cn
 */
@Service
public class RedisUtil<T> {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            if (value instanceof Long) {
                operations.set(key, (Long) value);
            } else {
                operations.set(key, value);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据key，将value++
     *
     * @param key
     * @return
     */
    public boolean increment(final String key) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            Object value = operations.get(key);
            System.out.println(value);
//            Long v = Long.valueOf(value+"");
//            System.out.println(v);
            operations.increment(key, 2);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 写入缓存设置时效时间
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime, TimeUnit timeUnit) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, timeUnit);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key
     *
     * @param pattern
     */
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Object get(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }

    /**
     * 哈希 添加
     *
     * @param key
     * @param hashKey
     * @param value
     */
    public void hmSet(String key, Object hashKey, Object value) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key, hashKey, value);
    }

    /**
     * 哈希的value自增
     *
     * @param key
     * @param hashKey
     */
    public Boolean hmSetIncrement(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        //如果不存在则创建key
        if (!hash.hasKey(key, hashKey)) {
            hmSet(key, hashKey, "0");
        }
        Long increment = hash.increment(key, hashKey, 1);
        if (increment > 0L) {
            return true;
        }
        return false;
    }

    /**
     * 哈希获取数据
     *
     * @param key
     * @param hashKey
     * @return
     */
    public Object hmGet(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.get(key, hashKey);
    }

    public List<Object> hmValueList(String key) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.values(key);
    }

    public Set<Object> hmKeyList(String key) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.keys(key);
    }

    public Map<String, Object> hmMap(String key) {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        return hash.entries(key);
    }

    /**
     * 判断某个haskey是否存在
     *
     * @param key
     * @param hashKey
     * @return boolean
     * @author lws
     **/
    public boolean hcheckKey(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.hasKey(key, hashKey);
    }



    //======================list 数据类型API操作 start==============================================
    /**
     * 列表头部添加
     *
     * @param k
     * @param v
     */
    public Boolean lPush(String k, Object v) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        Long push = list.leftPush(k, v);
        return push >0;
    }
    public Boolean lPushAll(String k, Collection v) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        Long push = list.leftPushAll(k, v);
        return push >0;
    }

    /**
     * 列表尾部部添加
     *
     * @param k
     * @param v
     */
    public Boolean rPush(String k, Object v) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        Long push = list.rightPush(k, v);
        return push >0;
    }
    public Boolean rPushAll(String k, Collection v) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        Long push = list.rightPushAll(k, v);
        return push >0;
    }

    /**
     * 根据索引修改已存在的值
     *
     * @param key 键名称
     * @param index 索引
     * @param value 新的值
     */
    public void lset(String key, Long index,Object value) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.set(key,index,value);
    }
    /**
     * 删除已存在的值
     *
     * @param key 键名称
     * @param count 删除的个数
     * @param value 删除的值
     */
    public Long lrem(String key, Long count,Object value) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        Long remove = list.remove(key, count, value);
        return remove;
    }

    /**
     * 列表获取
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Object lRange(String key, int start, int end) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.range(key, start, end);
    }
    /**
     * 获取list的长度
     *
     * @param key
     * @return java.lang.Object
     * @author lws
     * @date 2020/9/3 17:34
    **/
    public Long lLength(String key) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.size(key);
    }
    //======================list 数据类型API操作 start==============================================






    /**
     * 集合添加
     *
     * @param key
     * @param value
     */
    public void add(String key, Object value) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key, value);
    }

    /**
     * 集合获取
     *
     * @param key
     * @return
     */
    public Object setMembers(String key) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    /**
     * 有序集合添加
     *
     * @param key
     * @param value
     * @param scoure
     */
    public void zAdd(String key, Object value, double scoure) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        zset.add(key, value, scoure);
    }

    /**
     * 有序集合获取
     *
     * @param key
     * @param scoure
     * @param scoure1
     * @return
     */
    public Set<Object> rangeByScore(String key, double scoure, double scoure1) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.rangeByScore(key, scoure, scoure1);
    }
    /**
     * setbit 设置一个值
     *
     * @param key
     * @param offset
     * @param value
     */
    public void setBit(String key, Long offset, Boolean value) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("redis key is null");
        }
        redisTemplate.execute((RedisCallback<Boolean>) con -> con.setBit(key.getBytes(),offset , value));
    }

    /**
     * getBit
     * 获取指定offset的值
     *
     * @param key
     * @param offset
     * @return
     */

    public Boolean getBit(String key, Integer offset) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("redis key is null");
        }
        return (Boolean) redisTemplate.execute((RedisCallback<Boolean>) con -> con.getBit(key.getBytes(), offset));

    }

    /**
     * bitcount
     * @param key
     * @return
     */
    public Integer bitCount(String key) {
        Integer execute = (Integer) redisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes()));
        return execute;
    }

    public Long bitOp(RedisStringCommands.BitOperation op, String saveKey, String... desKey) {
        byte[][] bytes = new byte[desKey.length][];
        for (int i = 0; i < desKey.length; i++) {
            bytes[i] = desKey[i].getBytes();
        }
        return (Long) redisTemplate.execute((RedisCallback<Long>) con -> con.bitOp(op, saveKey.getBytes(), bytes));
    }


    public void addMap(String key, Map<String, Object> map) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.putAll(key, map);
    }

    public Map<String, String> resultMap(String key) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        return hashOperations.entries(key);
    }

    public List<String> reslutMapList(String key) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        return hashOperations.values(key);
    }

    public Set<String> resultMapSet(String key) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        return hashOperations.keys(key);
    }

    public Object resultMapItemValue(String key1, String key2) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        return hashOperations.get(key1, key2);
    }

    /**
     * 创建锁
     *
     * @param key         锁的Key
     * @param value       值(随便写毫无意义)
     * @param releaseTime 锁过期时间 防止死锁
     * @return
     */
    public boolean lock(String key, int value, long releaseTime) {
        // 尝试获取锁
        Boolean boo = redisTemplate.opsForValue().setIfAbsent(key, value, releaseTime, TimeUnit.SECONDS);
        // 判断结果
        return boo != null && boo;
    }

    /**
     * 创建锁
     *
     * @param key         锁的Key
     * @param releaseTime 锁过期时间 防止死锁
     * @return
     */
    public boolean lock(String key, long releaseTime) {
        // 判断结果
        return lock(key, 0, releaseTime);
    }

    /**
     * 根据key'删除锁
     *
     * @param key
     */
    public void unLock(String key) {
        // 删除key即可释放锁
        redisTemplate.delete(key);
    }

    public List<T> getCacheList(String key, Class<T> className) {
        if (this.exists(key)) {
            String cacheStr = (String) this.get(key);
            return JSONArray.parseArray(cacheStr, className);
        } else {
            return null;
        }
    }

    public T getCacheObject(String key, Class<T> className) {
        if (this.exists(key)) {
            String cacheStr = (String) this.get(key);
            return JSONObject.parseObject(cacheStr, className);
        } else {
            return null;
        }
    }
}
