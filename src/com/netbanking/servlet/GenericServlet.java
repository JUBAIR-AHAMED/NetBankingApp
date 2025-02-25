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