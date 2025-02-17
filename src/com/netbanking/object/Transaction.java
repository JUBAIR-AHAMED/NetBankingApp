package com.netbanking.object;

import javax.servlet.http.HttpServletResponse;

import com.netbanking.enums.TransactionType;
import com.netbanking.exception.CustomException;
import com.netbanking.model.Model;
import com.netbanking.util.Validator;

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
    private String type;

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

    public void setTransactionAmount(Float transactionAmount) throws CustomException {
    	if (transactionAmount != null && transactionAmount <= 0) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "Transaction amount must be a positive value."
            );
        }
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

    public void setBalance(Float balance) throws CustomException {
    	if (balance != null && balance <= 0) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "Balance amount must be a positive value."
            );
        }
        this.balance = balance;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) throws CustomException {
    	String accountNumberStr = String.valueOf(accountNumber);
        if (accountNumberStr != null && !accountNumberStr.matches("\\d{16}")) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "Account number must be exactly 16 digits and contain only numeric characters."
            );
        }
        this.accountNumber = accountNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) throws CustomException {
    	String userIdStr = String.valueOf(userId);
        if (userIdStr != null && !userIdStr.matches("\\d{1,6}")) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "User ID must be within 6 digits and contain only numeric characters."
            );
        }
        this.userId = userId;
    }

    public Long getTransactionAccount() {
        return transactionAccount;
    }

    public void setTransactionAccount(Long transactionAccount) throws CustomException {
    	String accountNumberStr = String.valueOf(transactionAccount);
        if (transactionAccount != null && !accountNumberStr.matches("\\d{16}")) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "Account number must be exactly 16 digits and contain only numeric characters."
            );
        }
        this.transactionAccount = transactionAccount;
    }
    
    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) throws CustomException {
        this.creationTime = creationTime;
    }
    
    public Long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Long modifiedBy) throws CustomException {
    	String userIdStr = String.valueOf(modifiedBy);
    	if (userIdStr != null && !userIdStr.matches("\\d{1,6}")) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "User ID must be within 6 digits and contain only numeric characters."
            );
        }
        this.modifiedBy = modifiedBy;
    }
    
    public String getType() {
    	return type;
    }
    
    // change to enum
    public void setType(String type) throws CustomException {
    	if (Validator.isNull(TransactionType.valueOf(type))) {
                throw new CustomException(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Type must be one of the following: Deposit, Withdraw, Credit, or Debit."
                );
            }

    	this.type=type;
    }
}
