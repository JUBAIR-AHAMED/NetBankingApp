//package com.netbanking.auth;
//
//import java.io.IOException;
//import java.util.Map;
//
////import com.netbanking.api.Api;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//
//
////@WebServelet("api/auth")
//public class Authentication extends HttpServlet {
//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		String userIdParam = req.getParameter("user_id");
//	    Long user_id = null;
//	    
//	    if (userIdParam != null && !userIdParam.isEmpty()) {
//	        try {
//	            user_id = Long.parseLong(userIdParam);
//	        } catch (NumberFormatException e) {
//	            req.setAttribute("errorMessage", "Invalid user ID format");
//	            req.getRequestDispatcher("login.jsp").forward(req, resp);
//	            return;
//	        }
//	    }
//	        
//		String password = req.getParameter("password");
//		Api api = new Api();
//		Map<String, Object> userData = api.getLogin(user_id, password);
//		
//		if(userData != null && !userData.isEmpty())
//		{
//			HttpSession session = req.getSession();
//			session.setAttribute("user_id", userData.get("user_id"));
//			session.setAttribute("role", userData.get("role"));
//			resp.sendRedirect("Account.jsp");
//		} 
//		else {
//	        req.setAttribute("errorMessage", "Invalid username or password");
//	        req.getRequestDispatcher("login.jsp").forward(req, resp);
//	    }
//	}
//}
