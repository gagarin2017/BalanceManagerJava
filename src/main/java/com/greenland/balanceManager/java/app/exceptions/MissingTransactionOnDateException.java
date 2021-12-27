package com.greenland.balanceManager.java.app.exceptions;

/**
 * Exception thrown when transactions not found for the date specified.
 * 
 * @author Jura
 *
 */
public class MissingTransactionOnDateException extends Exception {

	public MissingTransactionOnDateException(final String errorMessage) {
		super(errorMessage);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -961195436079802037L;

}
