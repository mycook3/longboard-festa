package com.example.trx.support.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.trx.support.filter.RequestServletWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    public static final String LOG_ID = "log_id";

    public static final String TRACE_ID = "traceId";

    public ObjectMapper objectMapper;

    public LoggingInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uuid=UUID.randomUUID().toString();
        request.setAttribute(LOG_ID,uuid);
        MDC.put(TRACE_ID,uuid);
        log.info("REQUEST [{}][{}][{}][{}]",request.getMethod(),getClientIp(request),request.getRequestURI(),handler);

        if(request instanceof RequestServletWrapper) {
            try {
                log.info("REQUEST BODY: {}",objectMapper.readValue(request.getInputStream(), Map.class).toString());

            }catch (Exception e){
                log.info("REQUEST BODY: {}",IOUtils.toString(request.getInputStream()));
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String logId=request.getAttribute(LOG_ID).toString();

        log.info("RESPONSE [{}][{}]",request.getMethod(),request.getRequestURI());
        if (ex != null) {
            log.error("afterCompletion!!", ex);
        }
        MDC.remove(LOG_ID);

    }

    private String getClientIp(HttpServletRequest request){
        String ip=request.getHeader("X-Forwarded-For");
        if(ip!=null && !ip.isEmpty() && "unknown".equalsIgnoreCase(ip))
            return ip.split(",")[0];
        return request.getRemoteAddr();
    }
}

