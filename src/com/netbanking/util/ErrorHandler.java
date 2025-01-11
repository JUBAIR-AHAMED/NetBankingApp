package com.netbanking.util;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import com.netbanking.exception.CustomException;

public class ErrorHandler {
    public static void handleException(Exception ex, HttpServletResponse httpResponse) throws IOException {
    	System.out.println("Exception faced.");
        if (ex instanceof CustomException) {
        	ex.printStackTrace();
        	CustomException customException = (CustomException) ex;
            int statusCode = customException.getStatusCode();
            String errorMessage = customException.getMessage();
            Writer.setResponse(httpResponse, HttpServletResponse.SC_OK, statusCode, errorMessage);
        } else {
        	ex.printStackTrace();
            Writer.setResponse(httpResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }
}