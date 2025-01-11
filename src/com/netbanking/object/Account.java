package com.netbanking.object;

import javax.servlet.http.HttpServletResponse;

import com.netbanking.enums.AccountType;
import com.netbanking.enums.Status;
import com.netbanking.exception.CustomException;
import com.netbanking.model.Model;

public class Account implements Model {
    private Long accountNumber;
    private Long userId;
    private Long branchId;
    private AccountType accountType;
    private Long dateOfOpening;
    private Float balance;
    private Status status;
    private Long creationTime;
    private Long modifiedTime;
    private Long modifiedBy;

    // Getters and Setters
    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) throws CustomException{
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
                "User ID must be exactly 6 digits and contain only numeric characters."
            );
        }

    	this.userId = userId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) throws CustomException {
    	String branchIdStr = String.valueOf(branchId);

        if (branchIdStr != null && !branchIdStr.matches("\\d{1,5}")) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "Branch ID must be exactly 5 digits and contain only numeric characters."
            );
        }
        this.branchId = branchId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Long getDateOfOpening() {
        return dateOfOpening;
    }

    public void setDateOfOpening(Long dateOfOpening) throws CustomException {
        this.dateOfOpening = dateOfOpening;
    }

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) throws CustomException {
    	if (balance != null && balance < 0) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "Balance cannot be negative."
            );
        }
        this.balance = balance;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) throws CustomException {
        this.creationTime = creationTime;
    }

    public Long getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Long modifiedTime) throws CustomException {
        this.modifiedTime = modifiedTime;
    }

    public Long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Long modifiedBy) throws CustomException {
    	String userIdStr = String.valueOf(modifiedBy);
    	if (userIdStr != null && !userIdStr.matches("\\d{1,6}")) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "User ID must be exactly 6 digits and contain only numeric characters."
            );
        }
        this.modifiedBy = modifiedBy;
    }
}
