package com.netbanking.filter;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.netbanking.enumHelper.UserAccessibleMethods;
import com.netbanking.exception.CustomException;
import com.netbanking.util.ErrorHandler;
import com.netbanking.util.Redis;
import com.netbanking.util.UserDetailsLocal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

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
        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        httpResponse.setHeader("Pragma", "no-cache"); // HTTP 1.0
        
        String path = httpRequest.getPathInfo();
        String servletPath = httpRequest.getServletPath();
        System.out.println("path: "+path+" servlet path: "+servletPath);
        

        if (path!=null && path.equals("/login")) {
			chain.doFilter(request, response);
            return;
        }
        
        String token = httpRequest.getHeader("Authorization");
        Claims claims = null;
        try {
        	claims = tokenValidator(token, claims);			
		} catch (Exception e) {
			ErrorHandler.handleException(e, httpResponse);
			return;
		}
        
        Long userId = claims.get("userId", Long.class);
        String role = claims.get("role", String.class);
        Long branchId = claims.get("branchId", Long.class);
        String redisStoredToken = Redis.get(userId.toString());

        token = token.replace("Bearer ", "");
        if(userId==null || token==null || redisStoredToken==null || !(redisStoredToken.equals(token))) {
        	setResponse(httpResponse, HttpServletResponse.SC_OK, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
        	return;
        }        
        
        if ((userId==null||role==null) ||
        	((role.equals("MANAGER")||role.equals("EMPLOYEE"))&&branchId==null)) {
        	setResponse(httpResponse, HttpServletResponse.SC_OK, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
        	return;
        }
        if(path.equals("/validate-token")) {
        	setResponse(httpResponse, HttpServletResponse.SC_OK, HttpServletResponse.SC_OK, "Token is valid.");
        	return;
        }
        
        String method = httpRequest.getHeader("action") != null? httpRequest.getHeader("action") : httpRequest.getMethod();
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
    	
    	UserDetailsLocal store = UserDetailsLocal.get();
    	store.setUserId(userId);
    	store.setRole(role);
    	store.setBranchId(branchId);
        chain.doFilter(request, response);
        store.clear();
    }
    
    private Claims tokenValidator(String token, Claims claims) throws CustomException {
    	if (token == null || !token.startsWith("Bearer ")) {
    		throw new CustomException(HttpServletResponse.SC_UNAUTHORIZED, "Authorization token is required.");
        }
    	try {
        	token = token.replace("Bearer ", "");
        	claims = Jwts.parser()
        			.setSigningKey(SECRET_KEY)
        			.parseClaimsJws(token)
        			.getBody();
        } catch (ExpiredJwtException ex) {
        	throw new CustomException(HttpServletResponse.SC_UNAUTHORIZED, "Expired token.");
        } catch (Exception e) {
        	throw new CustomException(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
		}
    	return claims;
    }
    
    private void setResponse(HttpServletResponse httpResponse, int serverStatus, int actionStatus, String message) throws IOException {
    	httpResponse.setStatus(serverStatus);
    	httpResponse.setContentType("application/json");
    	httpResponse.setCharacterEncoding("UTF-8");
    	PrintWriter writer = httpResponse.getWriter();
    	writer.write("{\"status\":"+actionStatus+",\"message\":\""+message+"\"}");
    	writer.close();
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}
