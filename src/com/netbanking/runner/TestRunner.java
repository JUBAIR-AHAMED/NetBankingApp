package com.netbanking.runner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.netbanking.dao.FunctionHandler;

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
    	
//    	try {
//			System.out.println(api.getCustomer(8L));
//		} catch (CustomException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	
//    	Customer
//    	Map<String, Object> customerDetails = new HashMap<>();
//        customerDetails.put("password", "password");
//        customerDetails.put("name", "Tony Stark");
//        customerDetails.put("email", "stark@example.com");
//        customerDetails.put("mobile", "7656755309");
//        customerDetails.put("dob", new Date(102, 1, 7)); // Date for 23-10-1999
//        customerDetails.put("modifiedBy", 3L);
//        customerDetails.put("aadharNumber", 966823569120L);
//        customerDetails.put("panNumber", "YTIC7854H");
//
//        try {
//            api.createCustomer("MANAGER", customerDetails);
//            System.out.println("Customer created successfully.");
//        } catch (CustomException e) {
//            e.printStackTrace();
//        }
    	
    	//Customer
    	Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("password", "password");
        customerDetails.put("name", "Mari");
        customerDetails.put("email", "mari@example.com");
        customerDetails.put("mobile", "9776459805");
        customerDetails.put("dob", new Date(100, 11, 12)); // Date for 23-10-1999
        customerDetails.put("modifiedBy", 3L);
        customerDetails.put("aadharNumber", 909876789579L);
        customerDetails.put("panNumber", "QRTYU6768L");

        try {
            api.createCustomer(customerDetails);
            System.out.println("Customer created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    	
    	//Eployee
//    	Map<String, Object> employeeDetails = new HashMap<>();
//    	employeeDetails.put("password", "12345");
//    	employeeDetails.put("name", "Gandhi");
//    	employeeDetails.put("email", "gandhi@asd.com");
//    	employeeDetails.put("mobile", "9247274243");
//    	employeeDetails.put("dob", new Date(91, 3, 12));
//    	employeeDetails.put("modifiedBy", 8L);
//    	employeeDetails.put("role", "EMPLOYEE");
//    	try {
//			api.createEmployee("MANAGER", employeeDetails);
//		} catch (CustomException e) {
//			e.printStackTrace();
//		}
    	
        
        //Branch
//    	Map<String, Object> branchDetails = new HashMap<>();
//        branchDetails.put("name", "Kovai Branch");
//        branchDetails.put("ifsc", 1234567891L);
//        branchDetails.put("employeeId", 8L);
//        branchDetails.put("address", "223, Mani St, Kovai, India");
//        branchDetails.put("modifiedBy", 3L);

//        try {
//            api.createBranch("MANAGER", branchDetails);
//            System.out.println("Branch created successfully.");
//        } catch (CustomException e) {
//            e.printStackTrace();
//        }
        
        //Account
//    	Map<String, Object> accountDetails = new HashMap<>();
//        accountDetails.put("userId", 4L);
//        accountDetails.put("branchId", 6L);
//        accountDetails.put("accountType", "Current");
//        accountDetails.put("balance", 10000.00F);
//        accountDetails.put("status", "ACTIVE");
//        accountDetails.put("modifiedBy", 3L);
//        try {
//            api.createAccount("MANAGER", accountDetails);
//            System.out.println("Account created successfully.");
//        } catch (CustomException e) {
//            e.printStackTrace();
//        }
    	
//    	Map<String, Object> accountDetails = new HashMap<>();
//        accountDetails.put("userId", 5L);
//        accountDetails.put("branchId", 6L);
//        accountDetails.put("accountType", "SAVINGS");
//        accountDetails.put("balance", 10000.00F);
//        accountDetails.put("status", "ACTIVE");
//        accountDetails.put("modifiedBy", 1L);
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
    	
//    	Map<String, Object> accMap = new HashMap<String, Object>();
//    	accMap.put("userId", 9L);
//    	accMap.put("branchId", 1L);
//    	accMap.put("accountType", "SAVINGS");
//    	accMap.put("balance", 15987L);
//    	accMap.put("status", "ACTIVE");
//    	accMap.put("modifiedBy", 6L);
//    	
//    	try {
//    		api.createAccount("MANAGER", accMap);
//    	} catch (Exception e) {
//    		e.printStackTrace();
//		}
    	
    	//getStatement
//    	try {
//			System.out.println(api.getTransactionStatement(776784567877L, 0L, 1729999825500L));
//		} catch (CustomException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	
//    	try {
//			System.out.println(api.accountIsValid(80960987234520L));
//		} catch (CustomException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	try {
//			System.out.println(api.getProfile(3L, "EMPLOYEE"));
//		} catch (CustomException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
    }
}
