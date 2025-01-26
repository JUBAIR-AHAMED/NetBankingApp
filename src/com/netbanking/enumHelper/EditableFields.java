package com.netbanking.enumHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import com.netbanking.exception.CustomException;
import com.netbanking.object.pattern.DataValidater;

public enum EditableFields {
    ACCOUNT(Arrays.asList("accountType", "branchId", "status")),
    CUSTOMER(Arrays.asList("name", "email", "status", "dateOfBirth", "aadharNumber", "panNumber")),
    USER(Arrays.asList("name", "email", "mobile")),
    BRANCH(Arrays.asList("name", "address")),
    EMPLOYEE(Arrays.asList("name", "email", "dateOfBirth", "branchId", "status", "role"));
	
    private final List<String> editableFields;

    EditableFields(List<String> editableFields) {
        this.editableFields = editableFields;
    }

    public static void validateEditableFields(Class<?> clazz, Map<String, Object> data) throws CustomException {
        // Get the enum for the class name
    	String className = clazz.getSimpleName().toUpperCase();
        EditableFields editableFieldEnum = EditableFields.valueOf(className);
        
        // Validate each key in the map
        for (Map.Entry<String, Object> value : data.entrySet()) {
        	String dataKey = value.getKey();
        	String dataValue =(String) value.getValue();
            if (!editableFieldEnum.editableFields.contains(dataKey)) {
                throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "The requested update fields are either non-editable/not-present.");
            }
            if (!DataValidater.isValidField(dataKey, dataValue)) {
//            	System.out.println(dataKey+" "+dataValue);
                throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Invalid value for field: " + dataKey + ".");
            }
        }
    }
}
