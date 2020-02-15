package com.greenland.balanceManager.java.app.exceptions;

/**
 * Exception thrown when transactions are not found
 * 
 * @author Jura
 *
 */
public class TransactionsNotFoundException extends RuntimeException {

	public TransactionsNotFoundException(final String errorMessage) {
		super(errorMessage);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -961195436079802037L;

}
