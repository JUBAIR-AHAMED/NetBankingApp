package com.netbanking.object.pattern;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.netbanking.enums.AccountType;
import com.netbanking.enums.Role;
import com.netbanking.enums.Status;

public class DataValidater {
    public static boolean validate(String value, DataPattern pattern) {
        if (value == null) {
            return false;
        }
        if(pattern == null) {
        	return false;
        }
        Pattern regexPattern = Pattern.compile(pattern.getPattern());
        Matcher matcher = regexPattern.matcher(value);
        return matcher.matches();
    }

    public static boolean accountNumber(String value) {
        return validate(value, DataPattern.ACCOUNTNUMBER);
    }
    
    public static boolean balance(String value) {
        return validate(value, DataPattern.BALANCE);
    }

    public static boolean userId(String value) {
        return validate(value, DataPattern.USERID);
    }

    public static boolean branchId(String value) {
    	System.out.println(value);
        return validate(value, DataPattern.BRANCHID);
    }

    public static boolean ifsc(String value) {
        return validate(value, DataPattern.IFSC);
    }

    public static boolean aadharNumber(String value) {
        return validate(value, DataPattern.AADHARNUMBER);
    }

    public static boolean panNumber(String value) {
        return validate(value, DataPattern.PANNUMBER);
    }

    public static boolean password(String value) {
        return validate(value, DataPattern.PASSWORD);
    }
    
    public static boolean name(String value) {
        return validate(value, DataPattern.NAME);
    }
    
    public static boolean address(String value) {
        return validate(value, DataPattern.ADDRESS);
    }
    
    public static boolean email(String value) {
        return validate(value, DataPattern.EMAIL);
    }

    public static boolean mobile(String value) {
        return validate(value, DataPattern.MOBILE);
    }
    
//    public static boolean dateOfBirth(String value) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        try {
//            LocalDate dob = LocalDate.parse(value, formatter);
//            LocalDate currentDate = LocalDate.now();
//            int age = Period.between(dob, currentDate).getYears();
//            return age >= 18;
//        } catch (Exception e) {
//            return false;
//        }
//    }
    
    public static boolean dateOfBirth(String value) {
        String datePattern = "^(\\d{4})-(\\d{2})-(\\d{2})$";
        
        if (value.matches(datePattern)) {
            try {
                LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    
    public static boolean role(String value) {
    	try {			
    		Role.valueOf(value);
    		return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
    }
    
    public static boolean accountType(String value) {
    	try {			
    		AccountType.valueOf(value);
    		return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
    }
    
    public static boolean status(String value) {
    	try {			
    		Status.valueOf(value);
    		return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
    }
    
    public static boolean isValidField(String field, String value) {
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
