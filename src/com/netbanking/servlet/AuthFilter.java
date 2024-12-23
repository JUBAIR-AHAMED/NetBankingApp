package com.netbanking.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netbanking.enumHelper.UserAccessibleMethods;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

//@WebFilter("/*")
public class AuthFilter implements Filter {
    private static final String SECRET_KEY = "018d7a1625d1d217ffde1629409edbdb889f373aaef7032d6a711d2d40848fef";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
    	System.out.println("Auth filter is being applied.");
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
        String path = httpRequest.getServletPath();
        System.out.println("path"+path);
        System.out.println("method "+httpRequest.getMethod());
        // Exclude login and other public endpoints from authentication
        if (path.equals("/userlogin")) { //|| path.startsWith("/public")
            chain.doFilter(request, response);
            return;
        }

        String token = httpRequest.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("{\"status\":false,\"message\":\"Authorization token is required.\"}");
            return;
        }

        token = token.replace("Bearer ", "");
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            Long userId = claims.get("userId", Long.class);
            String role = claims.get("role", String.class);
            Long branchId = claims.get("branchId", Long.class);
            // Add claims to request attributes for further use
            httpRequest.setAttribute("userId", userId);
            httpRequest.setAttribute("role", role);
            httpRequest.setAttribute("branchId", branchId);
            if(userId==null||role==null) {
            	throw new Exception("Invalid token");
            }
            if((role.equals("MANAGER")||role.equals("EMPLOYEE"))&&branchId==null) {
            	throw new Exception("Invalid token");
            }
        	if(!UserAccessibleMethods.isAuthorized(UserAccessibleMethods.valueOf(role), path, httpRequest.getMethod())) {
        		throw new Exception("Permission denied.");
        	}
            chain.doFilter(request, response); // Continue with the request
        } catch (Exception e) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            String errorMessage = e.getMessage();
            if(e.getMessage().equals("Permission denied.")) {
            	httpResponse.getWriter().write("{\"status\":false,\"message\":\"" + errorMessage + "\"}");
            } else {
            	httpResponse.getWriter().write("{\"status\":false,\"message\":\"Invalid or expired token.\"}");
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}
