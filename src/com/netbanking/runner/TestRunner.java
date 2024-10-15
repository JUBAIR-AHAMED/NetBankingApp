package com.netbanking.runner;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.netbanking.dao.Dao;
import com.netbanking.object.Customer;
import com.netbanking.object.Employee;
import com.netbanking.object.Role;
import com.netbanking.object.Status;
import com.netbanking.object.User;

public class TestRunner {

    public static void main(String[] args) {
        Dao dao = new Dao();
        

//         Testing User insertion
        User user = new User(
            10000012L,
            "password123",
            Role.CUSTOMER,
            "Jubair Ahamed",
            "jubair@example.cox",
            "9876543290",
            new Date(103, 9, 23),
            Status.ACTIVE,
            System.currentTimeMillis(),
            System.currentTimeMillis(),
            0L
        );
        
//        Map<String, Object> updateValues = new HashMap<>();
//        updateValues.put("password", "newSecurePassword123");
//        updateValues.put("status", Status.INACTIVE);
//
//        // Map for conditions (the WHERE clause conditions)
//        Map<String, Object> updateConditionValues = new HashMap<>();
//        updateConditionValues.put("user_id", user.getId());
//        updateConditionValues.put("role", Role.CUSTOMER);
//
//        // Operators for the update values (for now, using "=" for all updates)
//        List<String> operators = Arrays.asList("=", "=");
//
//        // Conditions for the WHERE clause (using "AND" between conditions)
//        List<String> conditions = Arrays.asList("AND");

        
//        Customer customer = new Customer(
//        		10000012L,          // customer_id
//                123456789018L,  // aadhar_number
//                "ABCDE12343"    // pan_number
//            );
//        
//        Employee employee = new Employee();
//        employee.setBranchId(001L);
//        employee.setEmployeeId(10000012L);
//        employee.setRole(Role.EMPLOYEE);
//        
//        try {
//			dao.update("user", updateValues, updateConditionValues, operators, conditions);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
//        try {
//			dao.insert(user);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//      try {
//			dao.update(user);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        
        
//            try {
//				dao.insert(customer);
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
        
//        try {
//			dao.insert(employee);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//            
//            try {
//				dao.update(customer);
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
        
//        try {
//			dao.delete(user);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        System.out.println("Generic DAO tests executed successfully!");
        
        
//        Map<String, Object> deleteConditionValues = new HashMap<>();
//        deleteConditionValues.put("status", Status.INACTIVE);
//        deleteConditionValues.put("role", Role.CUSTOMER);
//        deleteConditionValues.put("user_id", 10000012L);
//
//        // Operators for the conditions (for now, using "=" for all)
//        List<String> operators = Arrays.asList("=", "=", "=");
//
//        // Conditions for the WHERE clause (using "AND" between conditions)
//        List<String> conditions = Arrays.asList("AND", "AND");
//
//        // Create an instance of the class containing the delete method
//        Dao instance = new Dao();
//
//        // Call the method to generate and execute the delete query
//        try {
//			instance.delete("user", deleteConditionValues, operators, conditions);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
        List<String> selectFields = Arrays.asList("user_id", "name", "email");

        Map<String, Object> updateConditionValues = new HashMap<>();
        updateConditionValues.put("status", Status.ACTIVE);
        updateConditionValues.put("role", Role.CUSTOMER);
        updateConditionValues.put("user_id", 10000012L);

        List<String> operators = Arrays.asList("=", "=", "=");

        List<String> conditions = Arrays.asList("AND", "AND");

        Dao instance = new Dao();

        try {
			System.out.println(instance.select("user", selectFields, updateConditionValues, operators, conditions));
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}
