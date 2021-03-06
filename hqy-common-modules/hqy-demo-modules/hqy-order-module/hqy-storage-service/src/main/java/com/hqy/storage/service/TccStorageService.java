package com.hqy.storage.service;

import com.hqy.common.entity.storage.Storage;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/29 15:11
 */
@LocalTCC
public interface TccStorageService {



    /**
     * 定义两阶段提交
     * name = 该tcc的bean名称,全局唯一
     * commitMethod = commit 为二阶段确认方法
     * rollbackMethod = rollback 为二阶段取消方法
     * BusinessActionContextParameter注解 传递参数到二阶段中
     * @param beforeStorage 修改之前的库存实例
     * @param afterStorage  修改之后的库存实例
     * @return 是否成功
     */
    @TwoPhaseBusinessAction(name = "modifyStorage", commitMethod = "commitTcc", rollbackMethod = "cancel")
    boolean modifyStorage( @BusinessActionContextParameter(paramName = "beforeStorage") Storage beforeStorage,
                           @BusinessActionContextParameter(paramName = "afterStorage") Storage afterStorage);


    /**
     * 确认方法、可以另命名，但要保证与commitMethod一致
     * context可以传递try方法的参数
     * 参数是固定的, 不可以增加或减少,
     * 返回true表示成功 返回false seata会默认不断重试 直到成功为止
     * @param context
     * @return
     */
    boolean commitTcc(BusinessActionContext context);


    /**
     * 二阶段取消方法
     * 参数是固定的, 不可以增加或减少
     * 返回true表示成功 返回false seata会默认不断重试 直到成功为止
     * @param context
     * @return
     */
    boolean cancel(BusinessActionContext context);


}
