package com.netbanking.object;

import javax.servlet.http.HttpServletResponse;

import com.netbanking.exception.CustomException;
import com.netbanking.model.Model;

public class Employee extends User implements Model {
    private Long employeeId;
    private Long branchId;

    // Getter for employeeId
    public Long getEmployeeId() {
        return employeeId;
    }

    // Setter for employeeId
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

    // Getter for branchId
    public Long getBranchId() {
        return branchId;
    }

    // Setter for branchId
    public void setBranchId(Long branchId) throws CustomException {
    	String branchIdStr = String.valueOf(branchId);
    	if (branchId != null && !branchIdStr.matches("\\d{1,5}")) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "Branch ID must be within 5 digits and contain only numeric characters."
            );
        }
        this.branchId = branchId;
    }
}
