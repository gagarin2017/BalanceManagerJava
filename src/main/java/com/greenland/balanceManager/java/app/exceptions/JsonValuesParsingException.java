package com.greenland.balanceManager.java.app.exceptions;

/**
 * Exception thrown when trying to convert the Json object into TxDataRow
 * 
 * @author Jura
 *
 */
public class JsonValuesParsingException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8709466268006489443L;

	public JsonValuesParsingException(final String errorMessage) {
		super(errorMessage);
	}
	
	public JsonValuesParsingException(final String errorMessage, final Throwable ex) {
		super(errorMessage, ex);
	}


}
