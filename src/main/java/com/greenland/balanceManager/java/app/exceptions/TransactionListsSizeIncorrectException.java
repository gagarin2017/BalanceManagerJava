package com.greenland.balanceManager.java.app.exceptions;

/**
 * Exception thrown when transactions are not found
 * 
 * @author Jura
 *
 */
public class TransactionListsSizeIncorrectException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8709466268006489443L;

	public TransactionListsSizeIncorrectException(final String errorMessage) {
		super(errorMessage);
	}


}
