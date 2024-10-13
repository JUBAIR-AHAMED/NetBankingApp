package com.netbanking.runner;

import com.netbanking.dao.Dao;
import com.netbanking.object.Customer;
import com.netbanking.object.Role;
import com.netbanking.object.Status;
import com.netbanking.object.User;

import java.sql.SQLException;
import java.util.Date;

public class TestRunner {

    public static void main(String[] args) {
        Dao dao = new Dao();

//         Testing User insertion
//        User user = new User(
//            10000011L,
//            "password123",
//            Role.CUSTOMER,
//            "Jubair",
//            "jubair@example.cox",
//            "9876543290",
//            new Date(103, 9, 23),
//            Status.ACTIVE,
//            System.currentTimeMillis(),
//            System.currentTimeMillis(),
//            0L
//        );
//        try {
//			dao.insert(user);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
        Customer customer = new Customer(
        		10000011L,          // customer_id
                123456789019L,  // aadhar_number
                "ABCDE12343"    // pan_number
            );
//            try {
//				dao.insert(customer);
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//            
            try {
				dao.update(customer);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

//        System.out.println("Generic DAO tests executed successfully!");
    }
}
