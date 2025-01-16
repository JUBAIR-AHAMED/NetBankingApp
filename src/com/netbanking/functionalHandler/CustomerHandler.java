package com.netbanking.functionalHandler;

import com.netbanking.dao.DataAccessObject;
import com.netbanking.enums.Role;
import com.netbanking.enums.Status;
import com.netbanking.object.Customer;
import com.netbanking.util.Redis;

public class CustomerHandler {
	public long createCustomer(Customer customer, Long userId) throws Exception {
		customer.setCreationTime(System.currentTimeMillis());
		customer.setModifiedBy(userId);
		customer.setRole(Role.CUSTOMER);
		customer.setStatus(Status.ACTIVE);
		Redis.deleteKeysWithStartString("CUSTOMER$COUNT");
		DataAccessObject<Customer> accountDao = new DataAccessObject<>();
		return accountDao.insert(customer);
	}
	
	public void updateCustomer(Customer customer, Long userId, Long key) throws Exception {
		if(customer==null) {
			return;
		}
		String cacheKey = "CUSTOMER$USER_ID:"+userId;
		if(Redis.exists(cacheKey)) {
			Redis.delete(cacheKey);
		}
		Redis.deleteKeysWithStartString("CUSTOMER$COUNT");
		customer.setCustomerId(key);
		customer.setCreationTime(System.currentTimeMillis());
		customer.setModifiedBy(userId);
		DataAccessObject<Customer> customerDao = new DataAccessObject<>();
		customerDao.update(customer);
	}
}
