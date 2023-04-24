package com.miniw.common.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis 工具类
 */
@Component
@Slf4j
public class RedisUtil {

    private final long expire = 60 * 60;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取key的过期时间 单位：秒
     */
    public Long getExpire(String key) {
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 设置key的生命周期
     * @param key      key
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    public void expireKey(String key, long timeout, TimeUnit timeUnit) {
        stringRedisTemplate.expire(key, timeout, timeUnit);
    }

    /**
     * 设置key的生命周期
     * @param key      key
     * @param date  过期时间
     */
    public void expireKeyAt(String key, Date date) {
        stringRedisTemplate.expireAt(key, date);
    }

    /**
     * 获取key值内容
     * @param key 键
     */
    public String sGet(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * String 自增长(带过期时间)
     * @param key    key
     * @param expire 过期时间
     */
    public long sIncr(String key, long expire) {
        Long incr = stringRedisTemplate.opsForValue().increment(key);
        if (incr != null && incr == 1 && expire > 0)
            stringRedisTemplate.expire(key, expire, TimeUnit.SECONDS);
        return incr == null ? 0 : incr;

    }

    /**
     * 获取key值内容
     * @param key   key
     * @param clazz 类
     * @param load  重载接口
     * @param <T>   泛型
     * @return <T>
     */
    public <T> T sGet(String key, Class<T> clazz, Load<T> load, long expire) {
        String _str = sGet(key);
        if (StringUtils.isBlank(_str) && load != null) {
            T _t = load.onLoad();
            if (_t != null) {
                sPutExDay(key, _t, expire == 0 ? this.expire : expire);
                return _t;
            }
            return null;
        }
        return StringUtils.isBlank(_str) ? null : CommonUtil.parseObject(_str, clazz);
    }

    /**
     * 获取key值集合
     * @param key   key
     * @param clazz 类
     * @param load  重载接口
     * @param <T>   泛型
     * @return <T>
     */
    public <T> List<T> sGetList(String key, Class<T> clazz, LoadList<T> load, long expire) {
        String _str = sGet(key);
        if (StringUtils.isBlank(_str) && load != null) {
            List<T> _t = load.onLoad();
            if (_t != null && !_t.isEmpty()) {
                sPutExDay(key, _t, expire == 0 ? this.expire : expire);
                return _t;
            }
            return _t;
        }
        return StringUtils.isBlank(_str) ? null : CommonUtil.parseArrayObject(_str, clazz);
    }

    /**
     * 设置key值及过期时间
     *
     * @param key    key
     * @param value  value
     * @param date 过期时间
     */
    public void sPut(String key, Object value, Date date) {

        stringRedisTemplate.executePipelined((RedisCallback<String>) connection ->{
            connection.set(key.getBytes(), JSON.toJSONBytes(value));
            connection.pExpireAt(key.getBytes(), date.getTime());
            return null;
        });
    }


    /**
     * 设置key值
     *
     * @param key    key
     * @param value  value
     * @param expire 过期时间 单位：天
     */
    public void sPutExDay(String key, Object value, long expire) {
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(value));
        expireKey(key, expire, TimeUnit.DAYS);
    }

    /**
     * 删除key
     */
    public boolean sDelete(String key) {
        return stringRedisTemplate.delete(key);
    }

    /**
     * 获取Hash所有给定字段的值
     * @param key       key
     * @param hashKeys  字段
     */
    public List<String> hMultiGet(String key, List<String> hashKeys) {
        if (hashKeys == null || hashKeys.isEmpty())
            return new ArrayList<>();
        HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();
        return operations.multiGet(key, hashKeys);
    }

    /**
     * 递增Hash键指定字段的值和过期时间
     *
     * @param key     key
     * @param hashKey 字段
     * @param delta   增量
     * @param date    日期
     */
    public void hIncrementAndEx(String key, String hashKey, int delta, Date date) {

        stringRedisTemplate.executePipelined((RedisCallback<String>) connection ->{
            connection.hIncrBy(key.getBytes(), hashKey.getBytes(), delta);
            connection.pExpireAt(key.getBytes(), date.getTime());
            return null;
        });

    }

    /**
     * 递增Hash键指定字段的值
     *
     * @param key     key
     * @param hashKey 字段
     * @param delta   增量
     * @return {@link Long}
     */
    public Long hIncrement(String key, String hashKey, int delta) {
        return stringRedisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    /**
     * 递增Hash键指定字段的值
     * @param key       key
     * @param hashKey   字段
     * @param delta     增量
     * @param today     是否当天失效
     */
    public long hIncrement(String key, String hashKey, int delta, boolean today) {
        Long incr = stringRedisTemplate.opsForHash().increment(key, hashKey, delta);
        if (today) {
            if (!CommonUtil.positive(stringRedisTemplate.getExpire(key, TimeUnit.SECONDS)))
                stringRedisTemplate.expireAt(key, CommonUtil.twelve());
        }
        return incr;
    }

    /**
     * 获取Hash键指定的字段值
     * @param key       key
     * @param hashKey   字段
     * @return Object
     */
    public Object hGet(String key, String hashKey) {
        return stringRedisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 获取Hash键指定的字段值
     * @param key       key
     * @param hashKey   字段
     * @param clazz     Class
     * @param load      重载接口
     * @param <T>       泛型
     * @return <T>
     */
    public <T> T hGet(String key, String hashKey, Class<T> clazz, Load<T> load) {
        Object str = stringRedisTemplate.opsForHash().get(key, hashKey);
        if (str == null && load != null) {
            T _t = load.onLoad();
            if (_t != null) {
                hPut(key, hashKey, _t);
                return _t;
            }
        }
        if (str == null)
            return null;
        return CommonUtil.parseObject(str.toString(), clazz);
    }

    /**
     * 获取Hash键指定的字段值
     * @param key       key
     * @param hashKey   字段
     * @return int
     */
    public int hGetInt(String key, String hashKey) {
        Object obj = stringRedisTemplate.opsForHash().get(key, hashKey);
        return obj != null ? Integer.parseInt(String.valueOf(obj)) : 0;
    }

    /**
     * Hash键指定的字段值是否存在
     * @param key       key
     * @param hashKey   字段
     */
    public boolean hHasKey(String key, Object hashKey) {
        return stringRedisTemplate.opsForHash().hasKey(key, String.valueOf(hashKey));
    }

    /**
     * 删除指定的Hash key
     *
     * @param key     关键
     * @param hashKey 散列键
     * @return {@link Long}
     */
    public Long hDelKey(String key, Object hashKey){
        return stringRedisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * 设置Hash键指定字段值
     * @param key       key
     * @param hashKey   字段
     * @param value     value
     */
    public void hPut(String key, Object hashKey, Object value) {
        stringRedisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 设置Hash键指定字段并设置过期时间
     *
     * @param key     key
     * @param hashKey 字段
     * @param value   value
     */
    public void hPutAndEx(String key, Object hashKey, Object value, Date date){
        stringRedisTemplate.opsForHash().put(key, hashKey, value);
        if(Objects.requireNonNull(stringRedisTemplate.getExpire(key, TimeUnit.MILLISECONDS)) == -1 ){
//            stringRedisTemplate.expire(key, date.getTime(), TimeUnit.MILLISECONDS);
            stringRedisTemplate.expireAt(key, date);
        }
    }


    /**
     * 设置集合(Set)键值
     * @param key   key
     * @param value value
     */
    public Long sAdd(String key, Object value) {
        return stringRedisTemplate.opsForSet().add(key, JSON.toJSONString(value));
    }

    /**
     * 获取集合(Set)大小
     * @param key   key
     */
    public Long sSize(String key) {
        return stringRedisTemplate.opsForSet().size(key);
    }

    /**
     * 获取集合length个随机
     * @param key       key
     * @param length    随机数量
     */
    public List<String> sRandMembers(String key, int length) {
        List<String> members = stringRedisTemplate.opsForSet().randomMembers(key, length);
        return members == null ? new ArrayList<>() : members;
    }



    /*list*/
    public List<String> lGet(String key, long start, long end){ return stringRedisTemplate.opsForList().range(key, start, end); }

    /**
     * 从右入队
     *
     * @param key  key
     * @param value 值
     */
    public void lRightPush(String key, Object value) {
        stringRedisTemplate.opsForList().rightPush(key, JSON.toJSONString(value));
    }

    /**
     * 从右移除并返回列表key的头元素
     *
     * @param key  key
     */
    public Object lRightPop(String key){
        return stringRedisTemplate.opsForList().rightPop(key);
    }


    /**
     * 从左入队
     *
     * @param key   key
     * @param value 值
     */
    public void lLeftPush(String key, Object value){
        stringRedisTemplate.opsForList().leftPush(key, JSON.toJSONString(value));
    }


    /**
     * 从左移除并返回列表key的头元素
     *
     * @param key key
     * @return {@link String}
     */
    public String lLeftPop(String key){
        return stringRedisTemplate.opsForList().rightPop(key);
    }


    public void lSetIfPresent(String key, Object value){
        stringRedisTemplate.opsForList().leftPushIfPresent(key, JSON.toJSONString(value));
    }



    public boolean hasKey(String key){
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 获取list的长度
     *
     * @param key key
     * @return long
     */
    public long lGetListSize(String key) { return stringRedisTemplate.opsForList().size(key); }

    /**
     * zSet 删除元素
     * @param key   key
     * @param value 元素
     */
    public Long zDelete(String key, String value) {
        return stringRedisTemplate.opsForZSet().remove(key, value);
    }

    public Boolean delKey(String key) {
        return stringRedisTemplate.delete(key);
    }

    public Object delKeyByPrefix(String key, String prefix) {
        if(StringUtils.isNotBlank(prefix)){
            Set<String> keys = stringRedisTemplate.keys(prefix + "*");
            assert keys != null;
            return stringRedisTemplate.delete(keys);
        }
        return stringRedisTemplate.delete(key);
    }


    public interface Load<T> {
        T onLoad();
    }

    public interface LoadList<T> {
        List<T> onLoad();
    }


    public Cursor<Map.Entry<Object, Object>> hScan(String key, int count) throws IOException, ParseException {
        Cursor<Map.Entry<Object, Object>> cursor = stringRedisTemplate.opsForHash().scan(
                key,
                ScanOptions.scanOptions().count(100).match("*").build()
        );
        return cursor;
    }

}
