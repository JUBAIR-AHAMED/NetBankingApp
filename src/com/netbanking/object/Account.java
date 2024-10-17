package com.netbanking.object;

import com.netbanking.model.Model;

public class Account implements Model {
    private Long accountNumber;
    private Long userId;
    private Long branchId;
    private String accountType;  // Changed to String
    private Long dateOfOpening;
    private Integer balance;
    private String status;  // Changed to String
    private Long creationTime;
    private Long modifiedTime;
    private Long modifiedBy;

    // Default constructor
    public Account() {
    }

    // Constructor with parameters
    public Account(Long accountNumber, Long userId, Long branchId, String accountType,
                   Long dateOfOpening, Integer balance, String status, 
                   Long creationTime, Long modifiedTime, Long modifiedBy) {
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.branchId = branchId;
        this.accountType = accountType;
        this.dateOfOpening = dateOfOpening;
        this.balance = balance;
        this.status = status;
        this.creationTime = creationTime;
        this.modifiedTime = modifiedTime;
        this.modifiedBy = modifiedBy;
    }

    // Getters and Setters
    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Long getDateOfOpening() {
        return dateOfOpening;
    }

    public void setDateOfOpening(Long dateOfOpening) {
        this.dateOfOpening = dateOfOpening;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    public Long getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public Long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
