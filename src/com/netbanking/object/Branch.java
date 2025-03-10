package com.netbanking.object;

import javax.servlet.http.HttpServletResponse;

import com.netbanking.exception.CustomException;
import com.netbanking.model.Model;

public class Branch implements Model {
    private Long branchId;
    private String ifsc;
    private String name;
    private Long employeeId;
    private String address;
    private Long creationTime;
    private Long modifiedTime;
    private Long modifiedBy;

    // Getters and Setters
    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) throws CustomException {
    	String branchIdStr = String.valueOf(branchId);

        if (branchIdStr != null && !branchIdStr.matches("\\d{1,5}")) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "Branch ID must be within 5 digits and contain only numeric characters."
            );
        }
        this.branchId = branchId;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) throws CustomException {
    	if (ifsc != null && !ifsc.matches("^[A-Z]{4}0\\d{6}$")) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "IFSC code must start with 4 uppercase letters, followed by a 0, and then 6 digits."
            );
        }
        this.ifsc = ifsc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws CustomException {
    	if (name != null && !name.matches("^[A-Za-z. ]+$")) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "Name must contain only alphabets and dots (.)"
            );
        }
        this.name = name;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) throws CustomException {
    	String userIdStr = String.valueOf(employeeId);
        if (userIdStr != null && !userIdStr.matches("\\d{1,6}")) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "User ID must be within 6 digits and contain only numeric characters."
            );
        }
        this.employeeId = employeeId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) throws CustomException {
    	if (address != null && !address.matches("^[A-Za-z0-9,.'\\s-/]+$")) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "Address can only contain letters, numbers, spaces, and the following punctuation: , . ' - /"
            );
        }
        this.address = address;
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
                "User ID must be within 6 digits and contain only numeric characters."
            );
        }
        this.modifiedBy = modifiedBy;
    }
}
