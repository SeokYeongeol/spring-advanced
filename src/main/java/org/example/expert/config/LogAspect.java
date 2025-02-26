package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    @Pointcut("execution(* org.example.expert.domain.user.controller.UserAdminController.*(..)) " +
            "|| execution(* org.example.expert.domain.comment.controller.CommentAdminController.*(..))")
    private void pointCut() { }

    @Around("pointCut()")
    public Object loggingAdminApi(ProceedingJoinPoint joinPoint) throws Throwable {
        ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);

        Long userId = (Long) request.getAttribute("userId");
        LocalDateTime requestTime = LocalDateTime.now();
        String requestUrl = request.getRequestURI();
        String requestBody = getCachedRequestBody(cachingRequest);
        String responseBody = getCachedResponseBody(cachingResponse);

        log.info("\n[RequestLog]\n" +
                "[userId] : {}\n" +
                "[requestTime] : {}\n" +
                "[requestUrl] : {}\n" +
                "[requestBody]\n" +
                "{}",
                userId, requestTime, requestUrl, requestBody);

        Object result = joinPoint.proceed();

        log.info("\n[ResponseLog]\n" +
                 "[userId] : {}\n" +
                 "[responseBody]\n" +
                 "{}",
                userId, responseBody);

        return result;
    }

    private String getCachedRequestBody(ContentCachingRequestWrapper cachingRequest) {
        byte[] content = cachingRequest.getContentAsByteArray();
        if(content.length == 0) {
            return "{}";
        }
        try {
            return new String(content, request.getCharacterEncoding());
        } catch(UnsupportedEncodingException e) {
            throw new InvalidRequestException("요청 로그를 가져오는데 실패했습니다.");
        }
    }

    private String getCachedResponseBody(ContentCachingResponseWrapper cachingResponse) {
        byte[] content = cachingResponse.getContentAsByteArray();
        if(content.length == 0) {
            return "{}";
        }
        try {
            return new String(content, response.getCharacterEncoding());
        } catch(UnsupportedEncodingException e) {
            throw new InvalidRequestException("응답 로그를 가져오는데 실패했습니다.");
        }
    }
}