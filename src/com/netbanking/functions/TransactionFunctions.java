package com.netbanking.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.netbanking.dao.DataAccessObject;
import com.netbanking.daoObject.QueryRequest;
import com.netbanking.enumHelper.GetMetadata;
import com.netbanking.enums.TransactionType;
import com.netbanking.exception.CustomException;
import com.netbanking.object.Account;
import com.netbanking.object.Transaction;
import com.netbanking.util.ApiHelper;
import com.netbanking.util.Converter;
import com.netbanking.util.Redis;
import com.netbanking.util.UserDetailsLocal;
import com.netbanking.util.Validator;

public class TransactionFunctions {
	public List<Map<String, Object>> getStatement(Map<String, Object> requestData, Map<String, Object> accountData) throws CustomException, Exception {
		Long accountNumber = (Long) requestData.get("accountNumber");
		Long fromDate = (Long) requestData.getOrDefault("fromDate", null);
		Long toDate = (Long) requestData.getOrDefault("toDate", null);
		Integer limit = (Integer) requestData.getOrDefault("limit", null);
		Integer currentPage = (Integer) requestData.getOrDefault("currentPage", null);
		Boolean count = (Boolean) requestData.getOrDefault("count", false);
        Integer offset = ApiHelper.getOffset(limit, currentPage);

		// Query Request Building
		QueryRequest request = new QueryRequest()
									.setTableName("transaction")
									.setSelectAllColumns(true)
									.putWhereConditions("accountNumber")
									.putWhereOperators("=")
									.putWhereConditionsValues(accountNumber);
		if(fromDate!=null) {
			request.putWhereConditions("timestamp")
					.putWhereConditionsValues(fromDate)
					.putWhereOperators(">=")
					.putWhereLogicalOperators("AND");
		}
		if(toDate!=null) {
			request.putWhereConditions("timestamp")
					.putWhereConditionsValues(toDate)
					.putWhereOperators("<=")
					.putWhereLogicalOperators("AND");
		}
		DataAccessObject<Transaction> transactionHandle = new DataAccessObject<Transaction>();		
		List<String> orderByColumn = new ArrayList<String>(), orderDirections = new ArrayList<String>();
		orderByColumn.add("timestamp");
		orderDirections.add("DESC");
		request.setOrderByColumns(orderByColumn)
				.setOrderDirections(orderDirections)
				.setLimit(limit)
				.setOffset(offset)
				.setCount(count);
		return transactionHandle.select(request);
	}
	
	public void initiateTransaction(Map<String, Object> details, Map<String, Object> fromAccountMap, Map<String, Object> toAccountMap) throws CustomException, Exception {
		UserDetailsLocal store = UserDetailsLocal.get();
		Long userId = store.getUserId();
		
		Long fromAccountNumber = (Long) details.get("fromAccount");
		Long toAccountNumber = (Long) details.getOrDefault("toAccount", null);
		Float amount = (Float) details.get("amount");
		String transactionType = (String) details.get("transactionType");

		Float fromAccountBalance = null, toAccountBalance = null;
		Object frombalance = fromAccountMap.get("balance");

		if (frombalance instanceof Double) {
		    fromAccountBalance = ((Double) frombalance).floatValue();
		} else if (frombalance instanceof Float) {
		    fromAccountBalance = (Float) frombalance;
		}

		if(!transactionType.equals("deposit") && fromAccountBalance < amount) {
			throw new CustomException(422, "Balance Not Enough");
		}
		
		if(transactionType.equals("same-bank"))
		{
			Validator.checkInvalidInput(toAccountNumber);
			Object toBalance = toAccountMap.get("balance");
			if (toBalance instanceof Double) {
			    toAccountBalance = ((Double) toBalance).floatValue();
			} else if (toBalance instanceof Float) {
			    toAccountBalance = (Float) toBalance;
			}
			toAccountBalance += amount;
		}
		
		if(transactionType.equals("deposit"))
		{
			fromAccountBalance += amount;
		} else {
			fromAccountBalance -= amount;
		}
		Account from_account = new Account();
		from_account.setBalance(fromAccountBalance);
		from_account.setAccountNumber(fromAccountNumber);
		DataAccessObject<Account> daoCaller = new DataAccessObject<Account>();
		daoCaller.update(from_account);
		if(transactionType.equals("same-bank"))
		{
			Account to_account = new Account();
			to_account.setBalance(toAccountBalance);
			to_account.setAccountNumber(toAccountNumber);
			daoCaller.update(to_account);
		}
		Long fromUserId = Converter.convertToLong(fromAccountMap.get("userId"));
		Long toUserId = null;
		if(toAccountMap!=null) {
			toUserId = Converter.convertToLong(toAccountMap.get("userId"));
		}
		storeTransaction(fromAccountNumber, toAccountNumber,
				fromUserId, toUserId,
				userId, amount, fromAccountBalance, 
				toAccountBalance, transactionType);
		GetMetadata metadata = GetMetadata.ACCOUNT;
		String cacheKeyFromAcc = metadata.getCachKey()+fromAccountNumber;
		String cacheKeyToAcc = metadata.getCachKey()+toAccountNumber;
		Redis.delete(cacheKeyFromAcc);
		Redis.delete(cacheKeyToAcc);
	}
	
	private void storeTransaction(Long from_account, Long to_account, 
			Long from_user_id, Long to_user_id,
			Long user_id, Float amount, 
			Float from_account_balance, Float to_account_balance, String transactionType) throws Exception
	{
		DataAccessObject<Transaction> transactionHandle = new DataAccessObject<Transaction>();
		Transaction transaction_acc_one = new Transaction();
		transaction_acc_one.setAccountNumber(from_account);
		transaction_acc_one.setBalance(from_account_balance);
		transaction_acc_one.setModifiedBy(user_id);
		transaction_acc_one.setTimestamp(System.currentTimeMillis());
		transaction_acc_one.setTransactionAccount(to_account);
		transaction_acc_one.setTransactionAmount(amount);
		transaction_acc_one.setUserId(from_user_id);
		transaction_acc_one.setCreationTime(System.currentTimeMillis());
		
		if(transactionType.equalsIgnoreCase("same-bank")||transactionType.equalsIgnoreCase("other-bank")) {
			transaction_acc_one.setType(TransactionType.DEBIT.toString());
		} else if(transactionType.equalsIgnoreCase("deposit")){
			transaction_acc_one.setType(TransactionType.DEPOSIT.toString());
		} else if(transactionType.equalsIgnoreCase("withdraw")) {
			transaction_acc_one.setType(TransactionType.WITHDRAW.toString());
		} 
		Long ref_number = transactionHandle.insert(transaction_acc_one);
		
		if(transactionType.equals("same-bank"))
		{
			Transaction transaction_acc_two = new Transaction();
			transaction_acc_two.setAccountNumber(to_account);
			transaction_acc_two.setBalance(to_account_balance);
			transaction_acc_two.setModifiedBy(user_id);
			transaction_acc_two.setTimestamp(System.currentTimeMillis());
			transaction_acc_two.setTransactionAccount(from_account);
			transaction_acc_two.setTransactionAmount(amount);
			transaction_acc_two.setUserId(to_user_id);
			transaction_acc_two.setReferenceNumber(ref_number);
			transaction_acc_two.setCreationTime(System.currentTimeMillis());
			transaction_acc_two.setType(TransactionType.CREDIT.toString());
			transactionHandle.insert(transaction_acc_two);
		}
	}
}
