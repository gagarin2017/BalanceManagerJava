package com.greenland.balanceManager.java.app.services;

/**
 * Service which loops through the transactions from the different resources, usually remote vs local
 * and performs the comparison / check if the number of transactions match and if balances for the given period match.
 * 
 * @author Jura
 *
 */
public interface TransactionComparatorService {
	
	public void executeTransactionComparison();
	
}
