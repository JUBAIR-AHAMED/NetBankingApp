package com.netbanking.enumHelper;

import com.netbanking.object.Account;
import com.netbanking.object.Customer;
import com.netbanking.object.Employee;
import com.netbanking.object.User;
import com.netbanking.object.Branch;

public enum GetMetadata {
    USER(User.class, "user", "userId", "USER$USER_ID:"),
    CUSTOMER(Customer.class, "customer", "customerId", "CUSTOMER$USER_ID:"),
    EMPLOYEE(Employee.class, "employee", "employeeId", "EMPLOYEE$USER_ID:"),
	ACCOUNT(Account.class, "account", "accountNumber", "ACCOUNT$ACCOUNT_NUMBER:"),
	BRANCH(Branch.class, "branch", "branchId", "BRANCH$BRANCH_ID:");

    private final Class<?> pojoClass;
    private final String tableName;
    private final String primaryKeyColumn;
    private final String cacheKey;

    GetMetadata(Class<?> pojoClass, String tableName, String primaryKeyColumn, String cacheKey) {
        this.pojoClass = pojoClass;
        this.tableName = tableName;
        this.primaryKeyColumn = primaryKeyColumn;
        this.cacheKey = cacheKey;
    }

    public Class<?> getPojoClass(){
    	return pojoClass;
    }
    
    public String getTableName() {
        return tableName;
    }

    public String getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

    public String getCachKey() {
    	return cacheKey;
    }

    public static GetMetadata fromClass(Class<?> clazz) {
        for (GetMetadata metadata : values()) {
            if (metadata.pojoClass.equals(clazz)) {
                return metadata;
            }
        }
        throw new IllegalArgumentException("No metadata found for class: " + clazz.getName());
    }
}
