package com.greenland.balanceManager.java.app;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public interface CommonUtils {
	
	public static final String REMOTE_TX_REGEX = "(,)(?=(?:[^\"]|\"[^\"]*\")*$)";
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("d/MM/yyyy");
	
	// Amount to start balance calculations with.
	/*
	 * 
	 */
	public static final BigDecimal START_AMOUNT = new BigDecimal("77.94");
	
	
	public static final String TX_RULE = "txs_rule";

}
