package com.netbanking.enumHelper;

import com.netbanking.object.Account;
import com.netbanking.object.Customer;
import com.netbanking.object.Employee;
import com.netbanking.object.User;

public enum GetMetadata {
    USER(User.class, "user", "userId"),
    CUSTOMER(Customer.class, "customer", "customerId"),
    EMPLOYEE(Employee.class, "employee", "employeeId"),
	ACCOUNT(Account.class, "account", "accountNumber");

    private final Class<?> pojoClass;
    private final String tableName;
    private final String primaryKeyColumn;

    GetMetadata(Class<?> pojoClass, String tableName, String primaryKeyColumn) {
        this.pojoClass = pojoClass;
        this.tableName = tableName;
        this.primaryKeyColumn = primaryKeyColumn;
    }

    public String getTableName() {
        return tableName;
    }

    public String getPrimaryKeyColumn() {
        return primaryKeyColumn;
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
