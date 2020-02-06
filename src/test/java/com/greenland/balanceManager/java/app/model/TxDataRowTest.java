package com.greenland.balanceManager.java.app.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import mockit.Expectations;
import mockit.Tested;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TxDataRowTest {
	
	@Tested
	private TxDataRow txDataRow;
	
	@Test
	@DisplayName("getAmountString when credit is positive")
	public void getAmountString_credit_greaterThanZero() {
		// Setup
		final float creditAmount = 101.53f;
		final float debitAmount = 91.11f;
		
		final String expectedAmount = creditAmount+"CR";
		
		new Expectations(txDataRow) {
			{
				txDataRow.getCreditAmount();
				result = creditAmount;
				times = 2;

				txDataRow.getDebitAmount();
				result = debitAmount;
				times = 0;
			}
		};
		
		
		// Method under test
		final String resultString = txDataRow.getAmountString();
		
		assertThat("Amount string is credit amount", resultString, is(expectedAmount));
	}
	
	@Test
	@DisplayName("getAmountString when credit is negative and debit amount is positive")
	public void getAmountString_credit_lessThanZero() {
		// Setup
		final float creditAmount = -101.53f;
		final float debitAmount = 91.11f;
		
		final String expectedAmount = String.valueOf(debitAmount);
		
		new Expectations(txDataRow) {
			{
				txDataRow.getCreditAmount();
				result = creditAmount;
				times = 1;
				
				txDataRow.getDebitAmount();
				result = debitAmount;
				times = 2;
			}
		};
		
		
		// Method under test
		final String resultString = txDataRow.getAmountString();
		
		assertThat("Amount string is debit amount", resultString, is(expectedAmount));
	}
	
	@Test
	@DisplayName("getAmountString when credit is 0 and debit amount is positive")
	public void getAmountString_credit_isZero() {
		// Setup
		final float creditAmount = 0;
		final float debitAmount = 91.11f;
		
		final String expectedAmount = String.valueOf(debitAmount);
		
		new Expectations(txDataRow) {
			{
				txDataRow.getCreditAmount();
				result = creditAmount;
				times = 1;
				
				txDataRow.getDebitAmount();
				result = debitAmount;
				times = 2;
			}
		};
		
		
		// Method under test
		final String resultString = txDataRow.getAmountString();
		
		assertThat("Amount string is debit amount", resultString, is(expectedAmount));
	}
	
	@Test
	@DisplayName("getAmountString when credit is negative and debit amount is negative")
	public void getAmountString_credit_positive_debit_negative() {
		// Setup
		final float creditAmount = -10.15f;
		final float debitAmount = -1.13f;
		
		final String expectedAmount = "-";
		
		new Expectations(txDataRow) {
			{
				txDataRow.getCreditAmount();
				result = creditAmount;
				times = 1;
				
				txDataRow.getDebitAmount();
				result = debitAmount;
				times = 1;
			}
		};
		
		
		// Method under test
		final String resultString = txDataRow.getAmountString();
		
		assertThat("Amount string is \"-\"", resultString, is(expectedAmount));
	}
	
	@Test
	@DisplayName("getAmountString when credit is 0 and debit amount is 0")
	public void getAmountString_credit_and_debit_zero() {
		// Setup
		final float creditAmount = 0;
		final float debitAmount = 0;
		
		final String expectedAmount = "-";
		
		new Expectations(txDataRow) {
			{
				txDataRow.getCreditAmount();
				result = creditAmount;
				times = 1;
				
				txDataRow.getDebitAmount();
				result = debitAmount;
				times = 1;
			}
		};
		
		
		// Method under test
		final String resultString = txDataRow.getAmountString();
		
		assertThat("Amount string is \"-\"", resultString, is(expectedAmount));
	}

	
	@Test
	@DisplayName("getAmount when credit is positive")
	public void getAmountNumber_credit_greaterThanZero() {
		// Setup
		final float creditAmount = 101.53f;
		final float debitAmount = 91.11f;
		
		new Expectations(txDataRow) {
			{
				txDataRow.getCreditAmount();
				result = creditAmount;
				times = 2;

				txDataRow.getDebitAmount();
				result = debitAmount;
				times = 0;
			}
		};
		
		
		// Method under test
		final float result = txDataRow.getAmountNumber();
		
		assertThat("Amount string is credit amount", result, is(creditAmount));
	}
	
	@Test
	@DisplayName("getAmount when credit is negative and debit amount is positive")
	public void getAmountNumber_credit_lessThanZero() {
		// Setup
		final float creditAmount = -101.53f;
		final float debitAmount = 91.11f;
		
		new Expectations(txDataRow) {
			{
				txDataRow.getCreditAmount();
				result = creditAmount;
				times = 1;
				
				txDataRow.getDebitAmount();
				result = debitAmount;
				times = 2;
			}
		};
		
		// Method under test
		final float result = txDataRow.getAmountNumber();
		
		assertThat("Amount string is debit amount", result, is(debitAmount));
	}
	
	@Test
	@DisplayName("getAmountNumber when credit is 0 and debit amount is positive")
	public void getAmountNumber_credit_isZero() {
		// Setup
		final float creditAmount = 0;
		final float debitAmount = 91.11f;
		
		new Expectations(txDataRow) {
			{
				txDataRow.getCreditAmount();
				result = creditAmount;
				times = 1;
				
				txDataRow.getDebitAmount();
				result = debitAmount;
				times = 2;
			}
		};
		
		
		// Method under test
		final float result = txDataRow.getAmountNumber();
		
		assertThat("Amount string is debit amount", result, is(debitAmount));
	}
	
	@Test
	@DisplayName("getAmountNumber when credit is negative and debit amount is negative")
	public void getAmountNumber_credit_positive_debit_negative() {
		// Setup
		final float creditAmount = -10.15f;
		final float debitAmount = -1.13f;
		
		new Expectations(txDataRow) {
			{
				txDataRow.getCreditAmount();
				result = creditAmount;
				times = 1;
				
				txDataRow.getDebitAmount();
				result = debitAmount;
				times = 1;
			}
		};
		
		
		// Method under test
		final float result = txDataRow.getAmountNumber();
		
		assertThat("Amount string is \"-\"", result, is(0f));
	}
	
	@Test
	@DisplayName("getAmountNumber when credit is 0 and debit amount is 0")
	public void getAmountNumber_credit_and_debit_zero() {
		// Setup
		final float creditAmount = 0;
		final float debitAmount = 0;
		
		new Expectations(txDataRow) {
			{
				txDataRow.getCreditAmount();
				result = creditAmount;
				times = 1;
				
				txDataRow.getDebitAmount();
				result = debitAmount;
				times = 1;
			}
		};
		
		
		// Method under test
		final float result = txDataRow.getAmountNumber();
		
		assertThat("Amount string is \"-\"", result, is(0f));
	}
}
