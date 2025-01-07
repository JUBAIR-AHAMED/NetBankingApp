package com.netbanking.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

public class CORSFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("CORSFilter is being applied");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        

        
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "*");
        httpResponse.setHeader("Access-Control-Allow-Headers", "*");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "false");
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
        	chain.doFilter(request, response);
        	return;
        }
//        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
//        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        chain.doFilter(request, response);
    }


    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}
