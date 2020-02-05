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
		final String txString = "	23/01/2017	AIB-VISA-PLATINUM			phone car holder	Bills & Utilities:Mobile Phone		R	-20.77	";
		
		new Expectations() {
			{
				
			}
		};
		
		
		// Method under test
		final String result = txDataRow.getAmountString();
	}

}
