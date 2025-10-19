package com.example.trx.support.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;


public class RequestServletFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest wrappedRequest=new RequestServletWrapper((HttpServletRequest) servletRequest);
        filterChain.doFilter(wrappedRequest,servletResponse);
    }
}
