package com.hqy.access.flow.server;

import com.hqy.access.flow.FlowLimitConfig;
import com.hqy.access.flow.Measurement;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/17 16:39
 */
@Data
@ConfigurationProperties(prefix = "resource.limit")
public class FlowConfigProperties {

    public static final FlowLimitConfig DEFAULT_CONFIG = new FlowLimitConfig(10, Measurement.Seconds.ONE_SECONDS);

    private FlowLimitConfig getLimitConfig = DEFAULT_CONFIG;

    private FlowLimitConfig postLimitConfig = new FlowLimitConfig(5, Measurement.Seconds.ONE_SECONDS);

    private FlowLimitConfig uriLimitConfig = DEFAULT_CONFIG;


}
