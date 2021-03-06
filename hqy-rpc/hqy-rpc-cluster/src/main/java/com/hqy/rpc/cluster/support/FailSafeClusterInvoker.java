package com.hqy.rpc.cluster.support;

import com.hqy.base.common.base.lang.exception.RpcException;
import com.hqy.rpc.api.Invocation;
import com.hqy.rpc.api.Invoker;
import com.hqy.rpc.cluster.directory.Directory;
import com.hqy.rpc.cluster.loadbalance.LoadBalance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * When invoke fails, log the error message and ignore this error by returning an empty Result.
 * Usually used to write audit logs and other operations
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13 14:26
 */
public class FailSafeClusterInvoker<T> extends AbstractClusterInvoker<T> {

    private static final Logger log = LoggerFactory.getLogger(FailSafeClusterInvoker.class);

    public FailSafeClusterInvoker(Directory<T> directory, String hashFactor) {
        super(directory, hashFactor);
    }

    @Override
    protected Object doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadBalance) throws RpcException {
        try {
            checkInvokers(invokers, invocation);
            Invoker<T> invoker = select(loadBalance, invocation, invokers, null);
            return invoker.invoke(invocation);
        } catch (Throwable e) {
            log.error("Failsafe ignore exception: " + e.getMessage(), e);
            return null;
        }
    }
}
