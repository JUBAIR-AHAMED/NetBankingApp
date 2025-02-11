package com.netbanking.enumHelper;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import com.netbanking.exception.CustomException;
import com.netbanking.object.pattern.DataValidater;

public enum RequiredFields {

    ACCOUNT(createSet("userId", "branchId", "accountType", "balance", "status")),
    BRANCH(createSet("name", "ifsc", "address", "employeeId")),
    EMPLOYEE(createSet("name", "password", "email", "mobile", "dateOfBirth", "role", "status")),
    CUSTOMER(createSet("name", "password", "email", "mobile", "dateOfBirth", "aadharNumber", "panNumber"));

    private final Set<String> requiredFields;

    RequiredFields(Set<String> requiredFields) {
        this.requiredFields = requiredFields;
    }

    public Set<String> getRequiredFields() {
        return requiredFields;
    }

    public void validate(Map<String, Object> data) throws CustomException {
        for (String field : requiredFields) {
            if (!data.containsKey(field) || data.get(field) == null) {
                throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Missing required field: " + field + ".");
            }

            String value = data.get(field).toString();
            if (!DataValidater.isValidField(field, value)) {
                throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Invalid value for field: " + field + ".");
            }
        }

        for (String field : data.keySet()) {
        	if(this.equals(RequiredFields.EMPLOYEE) && field.equals("branchId")) {
        		continue;
        	}
            if (!requiredFields.contains(field)) {
                throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Field not allowed: " + field + ".");
            }
        }
    }
    
    // Helper method to create a Set in a Java 8-compatible way
    private static Set<String> createSet(String... fields) {
        Set<String> set = new HashSet<>();
        Collections.addAll(set, fields);
        return Collections.unmodifiableSet(set);
    }
}
