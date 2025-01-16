package com.netbanking.functionalHandler;

import com.netbanking.dao.DataAccessObject;
import com.netbanking.enums.Status;
import com.netbanking.object.Employee;
import com.netbanking.util.Encryption;
import com.netbanking.util.Redis;

public class EmployeeHandler {
	public long createEmployee(Employee employee, Long userId) throws Exception {
		employee.setPassword(Encryption.hashPassword(employee.getPassword()));
		employee.setStatus(Status.ACTIVE);
		employee.setCreationTime(System.currentTimeMillis());
		employee.setModifiedBy(userId);
	    Redis.deleteKeysWithStartString("EMPLOYEE$COUNT");
	    DataAccessObject<Employee> accountDao = new DataAccessObject<>();
		return accountDao.insert(employee);
	}
	
	public void updateEmployee(Employee employee, Long userId, Long key) throws Exception {
		if(employee==null) {
			return;
		}
		String cacheKey = "EMPLOYEE$USER_ID:"+userId;
		if(Redis.exists(cacheKey)) {
			Redis.delete(cacheKey);
		}
		Redis.deleteKeysWithStartString("EMPLOYEE$COUNT");
		employee.setEmployeeId(key);
		employee.setCreationTime(System.currentTimeMillis());
		employee.setModifiedBy(userId);
		DataAccessObject<Employee> employeeDao = new DataAccessObject<>();
		employeeDao.update(employee);
	}
}
