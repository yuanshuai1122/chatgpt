package vip.yuanshuai.chatgpt.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @program: chatgpt
 * @description: redis工具类
 * @author: yuanshuai
 * @create: 2023-07-03 12:36
 **/
public class RedisUtils {


    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    /**
     * 检查是否超过了次数
     *
     * @param key        钥匙
     * @param limit      限制
     * @param expireTime 过期时间
     * @param timeUnit   时间单位
     * @return boolean
     */
    public boolean checkLimit(String key, int limit, long expireTime, TimeUnit timeUnit) {
        String redisKey = "ip:limit:" + key;

        // 使用increment方法来对计数器进行自增
        Long count = redisTemplate.opsForValue().increment(redisKey, 1);

        if (count == 1) {
            // 第一次访问时，设置过期时间
            redisTemplate.expire(redisKey, expireTime, timeUnit);
        }

        // 判断计数是否超过限制
        if (count > limit) {
            return false;
        }

        return true;
    }

}
