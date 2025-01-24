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
    EMPLOYEE(createSet("name", "password", "email", "mobile", "dateOfBirth", "branchId", "role", "status")),
    CUSTOMER(createSet("name", "password", "email", "mobile", "dateOfBirth", "aadharNumber", "panNumber"));

    private final Set<String> requiredFields;

    RequiredFields(Set<String> requiredFields) {
        this.requiredFields = requiredFields;
    }

    public Set<String> getRequiredFields() {
        return requiredFields;
    }

    public static void validate(String table, Map<String, Object> data) throws CustomException {
        // Step 1: Get the required fields for the table
        RequiredFields requiredFields = RequiredFields.valueOf(table.toUpperCase());
        Set<String> required = requiredFields.getRequiredFields();

        // Step 2: Check if all required fields are present
        for (String field : required) {
            if (!data.containsKey(field) || data.get(field) == null) {
                throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Missing required field: " + field + ".");
            }

            // Step 3: Validate each field value using DataValidater
            String value = data.get(field).toString();
            if (!DataValidater.isValidField(field, value)) {
                throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Invalid value for field: " + field + ".");
            }
        }

        // Step 4: Check if there are any extra fields that are not allowed
        for (String field : data.keySet()) {
            if (!required.contains(field)) {
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
