package com.netbanking.object.pattern;

public enum DataPattern {
	
	ACCOUNTNUMBER("\\d{16}", 
			"Account number must be exactly 16 digits and contain only numeric characters."),
	BALANCE("^(0|[1-9]\\d*)(\\.\\d+)?$",
			"Balance must be a non-negative number with optional decimal(upto 2)."),
	USERID("\\d{1,6}", 
			"User ID must be within 6 digits and contain only numeric characters."),
	BRANCHID("\\d{1,5}", 
			"Branch ID must be within 5 digits and contain only numeric characters."),
	IFSC("^[A-Z]{4}0\\d{6}$", 
			"IFSC code must start with 4 uppercase letters, followed by a 0, and then 6 digits."),
	NAME("^[A-Za-z. ]+$", 
			"Name must contain only alphabets and dots (.)"),
	ADDRESS("^[A-Za-z0-9,.'\\s-/]+$", 
			"Address can only contain letters, numbers, spaces, and the following punctuation: , . ' - /"),
	AADHARNUMBER("^[2-9]{1}[0-9]{11}$", 
			"Aadhar number can only contain number and must 12 digits and cannot start with 0 or 1."),
	PANNUMBER("^[A-Z]{5}[0-9]{4}[A-Z]{1}$", 
			"PAN number must be in the format: 5 uppercase letters, followed by 4 digits, and ending with 1 uppercase letter."),
	PASSWORD("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$", 
			"Password must be at least 8 characters long, include at least one uppercase letter, one lowercase letter, "
			+ "one digit, and one special character."),
	EMAIL("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", 
			"Invalid email format. Please provide a valid email address."),
	MOBILE("^[6-9]\\d{9}$", 
			"Mobile number must be a 10-digit number starting with 6, 7, 8, or 9.");
	
	String pattern;
	String message;
	
	private DataPattern(String pattern, String message) {
		this.pattern = pattern;
		this.message = message;
	}
	
	public String getPattern() {
		return pattern;
	}
	
	public String getMessage() {
		return message;
	}
}