package com.netbanking.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.netbanking.handler.AccountHandler;
import com.netbanking.handler.BranchHandler;
import com.netbanking.handler.CustomerHandler;
import com.netbanking.handler.EmployeeHandler;
import com.netbanking.handler.LoginHandler;
import com.netbanking.handler.ProfileHandler;
import com.netbanking.handler.TransactionHandler;
import com.netbanking.handler.UserHandler;

public class GenericServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if (path != null) {
            switch (path) {
	            case "/profile":
	                ProfileHandler.handleGet(request, response);
	                break;
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        String action = request.getHeader("action");
        
        if ("GET".equalsIgnoreCase(action)) {
            // Handle as a GET request
            switch (path) {
	            case "/account":
	                AccountHandler.handleGet(request, response);
	                break;
                case "/user":
                    UserHandler.handleGet(request, response);
                    break;
                case "/branch":
                    BranchHandler.handleGet(request, response);
                    break;
                case "/transaction":
                    TransactionHandler.handleGet(request, response);
                    break;
            }
        } else {
            // Handle as a POST request
            switch (path) {
                case "/login":
                    LoginHandler.handlePost(request, response);
                    break;
                case "/account":
                    AccountHandler.handlePost(request, response);
                    break;
                case "/customer":
                    CustomerHandler.handlePost(request, response);
                    break;
                case "/employee":
                    EmployeeHandler.handlePost(request, response);
                    break;
                case "/branch":
                    BranchHandler.handlePost(request, response);
                    break;
                case "/transaction":
                    TransactionHandler.handlePost(request, response);
                    break;
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if (path != null) {
            switch (path) {
                case "/profile":
                    UserHandler.handlePut(request, response);
                    break;
                case "/customer":
                    CustomerHandler.handlePut(request, response);
                    break;
                case "/account":
                    AccountHandler.handlePut(request, response);
                    break;
                case "/employee":
                    EmployeeHandler.handlePut(request, response);
                    break;
                case "/logout":
                    LoginHandler.handlePut(request, response);
                    break;
            }
        }
    }
}
