package com.hqy.gateway.config;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.hqy.base.common.result.CommonResultCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.List;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/25 15:36
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({ServerProperties.class, ResourceProperties.class})
public class GatewayExceptionConfiguration {

    private final ServerProperties serverProperties;

    private final ApplicationContext applicationContext;

    private final ResourceProperties resourceProperties;

    private final List<ViewResolver> viewResolvers;

    private final ServerCodecConfigurer serverCodecConfigurer;


    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes) {
        GatewayResponseExceptionHandler exceptionHandler = new GatewayResponseExceptionHandler(
                errorAttributes,
                this.resourceProperties,
                this.serverProperties.getError(),
                this.applicationContext);
        exceptionHandler.setViewResolvers(this.viewResolvers);
        exceptionHandler.setMessageWriters(this.serverCodecConfigurer.getWriters());
        exceptionHandler.setMessageReaders(this.serverCodecConfigurer.getReaders());
        return exceptionHandler;
    }


    private static final class GatewayResponseExceptionHandler extends DefaultErrorWebExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(GatewayResponseExceptionHandler.class);

        /**
         * Create a new {@code DefaultErrorWebExceptionHandler} instance.
         * @param errorAttributes    the error attributes
         * @param resourceProperties the resources configuration properties
         * @param errorProperties    the error configuration properties
         * @param applicationContext the current application context
         */
        public GatewayResponseExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ErrorProperties errorProperties, ApplicationContext applicationContext) {
            super(errorAttributes, resourceProperties, errorProperties, applicationContext);
        }

        @Override
        protected Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
            Throwable error = super.getError(request);
            if (error instanceof BlockException) {
                return BeanUtil.beanToMap(CommonResultCode.messageResponse(CommonResultCode.INTERFACE_LIMITED));
            }
            log.error(error.getMessage(), error);
            return BeanUtil.beanToMap(CommonResultCode.messageResponse(CommonResultCode.SYSTEM_ERROR));
        }


        @Override
        protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
            return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
        }


        @Override
        protected int getHttpStatus(Map<String, Object> errorAttributes) {
            Object code = errorAttributes.get("code");
            if (code instanceof Integer) {
                if (code.equals(CommonResultCode.INTERFACE_LIMITED.code)) {
                    return HttpStatus.FORBIDDEN.value();
                }
            }
            return  HttpStatus.INTERNAL_SERVER_ERROR.value();
        }

    }



}
