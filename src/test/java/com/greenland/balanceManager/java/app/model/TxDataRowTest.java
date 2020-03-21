package com.greenland.balanceManager.java.app.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.greenland.balanceManager.java.app.CommonUtils;
import com.greenland.balanceManager.java.app.services.TransactionDataRowService;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;

/**
 * @author Jura
 *
 */
public class TxDataRowTest {
	
	@Tested
	private TxDataRow txDataRow;
	
	@Injectable
	private TransactionDataRowService transactionDataRowService;
	
	@Test
	@DisplayName("getAmountAsNumberSignedString when credit is positive")
	public void getAmountString_credit_greaterThanZero() {
		// Setup
		final BigDecimal creditAmount = new BigDecimal("101.53");
		
		final String expectedAmount = "+101.53";
		
		new Expectations(txDataRow) {
			{
				txDataRow.getCreditAmount();
				result = creditAmount;
				times = 2;

				txDataRow.getDebitAmount();
				times = 0;
			}
		};
		
		
		// Method under test
		final String resultString = txDataRow.getAmountNumberSignedString();
		
		assertThat("Amount string is credit amount", resultString, is(expectedAmount));
	}
	
	@Test
	@DisplayName("getAmountAsNumberSignedString when credit is negative and debit amount is positive")
	public void getAmountString_credit_lessThanZero_debit_positive() {
		// Setup
		final BigDecimal creditAmount = new BigDecimal("-101.53");
		final BigDecimal debitAmount = new BigDecimal("91.11");
		
		final String expectedAmount = "-"+String.valueOf(debitAmount);
		
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
		final String resultString = txDataRow.getAmountNumberSignedString();
		
		assertThat("Amount string is debit amount", resultString, is(expectedAmount));
	}
	
	@Test
	@DisplayName("getAmountAsNumberSignedString when credit is 0 and debit amount is positive")
	public void getAmountString_credit_isZero_debit_positive() {
		// Setup
		final BigDecimal creditAmount = new BigDecimal("0");
		final BigDecimal debitAmount = new BigDecimal("91.11");
		
		final String expectedAmount = "-"+String.valueOf(debitAmount);
		
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
		final String resultString = txDataRow.getAmountNumberSignedString();
		
		assertThat("Amount string is debit amount", resultString, is(expectedAmount));
	}
	
	@Test
	@DisplayName("getAmountAsNumberSignedString when credit is negative and debit amount is negative")
	public void getAmountString_credit_positive_debit_negative() {
		// Setup
		final BigDecimal creditAmount = new BigDecimal("-10.45");
		final BigDecimal debitAmount = new BigDecimal("-1.11");
		
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
		final String resultString = txDataRow.getAmountNumberSignedString();
		
		assertThat("Amount string is \"-\"", resultString, is(expectedAmount));
	}
	
	@Test
	@DisplayName("getAmountAsNumberSignedString when credit is 0 and debit amount is 0")
	public void getAmountString_credit_and_debit_zero() {
		// Setup
		final BigDecimal creditAmount = new BigDecimal("0");
		final BigDecimal debitAmount = new BigDecimal("0");
			
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
		final String resultString = txDataRow.getAmountNumberSignedString();
		
		assertThat("Amount string is \"-\"", resultString, is(expectedAmount));
	}
	
	@Test
	@DisplayName("getAmountAsNumber when credit is positive")
	public void getAmountAsNumber_credit_greaterThanZero() {
		// Setup
		final BigDecimal creditAmount = new BigDecimal("10.01");
		
		new Expectations(txDataRow) {
			{
				txDataRow.getCreditAmount();
				result = creditAmount;
				times = 2;

				txDataRow.getDebitAmount();
				times = 0;
			}
		};
		
		
		// Method under test
		final BigDecimal result = txDataRow.getAmountAsNumber();
		
		assertThat("Amount string is credit amount", result, is(creditAmount));
	}
	
