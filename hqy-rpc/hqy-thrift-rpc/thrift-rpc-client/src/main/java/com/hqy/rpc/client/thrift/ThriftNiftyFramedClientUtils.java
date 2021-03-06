package com.hqy.rpc.client.thrift;

import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.nifty.client.NiftyClientChannel;
import com.google.common.net.HostAndPort;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.base.common.base.lang.exception.RpcException;
import com.hqy.rpc.client.thrift.support.ThriftClientManagerWrapper;
import com.hqy.rpc.common.RPCServerAddress;
import com.hqy.util.AssertUtil;
import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author qiyuan.hong
 * @date 2022-07-06 23:37
 */
public class ThriftNiftyFramedClientUtils {

    private static final Logger log = LoggerFactory.getLogger(ThriftNiftyFramedClientUtils.class);

    @SuppressWarnings("all")
    public static FramedClientConnector createFramedClientConnector(RPCServerAddress address) {
        try {
            HostAndPort hostAndPort = HostAndPort.fromParts(address.getHostAddr(), address.getPort());
            return new FramedClientConnector(hostAndPort);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new RpcException("Create framedClientConnector failed.");
        }
    }

    public static String printfChannelInfo(NiftyClientChannel clientChannel) {
        if (Objects.isNull(clientChannel)) {
            return StringConstants.EMPTY;
        }
        Channel nettyChannel = clientChannel.getNettyChannel();
        return String.format("(%s -> %s)", nettyChannel.getLocalAddress(), nettyChannel.getRemoteAddress());
    }

}
