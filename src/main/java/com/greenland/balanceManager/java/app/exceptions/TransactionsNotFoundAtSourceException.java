package com.greenland.balanceManager.java.app.exceptions;

public class TransactionsNotFoundAtSourceException extends RuntimeException {

	private static final long serialVersionUID = -8996885848286843366L;

	public TransactionsNotFoundAtSourceException(final String errorText, final Throwable ex) {
		super(errorText, ex);
	}

}
