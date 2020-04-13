package com.greenland.balanceManager.java.app.services;

import java.io.FileNotFoundException;
import java.math.BigDecimal;

import org.json.JSONObject;

/**
 * Service which loops through the transactions from the different resources, usually remote vs local
 * and performs the comparison / check if the number of transactions match and if balances for the given period match.
 * 
 * @author Jura
 *
 */
public interface TransactionComparatorService {
	
	public void executeTransactionComparison() throws FileNotFoundException;

	JSONObject executeTransactionComparison(String remoteFileName, String localFileName, BigDecimal startingBalance)
			throws FileNotFoundException;
	
}
