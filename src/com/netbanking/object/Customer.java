package com.netbanking.object;

import com.netbanking.model.Model;

public class Customer extends User implements Model {
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
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getCustomerId() {
    	return customerId;
    }

    public Long getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(Long aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }    
}
