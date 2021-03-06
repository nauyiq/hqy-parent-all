package com.hqy.foundation.limit.service;


import com.hqy.foundation.common.HttpRequestInfo;
import com.hqy.foundation.limit.LimitResult;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-02 20:27
 */
public interface HttpThrottles {

    /**
     * 是否允许本次客户端的请求？
     * 同一ip：每秒允许同一ip请求数. 默认8
     * @param request
     * @return
     */
    LimitResult limitValue(HttpRequestInfo request);

    /**
     * 检查是否是黑客访问.....
     * @param requestParams
     * @param requestIp
     * @param uri
     * @param urlOrQueryString
     * @return
     */
    LimitResult checkHackAccess(String requestParams, String requestIp, String uri, String urlOrQueryString);


    /**
     * 是否是uri白名单
     * @param uri
     * @return
     */
    boolean isWhiteURI(String uri);

    /**
     * 当前ip 是否是人工白名单
     * @param remoteAddr
     * @return
     */
    boolean isManualWhiteIp(String remoteAddr);
}
