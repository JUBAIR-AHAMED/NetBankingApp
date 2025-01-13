package com.netbanking.object;

import java.sql.Date;
import javax.servlet.http.HttpServletResponse;
import com.netbanking.enums.Role;
import com.netbanking.enums.Status;
import com.netbanking.exception.CustomException;
import com.netbanking.model.Model;

public class User implements Model {
    protected Long userId;
    protected String password;
    protected Role role;
    protected String name;
    protected String email;
    protected String mobile;
    protected Date dateOfBirth;
    protected Status status;
    protected Long creationTime;
    protected Long modifiedTime;
    protected Long modifiedBy;
    
  //user getter and setters
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
    
    public Long getUserId() {
        return userId;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws CustomException {
    	if (password != null && !password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "Password must be at least 8 characters long, include at least one uppercase letter, one lowercase letter, one digit, and one special character."
            );
        }
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

    public void setName(String name) throws CustomException {
    	if (name != null && !name.matches("^[A-Za-z. ]+$")) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "Name must contain only alphabets and dots (.)"
            );
        }
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws CustomException {
    	if (email != null && !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid email format. Please provide a valid email address."
            );
        }
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) throws CustomException {
    	if (mobile != null && !mobile.matches("^[6-9]\\d{9}$")) {
            throw new CustomException(
                HttpServletResponse.SC_BAD_REQUEST,
                "Mobile number must be a 10-digit number starting with 6, 7, 8, or 9."
            );
        }

        this.mobile = mobile;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) throws CustomException {
        this.dateOfBirth = dateOfBirth;
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

	public String getIdField() {
		return "user_id";
	}
}

