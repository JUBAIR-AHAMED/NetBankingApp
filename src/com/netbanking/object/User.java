package com.netbanking.object;

import java.util.Date;

public class User {
    private Long userId;
    private String password;
    private Role role;
    private String name;
    private String email;
    private String mobile;
    private Date dateOfBirth;
    private Status status;
    private Long creationTime;
    private Long modifiedTime;
    private Long modifiedBy;
    
    public User() {
    }

    public User(Long userId, String password, Role role, String name, String email, String mobile, Date dateOfBirth, Status status, Long modifiedBy) {
        this.userId = userId;
        this.password = password;
        this.role = role;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
        this.modifiedBy = modifiedBy;
    }
    
    public User(Long userId, String password, Role role, String name, String email, String mobile, Date dateOfBirth, Status status, Long creationTime, Long modifiedTime, Long modifiedBy) {
        this.userId = userId;
        this.password = password;
        this.role = role;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
        this.creationTime = creationTime;
        this.modifiedTime = modifiedTime;
        this.modifiedBy = modifiedBy;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
    
    public Long getId() {
        return userId;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }    
}

