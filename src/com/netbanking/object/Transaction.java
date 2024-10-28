package com.netbanking.object;

import com.netbanking.model.Model;

public class Transaction implements Model {
    private Long referenceNumber;  // Use Integer if you want to allow nulls, otherwise use int
    private Float transactionAmount;
    private Long timestamp;  // Consider using java.time.Instant for better time handling
    private Float balance;
    private Long accountNumber;
    private Long userId;
    private Long transactionAccount;
    private Long creationTime;
    private Long modifiedBy;

    // Default constructor
    public Transaction() {
    }

    // Getters and Setters
    public Long getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(Long referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public Float getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Float transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

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

    public Long getTransactionAccount() {
        return transactionAccount;
    }

    public void setTransactionAccount(Long transactionAccount) {
        this.transactionAccount = transactionAccount;
    }
    
    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }
    
    public Long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
