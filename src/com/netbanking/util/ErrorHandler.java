package com.netbanking.util;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Level;
import com.netbanking.activityLogger.AsyncLoggerUtil;
import com.netbanking.exception.CustomException;

public class ErrorHandler {
    public static void handleException(Exception ex, HttpServletResponse httpResponse) throws IOException {
    	ex.printStackTrace();
    	AsyncLoggerUtil.log(ErrorHandler.class, Level.ERROR, ex);
        if (ex instanceof CustomException) {
        	CustomException customException = (CustomException) ex;
            int statusCode = customException.getStatusCode();
            String errorMessage = customException.getMessage();
            Writer.setResponse(httpResponse, HttpServletResponse.SC_OK, statusCode, errorMessage);
        } else if (ex instanceof SQLException) {
            SQLException sqlException = (SQLException) ex;
            String errorMessage = sqlException.getMessage();
            
            if (sqlException.getErrorCode() == 1062) { // Check if it's a duplicate entry error
                // Handle the duplicate entry error
                Writer.setResponse(
                    httpResponse, 
                    HttpServletResponse.SC_OK, 
                    HttpServletResponse.SC_CONFLICT, 
                    "Duplicate value is found: " + extractDuplicateValue(errorMessage)
                );
            } else {
                // Handle other SQL exceptions
                Writer.setResponse(
                    httpResponse, 
                    HttpServletResponse.SC_OK, 
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "An unexpected error occurred."
                );
            }
        }
        else {
            Writer.setResponse(httpResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }
    
    private static String extractDuplicateValue(String message) {
        String duplicateValue = "unknown"; // Default value
        try {
            // Regular expression to match the value between "Duplicate entry '" and "' for key"
            String regex = "Duplicate entry '(.+?)' for key";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
            java.util.regex.Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                duplicateValue = matcher.group(1); // Extract the value (e.g., '87')
            }
        } catch (Exception ex) {
        	AsyncLoggerUtil.log(ErrorHandler.class, Level.ERROR, "Error parsing duplicate value: " + ex.getMessage());
        }
        return duplicateValue;
    }

}