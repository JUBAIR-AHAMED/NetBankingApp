//package com.netbanking.servlet;
//
//import java.io.IOException;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import com.netbanking.handler.AccountHandler;
//import com.netbanking.handler.ActivityHandler;
//import com.netbanking.handler.BranchHandler;
//import com.netbanking.handler.CustomerHandler;
//import com.netbanking.handler.EmployeeHandler;
//import com.netbanking.handler.LoginHandler;
//import com.netbanking.handler.ProfileHandler;
//import com.netbanking.handler.TransactionHandler;
//import com.netbanking.handler.UserHandler;
//
//public class GenericServlet extends HttpServlet {
//    private static final long serialVersionUID = 1L;
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String path = request.getPathInfo();
//        if (path != null) {
//            switch (path) {
//	            case "/profile":
//	                ProfileHandler.handleGet(request, response);
//	                break;
//            }
//        }
//    }
//    
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String path = request.getPathInfo();
//        String action = request.getHeader("action");
//        if ("GET".equalsIgnoreCase(action)) {
//            // Handle as a GET request
//            switch (path) {
//	            case "/account":
//	                AccountHandler.handleGet(request, response);
//	                break;
//                case "/user":
//                    UserHandler.handleGet(request, response);
//                    break;
//                case "/branch":
//                    BranchHandler.handleGet(request, response);
//                    break;
//                case "/transaction":
//                    TransactionHandler.handleGet(request, response);
//                    break;
//                case "/logs":
//	                ActivityHandler.handleGet(request, response);
//	                break;
//            }
//        } else {
//            // Handle as a POST request
//            switch (path) {
//                case "/login":
//                    LoginHandler.handlePost(request, response);
//                    break;
//                case "/account":
//                    AccountHandler.handlePost(request, response);
//                    break;
//                case "/customer":
//                    CustomerHandler.handlePost(request, response);
//                    break;
//                case "/employee":
//                    EmployeeHandler.handlePost(request, response);
//                    break;
//                case "/branch":
//                    BranchHandler.handlePost(request, response);
//                    break;
//                case "/transaction":
//                    TransactionHandler.handlePost(request, response);
//                    break;
//            }
//        }
//    }
//
//    @Override
//    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String path = request.getPathInfo();
//        if (path != null) {
//            switch (path) {
//                case "/profile":
//                    UserHandler.handlePut(request, response);
//                    break;
//                case "/customer":
//                    CustomerHandler.handlePut(request, response);
//                    break;
//                case "/account":
//                    AccountHandler.handlePut(request, response);
//                    break;
//                case "/employee":
//                    EmployeeHandler.handlePut(request, response);
//                    break;
//                case "/logout":
//                    LoginHandler.handlePut(request, response);
//                    break;
//            }
//        }
//    }
//}


package com.netbanking.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netbanking.util.ErrorHandler;
import com.netbanking.util.Validator;

public class GenericServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String path = request.getPathInfo();
            if (Validator.isNull(path) || path.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path");
                return;
            }

            String handlerClassName = "com.netbanking.handler." + capitalize(path.substring(1)) + "Handler";
            Class<?> handlerClass = Class.forName(handlerClassName);

            String action = request.getHeader("action");
            String effectiveMethod = (action != null && !action.isEmpty()) ? action.toUpperCase() : request.getMethod();

            String methodName = "handle" + capitalize(effectiveMethod);
            Method method = handlerClass.getDeclaredMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            method.invoke(null, request, response);
        } catch (Exception ex) {
        	Throwable cause = ex.getCause();
            if (cause instanceof Exception) {
                ErrorHandler.handleException((Exception) cause, response);
            } else {
                ErrorHandler.handleException(ex, response);
            }
        }
    }

    private String capitalize(String str) {
        if (Validator.isNull(str) || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}