package com.daniel.taskspringcore.web;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(1)
public class TransactionLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String transactionId = UUID.randomUUID().toString();
        MDC.put(ApiConstants.TRANSACTION_ID_MDC_KEY, transactionId);
        response.setHeader(ApiConstants.TRANSACTION_ID_HEADER, transactionId);

        String endpoint = request.getMethod() + " " + request.getRequestURI();
        String query = request.getQueryString();
        log.info("Incoming REST call: {}{}", endpoint, query != null ? "?" + query : "");

        try {
            filterChain.doFilter(request, response);
            log.info("Completed REST call: {} -> {}", endpoint, response.getStatus());
        } catch (Exception ex) {
            log.error("Failed REST call: {} -> {} ({})", endpoint, response.getStatus(), ex.getMessage());
            throw ex;
        } finally {
            MDC.remove(ApiConstants.TRANSACTION_ID_MDC_KEY);
        }
    }
}
