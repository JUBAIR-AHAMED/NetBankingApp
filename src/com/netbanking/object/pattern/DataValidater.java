package com.netbanking.object.pattern;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletResponse;
import com.netbanking.enums.AccountType;
import com.netbanking.enums.Role;
import com.netbanking.enums.Status;
import com.netbanking.exception.CustomException;
import com.netbanking.util.Validator;

public class DataValidater {
    public static boolean validate(String value, DataPattern pattern) throws CustomException {
        if (Validator.isNull(value)) {
            return false;
        }
        if(Validator.isNull(pattern)) {
        	return false;
        }
        Pattern regexPattern = Pattern.compile(pattern.getPattern());
        Matcher matcher = regexPattern.matcher(value);
        if(matcher.matches()) {
        	return true;
        }
        throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, pattern.getMessage());
    }

    public static boolean accountNumber(String value) throws CustomException {
        return validate(value, DataPattern.ACCOUNTNUMBER);
    }
    
    public static boolean balance(String value) throws CustomException {
        return validate(value, DataPattern.BALANCE);
    }

    public static boolean userId(String value) throws CustomException {
        return validate(value, DataPattern.USERID);
    }

    public static boolean branchId(String value) throws CustomException {
        return validate(value, DataPattern.BRANCHID);
    }

    public static boolean ifsc(String value) throws CustomException {
        return validate(value, DataPattern.IFSC);
    }

    public static boolean aadharNumber(String value) throws CustomException {
        return validate(value, DataPattern.AADHARNUMBER);
    }

    public static boolean panNumber(String value) throws CustomException {
        return validate(value, DataPattern.PANNUMBER);
    }

    public static boolean password(String value) throws CustomException {
        return validate(value, DataPattern.PASSWORD);
    }
    
    public static boolean name(String value) throws CustomException {
        return validate(value, DataPattern.NAME);
    }
    
    public static boolean address(String value) throws CustomException {
        return validate(value, DataPattern.ADDRESS);
    }
    
    public static boolean email(String value) throws CustomException {
        return validate(value, DataPattern.EMAIL);
    }

    public static boolean mobile(String value) throws CustomException {
        return validate(value, DataPattern.MOBILE);
    }
    
    public static boolean dateOfBirth(String value) throws CustomException {
        String datePattern = "^(\\d{4})-(\\d{2})-(\\d{2})$";
        
        if (value.matches(datePattern)) {
            try {
                LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                return true;
            } catch (Exception e) {
            	throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Invalid value for the date of birth.");
            }
        }
        throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Invalid value for the date of birth.");
    }

    
    public static boolean role(String value) throws CustomException {
    	try {			
    		Role.valueOf(value);
    		return true;
		} catch (IllegalArgumentException e) {
			throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Provided role is invalid.");
		}
    }
    
    public static boolean accountType(String value) throws CustomException {
    	try {			
    		AccountType.valueOf(value);
    		return true;
		} catch (IllegalArgumentException e) {
			throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Account type must be SAVINGS/CURRENT.");
		}
    }
    
    public static boolean status(String value) throws CustomException {
    	try {			
    		Status.valueOf(value);
    		return true;
		} catch (IllegalArgumentException e) {
			throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Status must be ACTIVE/INACTIVE/BLOCKED");
		}
    }
    
    public static boolean isValidField(String field, String value) throws CustomException {
        switch (field) {
            case "accountType":
                return DataValidater.accountType(value);
            case "balance":
                return DataValidater.balance(value);
            case "status":
                return DataValidater.status(value);
            case "userId":
                return DataValidater.userId(value);
            case "employeeId":
                return DataValidater.userId(value);
            case "branchId":
                return DataValidater.branchId(value);
            case "ifsc":
                return DataValidater.ifsc(value);
            case "aadharNumber":
                return DataValidater.aadharNumber(value);
            case "panNumber":
                return DataValidater.panNumber(value);
            case "password":
                return DataValidater.password(value);
            case "name":
                return DataValidater.name(value);
            case "address":
                return DataValidater.address(value);
            case "email":
                return DataValidater.email(value);
            case "mobile":
                return DataValidater.mobile(value);
            case "role":
                return DataValidater.role(value);
            case "dateOfBirth":
                return DataValidater.dateOfBirth(value);
            default:
                return true; // No validation for unknown fields
        }
    }
    
}