	@Test
	@DisplayName("getAmountAsNumber when credit is negative and debit amount is positive")
	public void getAmountAsNumber_credit_negative_debit_positive() {
		// Setup
		final BigDecimal creditAmount = new BigDecimal("-50");
		final BigDecimal debitAmount = new BigDecimal("20");
		
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
		final BigDecimal result = txDataRow.getAmountAsNumber();
		
		assertThat("Amount string is debit amount", result, is(debitAmount));
	}
	
	@Test
	@DisplayName("getAmountAsNumber when credit is 0 and debit amount is positive")
	public void getAmountAsNumber_credit_isZero() {
		// Setup
		final BigDecimal creditAmount = new BigDecimal("-50");
		final BigDecimal debitAmount = new BigDecimal("20");
		
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
		final BigDecimal result = txDataRow.getAmountAsNumber();
		
		assertThat("Amount string is debit amount", result, is(debitAmount));
	}
	
	@Test
	@DisplayName("getAmountAsNumber when credit is negative and debit amount is negative")
	public void getAmountAsNumber_credit_positive_debit_negative() {
		// Setup
		final BigDecimal creditAmount = new BigDecimal("-50");
		final BigDecimal debitAmount = new BigDecimal("-20");
		
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
		final BigDecimal result = txDataRow.getAmountAsNumber();
		
		assertThat("Amount string is 0", result, is(new BigDecimal("0")));
	}
	
	@Test
	@DisplayName("getAmountAsNumber when credit is 0 and debit amount is 0")
	public void getAmountAsNumber_credit_and_debit_zero() {
		// Setup
		final BigDecimal creditAmount = new BigDecimal("0");
		final BigDecimal debitAmount = new BigDecimal("0");
		
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
		final BigDecimal result = txDataRow.getAmountAsNumber();
		
		assertThat("Amount string is 0", result, is(new BigDecimal("0")));
	}
	
	@Test
	@DisplayName("setTransactionAmount passed remote transaction string is valid, set credit amount")
	public void setTransactionAmount_remote_transaction_valid_date_valid_credit() {
		// Setup
		final String txString = 
				"1234 **** **** 5678,03/05/2018,\"PAYMENT THANK YOU\",\"0.00 \",\" 35.17 \",\"EUR\",\"Credit\",\" 35.17 \",\"EUR\"\r\n";
		final BigDecimal expectedCreditAmount = new BigDecimal("35.17");
		final String[] txStringArray = txString.split(CommonUtils.REMOTE_TX_REGEX);
		
		final Object[] expectedValidationResult = new Object[2];
		expectedValidationResult[0] = true;
		
		new Expectations(txDataRow) {
			{
				txDataRow.setCreditAmount(expectedCreditAmount);
				times = 1;

				txDataRow.setDebitAmount((BigDecimal) any);
				times = 0;
			}
		};
		
		// Method under test
		txDataRow.setTransactionAmount(txStringArray);;
		
		// Verification
		assertThat("Making sure that test txString length is correct", txStringArray.length, is(9));
	}
	
	@Test
	@DisplayName("setTransactionAmount passed remote transaction string is valid, set debit amount")
	public void setTransactionAmount_remote_transaction_valid_debit() {
		// Setup
		final String txString = 
				"1234 **** **** 5678,11/07/2013,\"ALDI STORES \",\"7.27 \",\"  \",\"EUR\",\"Debit\",\" 7.27 \",\"EUR\"\r\n";
		final BigDecimal expectedDebitAmount = new BigDecimal("7.27");
		final String[] txStringArray = txString.split(CommonUtils.REMOTE_TX_REGEX);
		
		final Object[] expectedValidationResult = new Object[2];
		expectedValidationResult[0] = true;
		
		new Expectations(txDataRow) {
			{
				txDataRow.setCreditAmount((BigDecimal)any);
				times = 0;
				
				txDataRow.setDebitAmount(expectedDebitAmount);
				times = 1;
			}
		};
		
		// Method under test
		txDataRow.setTransactionAmount(txStringArray);;
		
		// Verification
		assertThat("Making sure that test txString length is correct", txStringArray.length, is(9));
	}
	
