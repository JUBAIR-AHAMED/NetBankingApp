package com.netbanking.runner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.netbanking.dao.DaoHandler;
import com.netbanking.dao.FunctionHandler;
import com.netbanking.exception.CustomException;
import com.netbanking.object.Customer;
import com.netbanking.object.QueryRequest;
import com.netbanking.util.Encryption;

//import com.netbanking.api.Api;

public class TestRunner {
    @SuppressWarnings("deprecation")
	public static void main(String[] args) {
//    	Api api = new Api();
//    	
//    	// Get Account
//    	api.getAccounts(1L, "CUSTOMER", null);
//    	api.getAccounts(null, "EMPLOYEE", 101L);
//    	api.getAccounts(null, "MANAGER", null);
//    	
//    	// Get Transaciton
//    	api.getTransactions(1L, 2L, 4L);
//    	Customer cus = new Customer();
//    	List<String> updates = new ArrayList<>();
//    	List<String> whereConditions = new ArrayList<String>();
//    	List<Object> whereCondVal = new ArrayList<Object>();
//    	List<String> whereOperators = new ArrayList<String>();
////    	List<String> whereLogicalOperators = new ArrayList<String>();
////    	
////    	cus.setPassword("qwe12");
////    	updates.add("password");
//    	whereConditions.add("customer_id");
//    	whereCondVal.add(1L);
//    	whereOperators.add("=");
////    	
//    	DaoHandler<Customer> dao = new DaoHandler<Customer>();
////    	try {
////			dao.updateHandler(cus, Customer.class, updates, whereConditions, whereCondVal, whereOperators, null);
////		} catch (SQLException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////    	Class<?> sc1 = cus.getClass().getSuperclass();
////    	System.out.println(sc1.getSimpleName());
////    	Class<?> sc2 = sc1.getClass().getSuperclass();
////    	System.out.println(sc2.getSimpleName());
//    	
//    	QueryRequest qr = new QueryRequest();
//    	qr.setSelectAllColumns(true);
//    	qr.setTableName("customer");
//    	qr.setWhereConditions(whereConditions);
//    	qr.setWhereConditionsValues(whereCondVal);
//    	qr.setWhereOperators(whereOperators);
//    	try {
//			System.out.println(dao.selectHandler(qr));
//		} catch (CustomException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	
    	FunctionHandler api = new FunctionHandler();
    	// Get Login
//    	try {
//    		String pass = Encryption.hashPassword("passwordd");
//    		System.out.println(pass);
//			System.out.println(api.getLogin(8L, "password"));
//			System.out.println("here"+Encryption.verifyPassword("password", pass));
//		} catch (CustomException e) {
//			e.printStackTrace();
//		}
    	
    	try {
			System.out.println(api.getCustomer(8L));
		} catch (CustomException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
//    	//Customer
//    	Map<String, Object> customerDetails = new HashMap<>();
//        customerDetails.put("password", "securePass123");
//        customerDetails.put("name", "John Doe");
//        customerDetails.put("email", "john.doe@example.com");
//        customerDetails.put("mobile", "9876543210");
//        customerDetails.put("dob", new Date(99, 9, 23)); // Date for 23-10-1999
//        customerDetails.put("modifiedBy", 1L);
//        customerDetails.put("aadharNumber", 123456789012L);
//        customerDetails.put("panNumber", "ABCDE1234F");
//
//        try {
//            api.createCustomer("MANAGER", customerDetails);
//            System.out.println("Customer created successfully.");
//        } catch (CustomException e) {
//            e.printStackTrace();
//        }
    	
    	
    	//Eployee
//    	Map<String, Object> customerDetails = new HashMap<>();
//    	customerDetails.put("password", "234567");
//    	customerDetails.put("name", "Opera Ji");
//    	customerDetails.put("email", "oper@asd.com");
//    	customerDetails.put("mobile", "9876543201");
//    	customerDetails.put("dob", new Date(99, 9, 23));
//    	customerDetails.put("modifiedBy", 1L);
//    	customerDetails.put("role", "MANAGER");
//    	try {
//			api.createEmployee("MANAGER", customerDetails);
//		} catch (CustomException e) {
//			e.printStackTrace();
//		}
    	
        
        //Branch
//    	Map<String, Object> branchDetails = new HashMap<>();
//        branchDetails.put("name", "Main Branch");
//        branchDetails.put("ifsc", 1234567890L);
//        branchDetails.put("employeeId", 6L);
//        branchDetails.put("address", "123 Main St, City, India");
//        branchDetails.put("modifiedBy", 1L);
//
//        try {
//            api.createBranch("MANAGER", branchDetails);
//            System.out.println("Branch created successfully.");
//        } catch (CustomException e) {
//            e.printStackTrace();
//        }
        
        //Account
//    	Map<String, Object> accountDetails = new HashMap<>();
//        accountDetails.put("userId", 7L);
//        accountDetails.put("branchId", 1L);
//        accountDetails.put("accountType", "SAVINGS");
//        accountDetails.put("balance", 10000L);
//        accountDetails.put("status", "ACTIVE");
//        accountDetails.put("modifiedBy", 1L);
//
//        try {
//            api.createAccount("MANAGER", accountDetails);
//            System.out.println("Account created successfully.");
//        } catch (CustomException e) {
//            e.printStackTrace();
//        }
    	
//    	DaoHandler<Customer> dao = new DaoHandler<Customer>();
//    	Map<String, Object> updates = new HashMap<String, Object>();
//    	updates.put("creationTime", System.currentTimeMillis());
//    	List<String> whereConditions = new ArrayList<String>(), whereOperators = new ArrayList<String>();
//    	whereConditions.add("userId");
//    	whereOperators.add("=");
//    	List<Object> whereConditionValues = new ArrayList<Object>();
//    	whereConditionValues.add(7L);
//    	try {
//			dao.updateHandler(updates, Customer.class, whereConditions, whereConditionValues, whereOperators, null);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	
//    	System.out.println(Encryption.hashPassword("password"));
    }
}
