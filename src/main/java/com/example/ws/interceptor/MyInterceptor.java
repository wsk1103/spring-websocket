package com.example.ws.interceptor;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

/**
 * @author sk
 * @time 2022/10/14
 * @desc say
 **/
@Component
@Slf4j
public class MyInterceptor implements HandshakeInterceptor {

    /**
     * 握手前
     *
     * @param request
     * @param response
     * @param wsHandler
     * @param attributes
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        log.info("HandshakeInterceptor beforeHandshake start...");
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
            String authorization = req.getHeader("Sec-WebSocket-Protocol");
            log.info("authorization = {}", authorization);
            if (Objects.isNull(authorization)) {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                log.info("【beforeHandshake】 authorization Parse failure. authorization = {}", authorization);
                return false;
            }
            //存入数据，方便在hander中获取，这里只是在方便在webSocket中存储了数据，并不是在正常的httpSession中存储，想要在平时使用的session中获得这里的数据，需要使用session 来存储一下
//            attributes.put(MagicCode.WEBSOCKET_USER_ID, userId);
//            attributes.put(MagicCode.WEBSOCKET_CREATED, System.currentTimeMillis());
            attributes.put("token", authorization);
            attributes.put(HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME, req.getSession().getId());
            log.info("【beforeHandshake】 WEBSOCKET_INFO_MAP: {}", attributes);
        }
        log.info("HandshakeInterceptor beforeHandshake end...");
        return true;
    }

    /**
     * 握手后
     *
     * @param request
     * @param response
     * @param wsHandler
     * @param exception
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        log.info("HandshakeInterceptor afterHandshake start...");
        HttpServletRequest httpRequest = ((ServletServerHttpRequest) request).getServletRequest();
        HttpServletResponse httpResponse = ((ServletServerHttpResponse) response).getServletResponse();
        if (StrUtil.isNotEmpty(httpRequest.getHeader("Sec-WebSocket-Protocol"))) {
            httpResponse.addHeader("Sec-WebSocket-Protocol", httpRequest.getHeader("Sec-WebSocket-Protocol"));
        }
        log.info("HandshakeInterceptor afterHandshake end...");
    }

}
