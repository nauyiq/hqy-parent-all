package com.hqy.common.service;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.rpc.api.service.RPCService;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 11:09
 */
@ThriftService(value = MicroServiceConstants.DEMO_ORDER_SERVICE)
public interface OrderRemoteService extends RPCService {


    /**
     * 根据订单id 获取订单详情
     * @param orderId 订单id
     * @return
     */
    @ThriftMethod
    String queryOrderById(@ThriftField(1) Long orderId);

    /**
     * 下单
     * @param productId 商品id
     * @param count 数量
     * @param money 金额
     * @return 订单号
     */
    @ThriftMethod
    Long order(@ThriftField(1) Long productId, @ThriftField(2) int count, @ThriftField(3) String money,
               @ThriftField(4) String storageJson, @ThriftField(5) String accountJson);

    /**
     * 修改订单成功状态
     * @param orderNum
     */
    @ThriftMethod
    void updateOrderStateSuccess(@ThriftField(1)Long orderNum);
}
