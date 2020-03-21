package com.greenland.balanceManager.java.app.services;

import java.time.LocalDate;

/**
 * Service to perform transaction validations
 * 
 * @author Jura
 *
 */
public interface TransactionsValidationService {

	/**
	 * Checks if passed transaction array is a valid transaction.
	 * Transaction string is valid if [1] element of the passed array is a valid {@link LocalDate}
	 * 
	 * @param txRowArray
	 * 			transaction in array format
	 * 
	 * @return Object[2] array where first element is a flag true - if valid, false - if invalid;
	 * and second element is the transaction date
	 */
	Object[] isValidTransactionRow(String[] txRowArray);

	

}
