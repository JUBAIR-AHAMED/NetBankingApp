package com.netbanking.enumHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import com.netbanking.exception.CustomException;

public enum EditableFields {
    ACCOUNT(Arrays.asList("accountType", "branchId", "status")),
    CUSTOMER(Arrays.asList("name", "email", "status", "dateOfBirth", "aadharNumber", "panNumber")),
    USER(Arrays.asList("name", "email", "mobile")),
    BRANCH(Arrays.asList("name", "address")),
    EMPLOYEE(Arrays.asList("name", "number", "branchId"));
	
    private final List<String> editableFields;

    EditableFields(List<String> editableFields) {
        this.editableFields = editableFields;
    }

    public static void validateEditableFields(Class<?> clazz, Map<String, Object> data) throws CustomException {
        // Get the enum for the class name
    	String className = clazz.getSimpleName().toUpperCase();
        EditableFields editableFieldEnum = EditableFields.valueOf(className);
        
        // Validate each key in the map
        for (String key : data.keySet()) {
            if (!editableFieldEnum.editableFields.contains(key)) {
                throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "The requested update fields are either non-editable/present.");
            }
        }
    }
}
