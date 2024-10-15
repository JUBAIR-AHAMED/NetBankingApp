package com.netbanking.object;

import com.netbanking.model.Model;

public class Employee implements Model {
    private Long employeeId;
    private Long branchId;
    private Role role;

    @Override
    public Long getId() {
        return employeeId;
    }

    @Override
    public String getIdField() {
        return "employee_id";
    }

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

    // Getter for role
    public Role getRole() {
        return role;
    }

    // Setter for role
    public void setRole(Role role) {
        this.role = role;
    }
}
