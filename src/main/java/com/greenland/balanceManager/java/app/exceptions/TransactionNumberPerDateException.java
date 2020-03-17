package com.greenland.balanceManager.java.app.exceptions;

/**
 * Exception thrown when transactions are not found
 * 
 * @author Jura
 *
 */
public class TransactionNumberPerDateException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8709466268006489443L;

	public TransactionNumberPerDateException(final String errorMessage) {
		super(errorMessage);
	}


}
