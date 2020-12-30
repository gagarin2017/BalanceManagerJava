package com.greenland.balanceManager.java.app.exceptions;

import java.io.FileNotFoundException;

public class TransactionsNotFoundAtSourceException extends Exception {

	private static final long serialVersionUID = -8996885848286843366L;

	public TransactionsNotFoundAtSourceException(final String errorText, final FileNotFoundException ex) {
		super(errorText, ex);
	}

}
