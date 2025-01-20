package com.netbanking.filter;

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
import com.netbanking.util.Redis;
import com.netbanking.util.UserDetailsLocal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
        // Exclude login endpoints from authentication
        if (path.equals("/login")) {
        	System.out.println("login");
            chain.doFilter(request, response);
            return;
        }
        
        String token = httpRequest.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("{\"status\":false,\"message\":\"Authorization token is required.\"}");
            return;
        }
        Claims claims = null;
        try {
        	token = token.replace("Bearer ", "");
        	claims = Jwts.parser()
        			.setSigningKey(SECRET_KEY)
        			.parseClaimsJws(token)
        			.getBody();
        } catch (ExpiredJwtException ex) {
        	setResponse(httpResponse, HttpServletResponse.SC_OK, HttpServletResponse.SC_UNAUTHORIZED, "Expired token.");
        	return;
        } catch (Exception e) {
        	setResponse(httpResponse, HttpServletResponse.SC_OK, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
		}
        Long userId = claims.get("userId", Long.class);
        String role = claims.get("role", String.class);
        Long branchId = claims.get("branchId", Long.class);
        if(!(Redis.get(userId.toString()).equals(token))) {
        	setResponse(httpResponse, HttpServletResponse.SC_OK, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
        	return;
        }
        
        UserDetailsLocal store = UserDetailsLocal.get();
        store.setUserId(userId);
        store.setRole(role);
        store.setBranchId(branchId);
        
        // Add claims to request attributes for further use
        httpRequest.setAttribute("userId", userId);
        httpRequest.setAttribute("role", role);
        httpRequest.setAttribute("branchId", branchId);
        httpRequest.setAttribute("jwt", token);
        
        String method = httpRequest.getHeader("action") != null? httpRequest.getHeader("action") : httpRequest.getMethod();
        if ((userId==null||role==null) ||
        	((role.equals("MANAGER")||role.equals("EMPLOYEE"))&&branchId==null)) {
        	setResponse(httpResponse, HttpServletResponse.SC_OK, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
        	return;
        }
        if(!UserAccessibleMethods.isPathPresent(path, method)) {
        	setResponse(httpResponse, HttpServletResponse.SC_OK, HttpServletResponse.SC_NOT_FOUND, "The requested resource was not found on the server.");
        	return;
        }
        if(!UserAccessibleMethods.isMethodPresent(path, method)) {
        	setResponse(httpResponse, HttpServletResponse.SC_OK, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "The requested method is not supported for this endpoint.");
        	return;
        }
    	if(!UserAccessibleMethods.isAuthorized(UserAccessibleMethods.valueOf(role), path, method)) {
    		setResponse(httpResponse, HttpServletResponse.SC_OK, HttpServletResponse.SC_FORBIDDEN, "You are not authorized to perform this action.");
        	return;
    	}
    	System.out.println("passed auth filter.");
        chain.doFilter(request, response);
    }
    
    private void setResponse(HttpServletResponse httpResponse, int serverStatus, int actionStatus, String message) throws IOException {
    	httpResponse.setStatus(serverStatus);
    	httpResponse.setContentType("application/json");
    	httpResponse.setCharacterEncoding("UTF-8");
    	httpResponse.getWriter().write("{\"status\":"+actionStatus+",\"message\":\""+message+"\"}");
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}
