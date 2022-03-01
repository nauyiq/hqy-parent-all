package com.hqy.coll.gateway.entity;

import com.hqy.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author qy
 * @date 2021-08-10 11:43
 */
@Table(name = "throttle_ip_block_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ThrottledIpBlock extends BaseEntity<Long> {

    /**
     * 被什么方式节流的
     */
    private String throttleBy;

    /**
     * 请求的客户端ip
     */
    private String ip;

    /**
     * 请求url
     */
    private String url;

    /**
     * request json
     */
    private String accessJson ;

    /**
     * 封禁时间 单位s
     */
    private Integer blockedSeconds;

    /**
     * 所属环境
     */
    private String env;


}