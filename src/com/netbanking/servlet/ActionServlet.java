//package com.netbanking.servlet;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import com.netbanking.api.ApiHandler;
//import com.netbanking.exception.CustomException;
//import com.netbanking.util.Parser;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//
//public class ActionServlet extends HttpServlet{
//	private static final long serialVersionUID = 1L;
//
//    private static final String SECRET_KEY = "018d7a1625d1d217ffde1629409edbdb889f373aaef7032d6a711d2d40848fef";
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//    	Map<String, Object> responseMap = new HashMap<>();
//    	try {
//			String token = request.getHeader("Authorization");
//			token = token.substring(7);
//			ApiHandler apiHandler = new ApiHandler();
//			Claims claims = null;
//			try {
//				claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
//			} catch (Exception e) {
//				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                responseMap.put("status", false);
//                responseMap.put("message", "Invalid or expired token.");
//                Parser.writeResponse(response, responseMap);
//                return;
//			}
//			
//			Long userId = claims.get("userId", Long.class);
//			String role = claims.get("role", String.class);
//			Long branchId = claims.get("branchId", Long.class);
//			
//			if(userId == null)
//			{
//				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//				responseMap.put("status", false);
//				responseMap.put("message", "User Id is missing");
//				Parser.writeResponse(response, responseMap);
//            	return;
//			}
//			
//			if(!role.equals("EMPLOYEE") && !role.equals("MANAGER")) {
//				response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
//				responseMap.put("status", false);
//				responseMap.put("message", "Permission Denied.");
//				Parser.writeResponse(response, responseMap);
//				return;
//			}
//			
//			try {
//				apiHandler.initiateAction(request, userId, role, branchId);	
//				response.setStatus(HttpServletResponse.SC_OK);
//				responseMap.put("status", true);
//                responseMap.put("message", "Action completed successfully.");
//			} catch(CustomException e) {
//				e.printStackTrace();
//				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//				responseMap.put("status", false);
//                responseMap.put("message", e.getMessage());
//			}
//    	} catch(Exception e) {
//    		e.printStackTrace();
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			responseMap.put("status", false);
//            responseMap.put("message", "Action failed.");
//    	}
//    	Parser.writeResponse(response, responseMap);
//    }
//}
