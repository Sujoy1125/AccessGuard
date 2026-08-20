package com.accessguard.accessguard.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        long start = System.currentTimeMillis();
        String method = request.getMethod();
        String uri = request.getRequestURI();

        try {
            chain.doFilter(request, response);
        } finally {
            long durationMs = System.currentTimeMillis() - start;
            int status = response.getStatus();
            if (status >= 500) {
                log.error("{} {} -> {} ({} ms)", method, uri, status, durationMs);
            } else if (status >= 400) {
                log.warn("{} {} -> {} ({} ms)", method, uri, status, durationMs);
            } else {
                log.info("{} {} -> {} ({} ms)", method, uri, status, durationMs);
            }
        }
    }
}