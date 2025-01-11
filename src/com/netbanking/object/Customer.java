package com.netbanking.object;

import javax.servlet.http.HttpServletResponse;

import com.netbanking.exception.CustomException;

public class Customer extends User {
    private Long customerId;
    private Long aadharNumber;
    private String panNumber;

    // Constructors, Getters, and Setters...
    
    public Customer() {
    }

    public Customer(long customerId, long aadharNumber, String panNumber) {
        this.customerId = customerId;
        this.aadharNumber = aadharNumber;
        this.panNumber = panNumber;
    }
    
    public void setCustomerId(Long customerId) throws CustomException {
    	String userIdStr = String.valueOf(customerId);
        if (userIdStr != null && !userIdStr.matches("\\d{1,6}")) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "User ID must be exactly 6 digits and contain only numeric characters."
            );
        }
        this.customerId = customerId;
    }

    public Long getCustomerId() {
    	return customerId;
    }

    public Long getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(Long aadharNumber) throws CustomException {
    	if (aadharNumber != null && aadharNumber.toString().length() != 12) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "Aadhar number must be a 12-digit number."
            );
        }

        this.aadharNumber = aadharNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) throws CustomException {
    	if (panNumber != null && !panNumber.matches("^[A-Z]{5}[0-9]{4}[A-Z]{1}$")) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "PAN number must be in the format: 5 uppercase letters, followed by 4 digits, and ending with 1 uppercase letter."
            );
        }
        this.panNumber = panNumber;
    }    
}
