package com.netbanking.runner;
//
//import java.sql.Date;
//import java.sql.SQLException;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.netbanking.dao.Dao;
//import com.netbanking.object.Customer;
//import com.netbanking.object.Employee;
//import com.netbanking.object.Role;
//import com.netbanking.object.Status;
//import com.netbanking.object.User;
//
//public class TestRunner {
//
//    public static void main(String[] args) {
////        Dao dao = new Dao();
//        
//
////         Testing User insertion
////        User user = new User(
////            10000012L,
////            "password123",
////            Role.CUSTOMER,
////            "Jubair Ahamed",
////            "jubair@example.cox",
////            "9876543290",
////            new Date(103, 9, 23),
////            Status.ACTIVE,
////            System.currentTimeMillis(),
////            System.currentTimeMillis(),
////            0L
////        );
//        
////        Map<String, Object> updateValues = new HashMap<>();
////        updateValues.put("password", "newSecurePassword123");
////        updateValues.put("status", Status.INACTIVE);
////
////        // Map for conditions (the WHERE clause conditions)
////        Map<String, Object> updateConditionValues = new HashMap<>();
////        updateConditionValues.put("user_id", user.getId());
////        updateConditionValues.put("role", Role.CUSTOMER);
////
////        // Operators for the update values (for now, using "=" for all updates)
////        List<String> operators = Arrays.asList("=", "=");
////
////        // Conditions for the WHERE clause (using "AND" between conditions)
////        List<String> conditions = Arrays.asList("AND");
//
//        
//        Customer customer = new Customer(
//        		10000012L,          // customer_id
//                123456789018L,  // aadhar_number
//                "ABCDE12343"    // pan_number
//            );
////        
////        Employee employee = new Employee();
////        employee.setBranchId(001L);
////        employee.setEmployeeId(10000012L);
////        employee.setRole(Role.EMPLOYEE);
////        
////        try {
////			dao.update("user", updateValues, updateConditionValues, operators, conditions);
////		} catch (SQLException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//        
////        try {
////			dao.insert(user);
////		} catch (SQLException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////      try {
////			dao.update(user);
////		} catch (SQLException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////        
//        
////            try {
////				dao.insert(customer);
////			} catch (SQLException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
//        
//        try {
//			runTest();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
////        try {
////			dao.insert(employee);
////		} catch (SQLException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////            
////            try {
////				dao.update(customer);
////			} catch (SQLException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
//        
////        try {
////			dao.delete(user);
////		} catch (SQLException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////        System.out.println("Generic DAO tests executed successfully!");
//        
//        
////        Map<String, Object> deleteConditionValues = new HashMap<>();
////        deleteConditionValues.put("status", Status.INACTIVE);
////        deleteConditionValues.put("role", Role.CUSTOMER);
////        deleteConditionValues.put("user_id", 10000012L);
////
////        // Operators for the conditions (for now, using "=" for all)
////        List<String> operators = Arrays.asList("=", "=", "=");
////
////        // Conditions for the WHERE clause (using "AND" between conditions)
////        List<String> conditions = Arrays.asList("AND", "AND");
////
////        // Create an instance of the class containing the delete method
////        Dao instance = new Dao();
////
////        // Call the method to generate and execute the delete query
////        try {
////			instance.delete("user", deleteConditionValues, operators, conditions);
////		} catch (SQLException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//    }
//    
//    public static void runTest() throws SQLException {
//        String tableName = "user";
//        Map<String, Object> updateValues = new HashMap<>();
//        updateValues.put("status", "INACTIVE");
//
//        Map<String, Object> updateConditionValues = new HashMap<>();
//        updateConditionValues.put("user_id", 10000012L); // Condition to match specific user_id
//
//        String joinTableName = "customer";
//        Map<String, Object> joinTableUpdateValues = new HashMap<>();
//        joinTableUpdateValues.put("aadhar_number", "218139471381");
//
//        Map<String, Object> joinTableUpdateConditionValues = new HashMap<>();
//        joinTableUpdateConditionValues.put("customer_id", 10000012L); // Condition to match specific customer_id
//
//        // ON condition for JOIN clause
//        Map<String, String> onCondition = new HashMap<>();
//        onCondition.put("user_id", "customer_id"); // user.user_id = customer.user_id
//
//        // Operators for conditions (assuming equality for simplicity)
//        // The first is for the ON condition, the remaining for WHERE conditions.
//        // Adjust the number of operators according to the number of conditions.
//        String equals = "=";
//        List<String> operators = Arrays.asList(equals, equals, equals);
//
//        // Logical conditions (AND/OR)
//        // Adjust the number of conditions according to the number of WHERE conditions.
//        List<String> conditions = Arrays.asList("AND", "AND");
//
//        // Call the update method (assumes an instance of your class with the method exists)
//        // Replace YourDAOClass with the class where your update method is defined.
////        Dao dao = new Dao();
////        dao.update(tableName, updateValues, updateConditionValues, 
////                   joinTableName, joinTableUpdateValues, 
////                   joinTableUpdateConditionValues, onCondition, operators, conditions);
//
//        System.out.println("Update operation completed.");
//    }
//}


import com.netbanking.dao.DaoHandler;
import com.netbanking.object.Customer;
import com.netbanking.object.Role; // Assuming Role is an enum or class you have
import com.netbanking.object.Status; // Assuming Status is an enum or class you have

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestRunner {
    public static void main(String[] args) {
        // Create an instance of Customer
        Customer customer = new Customer();
        
        // Set User fields
        customer.setPassword("securePassword123");
        customer.setRole(Role.CUSTOMER); // Assuming Role is an enum
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setMobile("1234567890");
        customer.setDateOfBirth(new Date(103, 9, 23)); // Example date: 1990-01-01
        customer.setStatus(Status.ACTIVE); // Assuming Status is an enum
        customer.setCreationTime(System.currentTimeMillis());
        customer.setModifiedTime(System.currentTimeMillis());
        customer.setModifiedBy(1L); // Example user ID

        // Set Customer fields
        customer.setAadharNumber(123456789012L); // Example Aadhar number
        customer.setPanNumber("ABCDE1234F"); // Example PAN number
        
        // Display the customer information
        System.out.println("Customer Information:");
        System.out.println("Name: " + customer.getName());
        System.out.println("Email: " + customer.getEmail());
        System.out.println("Mobile: " + customer.getMobile());
        System.out.println("Aadhar Number: " + customer.getAadharNumber());
        System.out.println("PAN Number: " + customer.getPanNumber());
        System.out.println("Date of Birth: " + customer.getDateOfBirth());
        System.out.println("Status: " + customer.getStatus());
        System.out.println("Creation Time: " + customer.getCreationTime());
        System.out.println("Modified Time: " + customer.getModifiedTime());
        System.out.println("Modified By: " + customer.getModifiedBy());
        
        DaoHandler<Customer> dao = new DaoHandler<>();
        dao.insertHandler(customer);
//        Map<String, Object> values = new HashMap<>();
//        values.put("customer_id", 20);
//        try {
//			dao.delete("customer", values);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }
}
