package com.greenland.balanceManager.java.app.exceptions;

import java.io.IOException;

public class TransactionsNotFoundAtSourceException extends Exception {

	private static final long serialVersionUID = -8996885848286843366L;

	public TransactionsNotFoundAtSourceException(final String errorText, final IOException ex) {
		super(errorText, ex);
	}

}
