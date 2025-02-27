package org.example.expert.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

public class LogFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain filterChain
    ) throws IOException, ServletException
    {
        ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

        request.setAttribute("cachingRequest", cachingRequest);
        request.setAttribute("cachingResponse", cachingResponse);

        cachingResponse.copyBodyToResponse();

        filterChain.doFilter(cachingRequest, cachingResponse);
    }
}
