package cn.seppel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by Ldlood on 2017/9/3.
 */
@Component
@Slf4j
public class RedisLock {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @Description: 加锁
     * @Param: key
     * @Param: value 当前时间+超时时间
     * @return: boolean
     * @Author: Huangsp
     */
    public boolean lock(String key, String value) {
        if (stringRedisTemplate.opsForValue().setIfAbsent(key, value)) {
            return true;
        }

        String currentValue = stringRedisTemplate.opsForValue().get(key);
        //如果锁过期
        if (StringUtils.hasText(currentValue) && Long.parseLong(currentValue) < System.currentTimeMillis()) {

            //获取上一个锁的时间
            String oldValue = stringRedisTemplate.opsForValue().getAndSet(key, value);
            if (StringUtils.hasText(oldValue) && oldValue.equalsIgnoreCase(currentValue)) {
                return true;
            }
        }
        return false;
    }

    public void unlock(String key, String value) {
        String currentVaule = stringRedisTemplate.opsForValue().get(key);
        try {
            if (StringUtils.hasText(currentVaule) && currentVaule.equalsIgnoreCase(value)) {
                stringRedisTemplate.opsForValue().getOperations().delete(key);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

    }
}
