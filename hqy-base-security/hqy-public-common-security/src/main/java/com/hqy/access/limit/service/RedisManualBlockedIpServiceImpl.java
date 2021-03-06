package com.hqy.access.limit.service;

import com.hqy.foundation.limit.service.ManualBlockedIpService;
import com.hqy.fundation.cache.redis.LettuceStringRedis;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import com.hqy.util.thread.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

/**
 * 基于redis ip人工 黑名单 服务
 * @author qy
 * @date 2021-08-02 10:51
 */
@Slf4j
@Component
public class RedisManualBlockedIpServiceImpl implements ManualBlockedIpService {

    /**
     * 过期时间集合
     */
    private static final Map<String, Long> TIMESTAMP_MAP = new ConcurrentHashMap<>();

    /**
     * 内存存放被人工指定限制的ip
     */
    private static final Set<String> SET_CACHE = new CopyOnWriteArraySet<>();

    /**
     * 定时load redis数据到内存中, 减少网络请求 提高效率
     */
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE
            = new ScheduledThreadPoolExecutor(1, new DefaultThreadFactory("ManualBlock"));


    public RedisManualBlockedIpServiceImpl() {
        long delay = 3 * 60;
        long period = 15 * 60;
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    SET_CACHE.clear();
                    SET_CACHE.addAll(getAllBlockIpSet());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }, delay, period, TimeUnit.SECONDS);
    }


    @Override
    public void addBlockIp(String ip, int blockSeconds) {
        ip = ip.trim();
        SET_CACHE.add(ip);
        TIMESTAMP_MAP.put(ip, System.currentTimeMillis() + blockSeconds * 1000L);
        LettuceStringRedis.getInstance().sAdd(ProjectContextInfo.MANUAL_BLOCKED_IP_KEY, blockSeconds, TimeUnit.SECONDS, ip);
    }

    @Override
    public void removeBlockIp(String ip) {
        LettuceStringRedis.getInstance().sRem(ProjectContextInfo.MANUAL_BLOCKED_IP_KEY, ip);
        SET_CACHE.remove(ip);
        TIMESTAMP_MAP.remove(ip);
    }

    @Override
    public void clearAllBlockIp() {
        LettuceStringRedis.getInstance().del(ProjectContextInfo.MANUAL_BLOCKED_IP_KEY);
        SET_CACHE.clear();
        TIMESTAMP_MAP.clear();
    }

    @Override
    public Set<String> getAllBlockIpSet() {
        Set<String> attributeSetString =
                SpringContextHolder.getProjectContextInfo().getAttributeSetString(ProjectContextInfo.MANUAL_BLOCKED_IP_KEY);
        if (CollectionUtils.isNotEmpty(attributeSetString)) {
            SET_CACHE.addAll(attributeSetString);
        }

        Set<String> ips = LettuceStringRedis.getInstance().sMembers(ProjectContextInfo.MANUAL_BLOCKED_IP_KEY);
        if (CollectionUtils.isEmpty(ips)) {
            return SET_CACHE;
        } else {
            ips.addAll(SET_CACHE);
            long now = System.currentTimeMillis();
            boolean foundTimeoutItems = false;
            //判断是否存在过期的ip
            List<String> removeString = new ArrayList<>();
            for (String ip : ips) {
                if (TIMESTAMP_MAP.containsKey(ip)) {
                    if (now > TIMESTAMP_MAP.get(ip)) {
                        TIMESTAMP_MAP.remove(ip);
                        removeString.add(ip);
                        foundTimeoutItems = true;
                    }
                }
            }
            if (foundTimeoutItems) {
                //移除过期的ip
                LettuceStringRedis.getInstance().sRem(ProjectContextInfo.MANUAL_BLOCKED_IP_KEY, removeString.toArray(new String[0]));
                //重新获取ip集合
                ips = LettuceStringRedis.getInstance().sMembers(ProjectContextInfo.MANUAL_BLOCKED_IP_KEY);
            }
            return ips;
        }

    }

    @Override
    public boolean isBlockIp(String ip) {
        ip = ip.trim();
        boolean blocked = SET_CACHE.contains(ip);
        if (blocked) {
            if (!LettuceStringRedis.getInstance().sIsMember(ProjectContextInfo.MANUAL_BLOCKED_IP_KEY, ip)) {
                TIMESTAMP_MAP.remove(ip);
                SET_CACHE.remove(ip);
                return false;
            } else {
                //如果包含，有可能是已经过期了，受限于redis，被识别为没有过期
                if (TIMESTAMP_MAP.containsKey(ip)) {
                    if (System.currentTimeMillis() > TIMESTAMP_MAP.get(ip)) {
                        TIMESTAMP_MAP.remove(ip);
                        SET_CACHE.remove(ip);
                        LettuceStringRedis.getInstance().sRem(ProjectContextInfo.MANUAL_BLOCKED_IP_KEY, ip);
                    }
                }
            }
        }
        return blocked;
    }


}
