package com.hqy.order.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.mq.common.MessageQueueType;
import com.hqy.mq.common.transaction.service.impl.AbstractMessageTransactionRecordService;
import com.hqy.order.common.entity.OrderMessageRecord;
import com.hqy.order.dao.OrderMessageRecordDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/23 9:50
 */
@Service
public class RabbitmqMessageTransactionRecordServiceImpl extends AbstractMessageTransactionRecordService<OrderMessageRecord> {

    @Resource
    private OrderMessageRecordDao messageRecordDao;


    @Override
    public BaseDao<OrderMessageRecord, Long> registryDao() {
        return messageRecordDao;
    }

    @Override
    public MessageQueueType registryQueueType() {
        return MessageQueueType.RABBITMQ;
    }
}