	@Test
	@DisplayName("setTransactionAmount passed remote transaction string is valid, set debit amount (GBP)")
	public void setTransactionAmount_remote_transaction_valid_debit_GBP() {
		// Setup
		final String txString = 
				"1234 **** **** 5678,10/09/2015,\"AMZN \",\"41.83 \",\"  \",\"GBP\",\"Debit\",\" 35.99 \",\"GBP\"";
		final BigDecimal expectedDebitAmount = new BigDecimal("41.83");
		final String[] txStringArray = txString.split(CommonUtils.REMOTE_TX_REGEX);
		
		new Expectations(txDataRow) {
			{
				txDataRow.setCreditAmount((BigDecimal)any);
				times = 0;
				
				txDataRow.setDebitAmount(expectedDebitAmount);
				times = 1;
			}
		};
		
		// Method under test
		txDataRow.setTransactionAmount(txStringArray);;
		
		// Verification
		assertThat("Making sure that test txString length is correct", txStringArray.length, is(9));
	}
	
	
	@Test
	@DisplayName("setTransactionAmount passed remote transaction string is valid, set credit amount (GBP)")
	public void setTransactionAmount_remote_transaction_valid_credit_GBP() {
		// Setup
		final String txString = 
				"1234 **** **** 5678,11/11/2016,\"AMZ*Oroo\",\"0.00 \",\" 29.10 \",\"GBP\",\"Credit\",\" 25.97 \",\"GBP\"";
		final BigDecimal expectedCreditAmount = new BigDecimal("29.10");
		final String[] txStringArray = txString.split(CommonUtils.REMOTE_TX_REGEX);
		
		new Expectations(txDataRow) {
			{
				final Object[] expectedValidationResult = new Object[2];
				expectedValidationResult[0] = true;
				
				txDataRow.setCreditAmount(expectedCreditAmount);
				times = 1;
				
				txDataRow.setDebitAmount((BigDecimal)any);
				times = 0;
			}
		};
		
		// Method under test
		txDataRow.setTransactionAmount(txStringArray);;
		
		// Verification
		assertThat("Making sure that test txString length is correct", txStringArray.length, is(9));
	}
	
	@Test
	@DisplayName("setTransactionAmount passed local transaction string is valid, set credit amount")
	public void setTransactionAmount_local_transaction_valid_date_valid_credit() {
		// Setup
		final String txString = 
				"	11/06/2018	AIB-VISA			Cashback award	Shopping		R	1.56	";
		final BigDecimal expectedCreditAmount = new BigDecimal("1.56");
		final String[] txStringArray = txString.split("\t");
		
		final Object[] expectedValidationResult = new Object[2];
		expectedValidationResult[0] = true;
		
		new Expectations(txDataRow) {
			{
				txDataRow.setCreditAmount(expectedCreditAmount);
				times = 1;
				
				txDataRow.setDebitAmount((BigDecimal)any);
				times = 0;
			}
		};
		
		// Method under test
		txDataRow.setTransactionAmount(txStringArray);;
		
		// Verification
		assertThat("Making sure that test txString length is correct", txStringArray.length, is(10));
	}
	
	@Test
	@DisplayName("setTransactionAmount passed local transaction string is valid, set debit amount")
	public void setTransactionAmount_local_transaction_valid_date_valid_debit() {
		// Setup
		final String txString = 
				"	08/04/2015	AIB-VISA			polo	Food & Dining:Groceries		R	-5.57	";
		final BigDecimal expectedDebitAmount = new BigDecimal("5.57");
		final String[] txStringArray = txString.split("\t");
		
		final Object[] expectedValidationResult = new Object[2];
		expectedValidationResult[0] = true;
		
		new Expectations(txDataRow) {
			{
				txDataRow.setCreditAmount((BigDecimal)any);
				times = 0;
				
				txDataRow.setDebitAmount(expectedDebitAmount);
				times = 1;
			}
		};
		
		// Method under test
		txDataRow.setTransactionAmount(txStringArray);;
		
		// Verification
		assertThat("Making sure that test txString length is correct", txStringArray.length, is(10));
	}
}
