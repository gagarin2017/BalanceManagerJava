package com.greenland.balanceManager.java.app.services;

import java.math.BigDecimal;
import java.util.List;

import org.json.JSONObject;

import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
import com.greenland.balanceManager.java.app.model.TxDataRow;

/**
 * Service which loops through the transactions from the different resources, usually remote vs local
 * and performs the comparison / check if the number of transactions match and if balances for the given period match.
 * 
 * @author Jura
 *
 */
public interface TransactionComparatorService {
	
	public void executeTransactionComparison() throws TransactionsNotFoundAtSourceException;

	JSONObject executeTransactionComparison(String remoteFileName, String localFileName, BigDecimal startingBalance)
			throws TransactionsNotFoundAtSourceException;
	
	JSONObject executeTransactionComparison(JSONObject remoteTransactions, JSONObject localTransactions, BigDecimal startingBalance)
			throws TransactionsNotFoundAtSourceException;

	public List<TxDataRow> getAllTransactions() throws TransactionsNotFoundAtSourceException;
	
}
