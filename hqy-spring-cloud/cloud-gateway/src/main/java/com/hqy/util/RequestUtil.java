package com.hqy.util;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LRUCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Flux;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-30 15:02
 */
@Slf4j
public class RequestUtil {


    /**
     * 读取body内容
     * @param serverHttpRequest
     * @return
     */
    public static String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {

        Flux<DataBuffer> body = serverHttpRequest.getBody();
        StringBuilder sb = new StringBuilder();

        body.subscribe(buffer -> {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            String bodyString = new String(bytes, StandardCharsets.UTF_8);
            sb.append(bodyString);
        });

        return formatStr(sb.toString());
    }


    /**
     * 去掉空格,换行和制表符
     * @param str
     * @return
     */
    public static String formatStr(String str){
        if (str != null && str.length() > 0) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            return m.replaceAll("");
        }
        return str;
    }


    /**
     * 是否是静态资源或者html
     * @param url
     * @return
     */
    public static boolean isStaticResourceOrHtml(String url) {
        String urlTempString = url.toLowerCase();
        if (url.contains("?")) {
            urlTempString = urlTempString.substring(0, urlTempString.indexOf('?'));
        }
        if (isStaticResource(urlTempString)) {
            return true;
        } else {
            if (urlTempString.endsWith(".html") || urlTempString.endsWith(".htm")) {
                return true;
            }
        }
        return false;
    }


    private static final LRUCache<String, Boolean> CACHE = CacheUtil.newLRUCache(1024, 60 * 1000 * 10);

    private static final String PREFIX_STATIC_LIKE = "/";

    private static final List<String> staticStrings = new ArrayList<>();
    static {
        staticStrings.add(".js");
        staticStrings.add(".css");
        staticStrings.add(".ttf");
        staticStrings.add(".map");
        staticStrings.add(".png");
        staticStrings.add(".jpg");
        staticStrings.add(".jpeg");
        staticStrings.add(".gif");
        staticStrings.add(".woff");
        staticStrings.add(".otf");
        staticStrings.add("/favicon.ico");
        staticStrings.add("/resources/");
        staticStrings.add("/UploadFiles/");
        staticStrings.add("/files/");
        staticStrings.add("/csp-report");
        staticStrings.add("/fonts/");
    }


    /**
     ** 是否是静态资源请求
     * @param url
     * @return true 表示是静态或者类似静态的资源
     */
    public static boolean isStaticResource(String url) {
        String urlTempString = url.toLowerCase();

        if (url.contains("?")) {
            urlTempString = urlTempString.substring(0, urlTempString.indexOf('?'));
        }
        final String key = "isStaticResource.".concat(urlTempString);

        Boolean flag = CACHE.get(key);
        if (flag == null) {
            try {
                if (!urlTempString.startsWith(PREFIX_STATIC_LIKE)) {
                    // 如果不像是静态资源请求.....
                    URL neturl = new URL(urlTempString);
                    urlTempString = neturl.getPath();
                }
            } catch (MalformedURLException e) {
                log.warn("MalformedURLException: {} , {}", e.getMessage(), url);
            }

            final String uString = urlTempString;
            boolean match = staticStrings.stream()
                    .anyMatch(stPattern -> StringUtils.containsIgnoreCase(uString, stPattern));
            CACHE.put(key, match);
            return match;
        } else {
            return flag;
        }

    }


}