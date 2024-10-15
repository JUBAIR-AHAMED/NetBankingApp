package com.netbanking.object;

import java.util.Arrays;
import java.util.List;
import com.netbanking.model.Model;

public class Customer implements Model {
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
    
    @Override
    public Long getId() {
        return customerId;
    }
    
    @Override
    public String getIdField() {
        return "customer_id";  // Return the actual column name for the primary key
    }
}
