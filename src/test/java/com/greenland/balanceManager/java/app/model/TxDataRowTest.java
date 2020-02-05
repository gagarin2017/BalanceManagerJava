package com.greenland.balanceManager.java.app.model;

import org.junit.jupiter.api.Test;

import mockit.Expectations;
import mockit.Tested;

public class TxDataRowTest {
	
	@Tested
	private TxDataRow txDataRow;
	
	@Test
	public void parseLocalFileTransaction() {
		// Setup
		new Expectations() {
			{
				
			}
		};
		
		
		// Method under test
		final String result = txDataRow.getAmountString();
	}

}
