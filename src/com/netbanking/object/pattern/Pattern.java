package com.netbanking.object.pattern;

public enum Pattern {
	ACCOUNTNUMBER("\\\\d{16}", "Account number must be exactly 16 digits and contain only numeric characters."),
	USERID("\\\\d{1,6}", "User ID must be within 6 digits and contain only numeric characters."),
	BRANCHID("\\\\d{1,5}", "Branch ID must be within 5 digits and contain only numeric characters."),
	IFSC("^[A-Z]{4}0\\d{6}$", "IFSC code must start with 4 uppercase letters, followed by a 0, and then 6 digits."),
	NAME("^[A-Za-z. ]+$", "Name must contain only alphabets and dots (.)"),
	ADDRESS("^[A-Za-z0-9,.'\\s-/]+$", "Address can only contain letters, numbers, spaces, and the following punctuation: , . ' - /"),
	AADHARNUMBER("^[2-9]{1}[0-9]{11}$", "Aadhar number can only contain number and must be less than 12 digits.");
	
	String pattern;
	String message;
	
	private Pattern(String pattern, String message) {
		this.pattern = pattern;
		this.message = message;
	}
}
