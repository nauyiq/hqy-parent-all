package com.hqy.rpc.thrift;

import com.facebook.nifty.client.NettyClientConfig;
import com.facebook.nifty.client.NiftyClient;
import com.facebook.swift.codec.ThriftCodecManager;
import com.facebook.swift.service.ThriftClientEventHandler;
import com.facebook.swift.service.ThriftClientManager;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * 单利类 实现一个进程中NIO客户端复用。 提升性能，减少不必要的Netty客户端
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-18 10:44
 */
@Slf4j
public class MultiplexThriftClientManager {

    private final ThriftClientManager clientManager;
//    private ThriftClientEventHandler eventHandler = null;
    private static final int workerThreadCount  = Runtime.getRuntime().availableProcessors() * 4;

    private MultiplexThriftClientManager() {
        Set<ThriftClientEventHandler> eventHandlers = new HashSet<>();
        //NIO线路复用！！！！
        ThriftCodecManager codecManager = new ThriftCodecManager();
        NettyClientConfig clientConfig = NettyClientConfig.newBuilder().setWorkerThreadCount(workerThreadCount).build();
        log.info("### workerThreadCount={}", workerThreadCount);
        NiftyClient client = new NiftyClient(clientConfig);
        //NIO线路复用！
        this.clientManager = new ThriftClientManager(codecManager, client, eventHandlers);
    }

    private static final MultiplexThriftClientManager instance = new MultiplexThriftClientManager();

    public static MultiplexThriftClientManager getInstance() {
        return instance;
    }

    public static ThriftClientManager getThriftClientManager() {
        return instance.clientManager;
    }

}