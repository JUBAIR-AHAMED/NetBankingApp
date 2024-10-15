package com.netbanking.object;

import com.netbanking.model.Model;

public class Branch implements Model {
    private Long branchId;
    private Long ifsc;
    private String name;
    private Long employeeId;
    private String address;
    private Long creationTime;
    private Long modifiedTime;
    private Long modifiedBy;

    // Constructors
    public Branch() {
    }

    public Branch(Long branchId, Long ifsc, String name, Long employeeId, String address,
                  Long creationTime, Long modifiedTime, Long modifiedBy) {
        this.branchId = branchId;
        this.ifsc = ifsc;
        this.name = name;
        this.employeeId = employeeId;
        this.address = address;
        this.creationTime = creationTime;
        this.modifiedTime = modifiedTime;
        this.modifiedBy = modifiedBy;
    }

    // Getters and Setters
    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public Long getIfsc() {
        return ifsc;
    }

    public void setIfsc(Long ifsc) {
        this.ifsc = ifsc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    @Override
    public Long getId() {
        return branchId;
    }

    @Override
    public String getIdField() {
        return "branch_id";  // Return the actual column name for the primary key
    }
}
