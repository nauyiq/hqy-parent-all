package com.hqy.fundation.cache.redis;

import com.hqy.fundation.cache.exception.RedisException;
import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/2 17:56
 */
@Slf4j
@SuppressWarnings("rawtypes")
public class LettuceStringRedis extends AbstractRedisAdaptor {

    private LettuceStringRedis(RedisTemplate redisTemplate) {
        super(redisTemplate);
    }

    private static volatile LettuceStringRedis instance = null;


    public static LettuceStringRedis getInstance() {
        if (Objects.isNull(instance)) {
            synchronized (LettuceStringRedis.class) {
                if (Objects.isNull(instance)) {
                    RedisTemplate template = SpringContextHolder.getBean(StringRedisTemplate.class);
                    instance = new LettuceStringRedis(template);
                }
            }
        }
        return instance;
    }




    @Override
    public AbstractRedisAdaptor selectDb(int db) {
        if (db < 0 || db > 15) {
            db = 0;
        }
        RedisTemplate redisTemplate = getRedisTemplate();
        LettuceConnectionFactory lettuceConnectionFactory = (LettuceConnectionFactory) redisTemplate.getConnectionFactory();
        if (Objects.isNull(lettuceConnectionFactory)) {
            throw new RedisException("@@@ Initialize redisTemplate failure, get redisTemplateConnectionFactory failure.");
        }
        try {
            lettuceConnectionFactory.setDatabase(db);
            super.getRedisTemplate().setConnectionFactory(lettuceConnectionFactory);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return this;
    }
}
