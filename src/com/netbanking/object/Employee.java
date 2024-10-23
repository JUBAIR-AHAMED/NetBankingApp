package com.netbanking.object;

import com.netbanking.model.Model;

public class Employee extends User implements Model {
    private Long employeeId;
    private Long branchId;

    // Getter for employeeId
    public Long getEmployeeId() {
        return employeeId;
    }

    // Setter for employeeId
    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    // Getter for branchId
    public Long getBranchId() {
        return branchId;
    }

    // Setter for branchId
    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }
}
