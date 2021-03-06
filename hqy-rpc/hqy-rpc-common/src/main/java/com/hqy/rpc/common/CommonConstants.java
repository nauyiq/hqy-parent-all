package com.hqy.rpc.common;

import com.hqy.base.common.base.lang.StringConstants;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 16:33
 */
public interface CommonConstants {

    String REGISTRY_DELAY_NOTIFICATION_KEY = "delay-notification";
    String WARMUP = "warmup";
    String USERNAME = "username";
    String PASSWORD = "password";

    String WEIGHT = "weight";
    String PUB_MODE = "pubMode";
    String START_SERVER_TIMESTAMP = "startServerTimestamp";
    String HASH_FACTOR = "hashFactor";
    String RPC_SERVER_ADDR = "rpcServerAddress";
    String ACTUATOR_TYPE = "actuatorType";

    String DEFAULT_HASH_FACTOR = StringConstants.DEFAULT;
    String DIRECT_SERVICE = "direct-service";

    String EVENT_LOOP_BOSS_POOL_NAME  = "NettyServerBoss";
    String EVENT_LOOP_WORKER_POOL_NAME  = "NettyServerWorker";

    String POOL_MIN_IDLE_PER_KEY = "poolMinIdlePerKey";
    String POOL_MAX_IDLE_PER_KEY = "poolMaxIdlePerKey";
    String POOL_MIN_IDLE = "poolMinIdle";
    String POOL_MAX_IDLE = "poolMaxIdle";
    String POOL_MAX_TOTAL = "poolMaxTotal";

    String RPC_CLUSTER_AVAILABLE_CHECK = "availableCheck";
    String RPC_CLUSTER_STICKY_KEY = "sticky";
    String RPC_CLUSTER_RETRIES_TIMES = "retries";
    String RPC_CLUSTER_FAIL_BACK_TASKS_KEY = "failBackTasks";
    String SEATA_XID = "xid";
    String RPC_CLIENT_WORKER_THREAD_COUNTS = "clientWorkerCount";
    String THRIFT_MONITOR_APPLICATION_NAME = "monitorApplication";

    boolean DEFAULT_RPC_CLUSTER_STICKY = true;
    boolean DEFAULT_RPC_CLUSTER_AVAILABLE_CHECK = true;
    int DEFAULT_DELAY_NOTIFICATION_TIME = 5000;
    int DEFAULT_RECONNECT_TASK_TRY_COUNT = 10;
    int DEFAULT_RECONNECT_TASK_PERIOD = 1000;
    int DEFAULT_WEIGHT = 100;
    int DEFAULT_WARMUP = 5 * 60 * 1000;
    int DEFAULT_FAIL_BACK_TIMES = 3;
    int DEFAULT_RETRIES = 2;
    int DEFAULT_FAIL_BACK_TASKS = 100;

    String NORMAL_RPC = "normal";
    String SLOW_RPC = "slow";
    String ERROR_RPC = "error";
    String EXCEPTION_ORIGIN_CLIENT = "client";
    String EXCEPTION_ORIGIN_SERVICE = "service";
    String MONITOR_SEND_DATA_INTERVAL_KEY = "interval";
    int DEFAULT_MONITOR_SEND_DATA_INTERVAL = 60000;

    String RPC_SLOW_TIME_KEY = "slowRpc";
    long DEFAULT_RPC_SLOW_TIME_MILLIS = 15000;

    String RPC_MONITOR_WARNING_KEY = "maxWarning";
    int DEFAULT_MONITOR_WARNING_KEY = 60;









}
