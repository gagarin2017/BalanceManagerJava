//package com.greenland.balanceManager.java.app.model;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.is;
//
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import com.greenland.balanceManager.java.app.CommonUtils;
//import com.greenland.balanceManager.java.app.services.TransactionDataRowService;
//
//import mockit.Expectations;
//import mockit.Injectable;
//import mockit.Tested;
//
///**
// * @author Jura
// *
// */
//@Disabled
//public class TxDataRowTest {
//	
//	@Tested
//	private TxDataRow txDataRow;
//	
//	@Injectable
//	private TransactionDataRowService transactionDataRowService;
//	
//	@Test
//	@DisplayName("getAmountString when credit is positive")
//	public void getAmountString_credit_greaterThanZero() {
//		// Setup
//		final float creditAmount = 101.53f;
//		final float debitAmount = 91.11f;
//		
//		final String expectedAmount = creditAmount+"CR";
//		
//		new Expectations(txDataRow) {
//			{
//				txDataRow.getCreditAmount();
//				result = creditAmount;
//				times = 2;
//
//				txDataRow.getDebitAmount();
//				result = debitAmount;
//				times = 0;
//			}
//		};
//		
//		
//		// Method under test
//		final String resultString = txDataRow.getAmountString();
//		
//		assertThat("Amount string is credit amount", resultString, is(expectedAmount));
//	}
//	
//	@Test
//	@DisplayName("getAmountString when credit is negative and debit amount is positive")
//	public void getAmountString_credit_lessThanZero() {
//		// Setup
//		final float creditAmount = -101.53f;
//		final float debitAmount = 91.11f;
//		
//		final String expectedAmount = String.valueOf(debitAmount);
//		
//		new Expectations(txDataRow) {
//			{
//				txDataRow.getCreditAmount();
//				result = creditAmount;
//				times = 1;
//				
//				txDataRow.getDebitAmount();
//				result = debitAmount;
//				times = 2;
//			}
//		};
//		
//		
//		// Method under test
//		final String resultString = txDataRow.getAmountString();
//		
//		assertThat("Amount string is debit amount", resultString, is(expectedAmount));
//	}
//	
//	@Test
//	@DisplayName("getAmountString when credit is 0 and debit amount is positive")
//	public void getAmountString_credit_isZero() {
//		// Setup
//		final float creditAmount = 0;
//		final float debitAmount = 91.11f;
//		
//		final String expectedAmount = String.valueOf(debitAmount);
//		
//		new Expectations(txDataRow) {
//			{
//				txDataRow.getCreditAmount();
//				result = creditAmount;
//				times = 1;
//				
//				txDataRow.getDebitAmount();
//				result = debitAmount;
//				times = 2;
//			}
//		};
//		
//		
//		// Method under test
//		final String resultString = txDataRow.getAmountString();
//		
//		assertThat("Amount string is debit amount", resultString, is(expectedAmount));
//	}
//	
//	@Test
//	@DisplayName("getAmountString when credit is negative and debit amount is negative")
//	public void getAmountString_credit_positive_debit_negative() {
//		// Setup
//		final float creditAmount = -10.15f;
//		final float debitAmount = -1.13f;
//		
//		final String expectedAmount = "-";
//		
//		new Expectations(txDataRow) {
//			{
//				txDataRow.getCreditAmount();
//				result = creditAmount;
//				times = 1;
//				
//				txDataRow.getDebitAmount();
//				result = debitAmount;
//				times = 1;
//			}
//		};
//		
//		
//		// Method under test
//		final String resultString = txDataRow.getAmountString();
//		
//		assertThat("Amount string is \"-\"", resultString, is(expectedAmount));
//	}
//	
//	@Test
//	@DisplayName("getAmountString when credit is 0 and debit amount is 0")
//	public void getAmountString_credit_and_debit_zero() {
//		// Setup
//		final float creditAmount = 0;
//		final float debitAmount = 0;
//		
//		final String expectedAmount = "-";
//		
//		new Expectations(txDataRow) {
//			{
//				txDataRow.getCreditAmount();
//				result = creditAmount;
//				times = 1;
//				
//				txDataRow.getDebitAmount();
//				result = debitAmount;
//				times = 1;
//			}
//		};
//		
//		
//		// Method under test
//		final String resultString = txDataRow.getAmountString();
//		
//		assertThat("Amount string is \"-\"", resultString, is(expectedAmount));
//	}
//
//	
//	@Test
//	@DisplayName("getAmount when credit is positive")
//	public void getAmountNumber_credit_greaterThanZero() {
//		// Setup
//		final float creditAmount = 101.53f;
//		final float debitAmount = 91.11f;
//		
//		new Expectations(txDataRow) {
//			{
//				txDataRow.getCreditAmount();
//				result = creditAmount;
//				times = 2;
//
//				txDataRow.getDebitAmount();
//				result = debitAmount;
//				times = 0;
//			}
//		};
//		
//		
//		// Method under test
//		final float result = txDataRow.getAmountNumber();
//		
//		assertThat("Amount string is credit amount", result, is(creditAmount));
//	}
//	
//	@Test
//	@DisplayName("getAmount when credit is negative and debit amount is positive")
//	public void getAmountNumber_credit_lessThanZero() {
//		// Setup
//		final float creditAmount = -101.53f;
//		final float debitAmount = 91.11f;
//		
//		new Expectations(txDataRow) {
//			{
//				txDataRow.getCreditAmount();
//				result = creditAmount;
//				times = 1;
//				
//				txDataRow.getDebitAmount();
//				result = debitAmount;
//				times = 2;
//			}
//		};
//		
//		// Method under test
//		final float result = txDataRow.getAmountNumber();
//		
//		assertThat("Amount string is debit amount", result, is(debitAmount));
//	}
//	
//	@Test
//	@DisplayName("getAmountNumber when credit is 0 and debit amount is positive")
//	public void getAmountNumber_credit_isZero() {
//		// Setup
//		final float creditAmount = 0;
//		final float debitAmount = 91.11f;
//		
//		new Expectations(txDataRow) {
//			{
//				txDataRow.getCreditAmount();
//				result = creditAmount;
//				times = 1;
//				
//				txDataRow.getDebitAmount();
//				result = debitAmount;
//				times = 2;
//			}
//		};
//		
//		
//		// Method under test
//		final float result = txDataRow.getAmountNumber();
//		
//		assertThat("Amount string is debit amount", result, is(debitAmount));
//	}
//	
//	@Test
//	@DisplayName("getAmountNumber when credit is negative and debit amount is negative")
//	public void getAmountNumber_credit_positive_debit_negative() {
//		// Setup
//		final float creditAmount = -10.15f;
//		final float debitAmount = -1.13f;
//		
//		new Expectations(txDataRow) {
//			{
//				txDataRow.getCreditAmount();
//				result = creditAmount;
//				times = 1;
//				
//				txDataRow.getDebitAmount();
//				result = debitAmount;
//				times = 1;
//			}
//		};
//		
//		
//		// Method under test
//		final float result = txDataRow.getAmountNumber();
//		
//		assertThat("Amount string is 0", result, is(0f));
//	}
//	
//	@Test
//	@DisplayName("getAmountNumber when credit is 0 and debit amount is 0")
//	public void getAmountNumber_credit_and_debit_zero() {
//		// Setup
//		final float creditAmount = 0;
//		final float debitAmount = 0;
//		
//		new Expectations(txDataRow) {
//			{
//				txDataRow.getCreditAmount();
//				result = creditAmount;
//				times = 1;
//				
//				txDataRow.getDebitAmount();
//				result = debitAmount;
//				times = 1;
//			}
//		};
//		
//		
//		// Method under test
//		final float result = txDataRow.getAmountNumber();
//		
//		assertThat("Amount string is 0", result, is(0f));
//	}
//	
//	@Test
//	@DisplayName("setTransactionAmount passed remote transaction string is valid, set credit amount")
//	public void setTransactionAmount_remote_transaction_valid_date_valid_credit() {
//		// Setup
//		final String txString = 
//				"1234 **** **** 5678,03/05/2018,\"PAYMENT THANK YOU\",\"0.00 \",\" 35.17 \",\"EUR\",\"Credit\",\" 35.17 \",\"EUR\"\r\n";
//		final float expectedCreditAmount = 35.17f;
//		final String[] txStringArray = txString.split(CommonUtils.REMOTE_TX_REGEX);
//		
//		final Object[] expectedValidationResult = new Object[2];
//		expectedValidationResult[0] = true;
//		
//		new Expectations(txDataRow) {
//			{
//				transactionDataRowService.isValidTransactionRow(txStringArray);
//				result = expectedValidationResult;
//				times = 1;
//				
//				txDataRow.setCreditAmount(expectedCreditAmount);
//				times = 1;
//
//				txDataRow.setDebitAmount(anyFloat);
//				times = 0;
//			}
//		};
//		
//		// Method under test
//		txDataRow.setTransactionAmount(txStringArray);;
//		
//		// Verification
//		assertThat("Making sure that test txString length is correct", txStringArray.length, is(9));
//	}
//	
//	@Test
//	@DisplayName("setTransactionAmount passed remote transaction string is valid, set debit amount")
//	public void setTransactionAmount_remote_transaction_valid_debit() {
//		// Setup
//		final String txString = 
//				"1234 **** **** 5678,11/07/2013,\"ALDI STORES \",\"7.27 \",\"  \",\"EUR\",\"Debit\",\" 7.27 \",\"EUR\"\r\n";
//		final float expectedDebitAmount = 7.27f;
//		final String[] txStringArray = txString.split(CommonUtils.REMOTE_TX_REGEX);
//		
//		final Object[] expectedValidationResult = new Object[2];
//		expectedValidationResult[0] = true;
//		
//		new Expectations(txDataRow) {
//			{
//				transactionDataRowService.isValidTransactionRow(txStringArray);
//				result = expectedValidationResult;
//				times = 1;
//				
//				txDataRow.setCreditAmount(anyFloat);
//				times = 0;
//				
//				txDataRow.setDebitAmount(expectedDebitAmount);
//				times = 1;
//			}
//		};
//		
//		// Method under test
//		txDataRow.setTransactionAmount(txStringArray);;
//		
//		// Verification
//		assertThat("Making sure that test txString length is correct", txStringArray.length, is(9));
//	}
//	
//	@Test
//	@DisplayName("setTransactionAmount passed remote transaction string is valid, set debit amount (GBP)")
//	public void setTransactionAmount_remote_transaction_valid_debit_GBP() {
//		// Setup
//		final String txString = 
//				"1234 **** **** 5678,10/09/2015,\"AMZN \",\"41.83 \",\"  \",\"GBP\",\"Debit\",\" 35.99 \",\"GBP\"";
//		final float expectedDebitAmount = 41.83f;
//		final String[] txStringArray = txString.split(CommonUtils.REMOTE_TX_REGEX);
//		
//		final Object[] expectedValidationResult = new Object[2];
//		expectedValidationResult[0] = true;
//		
//		new Expectations(txDataRow) {
//			{
//				transactionDataRowService.isValidTransactionRow(txStringArray);
//				result = expectedValidationResult;
//				times = 1;
//				
//				txDataRow.setCreditAmount(anyFloat);
//				times = 0;
//				
//				txDataRow.setDebitAmount(expectedDebitAmount);
//				times = 1;
//			}
//		};
//		
//		// Method under test
//		txDataRow.setTransactionAmount(txStringArray);;
//		
//		// Verification
//		assertThat("Making sure that test txString length is correct", txStringArray.length, is(9));
//	}
//	
//	
//	@Test
//	@DisplayName("setTransactionAmount passed remote transaction string is valid, set credit amount (GBP)")
//	public void setTransactionAmount_remote_transaction_valid_credit_GBP() {
//		// Setup
//		final String txString = 
//				"1234 **** **** 5678,11/11/2016,\"AMZ*Oroo\",\"0.00 \",\" 29.10 \",\"GBP\",\"Credit\",\" 25.97 \",\"GBP\"";
//		final float expectedCreditAmount = 29.10f;
//		final String[] txStringArray = txString.split(CommonUtils.REMOTE_TX_REGEX);
//		
//		new Expectations(txDataRow) {
//			{
//				final Object[] expectedValidationResult = new Object[2];
//				expectedValidationResult[0] = true;
//				
//				transactionDataRowService.isValidTransactionRow(txStringArray);
//				result = expectedValidationResult;
//				times = 1;
//				
//				txDataRow.setCreditAmount(expectedCreditAmount);
//				times = 1;
//				
//				txDataRow.setDebitAmount(anyFloat);
//				times = 0;
//			}
//		};
//		
//		// Method under test
//		txDataRow.setTransactionAmount(txStringArray);;
//		
//		// Verification
//		assertThat("Making sure that test txString length is correct", txStringArray.length, is(9));
//	}
//	
//	@Test
//	@DisplayName("setTransactionAmount passed local transaction string is valid, set credit amount")
//	public void setTransactionAmount_local_transaction_valid_date_valid_credit() {
//		// Setup
//		final String txString = 
//				"	11/06/2018	AIB-VISA			Cashback award	Shopping		R	1.56	";
//		final float expectedCreditAmount = 1.56f;
//		final String[] txStringArray = txString.split("\t");
//		
//		final Object[] expectedValidationResult = new Object[2];
//		expectedValidationResult[0] = true;
//		
//		new Expectations(txDataRow) {
//			{
//				transactionDataRowService.isValidTransactionRow(txStringArray);
//				result = expectedValidationResult;
//				times = 1;
//				
//				txDataRow.setCreditAmount(expectedCreditAmount);
//				times = 1;
//				
//				txDataRow.setDebitAmount(anyFloat);
//				times = 0;
//			}
//		};
//		
//		// Method under test
//		txDataRow.setTransactionAmount(txStringArray);;
//		
//		// Verification
//		assertThat("Making sure that test txString length is correct", txStringArray.length, is(10));
//	}
//	
//	@Test
//	@DisplayName("setTransactionAmount passed local transaction string is valid, set debit amount")
//	public void setTransactionAmount_local_transaction_valid_date_valid_debit() {
//		// Setup
//		final String txString = 
//				"	08/04/2015	AIB-VISA			polo	Food & Dining:Groceries		R	-5.57	";
//		final float expectedDebitAmount = 5.57f;
//		final String[] txStringArray = txString.split("\t");
//		
//		final Object[] expectedValidationResult = new Object[2];
//		expectedValidationResult[0] = true;
//		
//		new Expectations(txDataRow) {
//			{
//				transactionDataRowService.isValidTransactionRow(txStringArray);
//				result = expectedValidationResult;
//				times = 1;
//				
//				txDataRow.setCreditAmount(anyFloat);
//				times = 0;
//				
//				txDataRow.setDebitAmount(expectedDebitAmount);
//				times = 1;
//			}
//		};
//		
//		// Method under test
//		txDataRow.setTransactionAmount(txStringArray);;
//		
//		// Verification
//		assertThat("Making sure that test txString length is correct", txStringArray.length, is(10));
//	}
//	
//	@Test
//	@DisplayName("setTransactionAmount passed remote transaction string invalid, trying to set credit amount (invalid)")
//	public void setTransactionAmount_remote_transaction_invalid_amount_invalid_coma_credit() {
//		// Setup
//		final String txString = 
//				"1234 **** **** 5678,03/05/2018,\"PAYMENT THANK YOU\",\"0.00 \",\" 3#.17 \",\"EUR\",\"Credit\",\" 3#.l7 \",\"EUR\"\r\n";
//		final float expectedCreditAmount = Float.MIN_VALUE;
//		final String[] txStringArray = txString.split(CommonUtils.REMOTE_TX_REGEX);
//		
//		final Object[] expectedValidationResult = new Object[2];
//		expectedValidationResult[0] = true;
//		
//		new Expectations(txDataRow) {
//			{
//				transactionDataRowService.isValidTransactionRow(txStringArray);
//				result = expectedValidationResult;
//				times = 1;
//				
//				txDataRow.setCreditAmount(expectedCreditAmount);
//				times = 1;
//
//				txDataRow.setDebitAmount(anyFloat);
//				times = 0;
//			}
//		};
//		
//		// Method under test
//		txDataRow.setTransactionAmount(txStringArray);;
//		
//		// Verification
//		assertThat("Making sure that test txString length is correct", txStringArray.length, is(9));
//	}
//	
//	@Test
//	@DisplayName("setTransactionAmount passed remote transaction string invalid, trying to set invalid debit amount (comma)")
//	public void setTransactionAmount_remote_transaction_amount_invalid_comma_debit() {
//		// Setup
//		final String txString = 
//				"1234 **** **** 5678,04/12/2012,\"AMAZONE\",\"67,39 \",\"  \",\"GBP\",\"Debit\",\" 58,09 \",\"GBP\"";
//		final float expectedDebitAmount = Float.MIN_VALUE;
//		final String[] txStringArray = txString.split(CommonUtils.REMOTE_TX_REGEX);
//		
//		final Object[] expectedValidationResult = new Object[2];
//		expectedValidationResult[0] = true;
//		
//		new Expectations(txDataRow) {
//			{
//				transactionDataRowService.isValidTransactionRow(txStringArray);
//				result = expectedValidationResult;
//				times = 1;
//				
//				txDataRow.setCreditAmount(anyFloat);
//				times = 0;
//
//				txDataRow.setDebitAmount(expectedDebitAmount);
//				times = 1;
//			}
//		};
//		
//		// Method under test
//		txDataRow.setTransactionAmount(txStringArray);;
//		
//		// Verification
//		assertThat("Making sure that test txString length is correct", txStringArray.length, is(9));
//	}
//	
//	@Test
//	@DisplayName("setTransactionAmount passed local transaction empty string ")
//	public void setTransactionAmount_local_transaction_empty_string() {
//		// Setup
//		final String txString = "										";
//		final String[] txStringArray = txString.split("\t");
//		
//		final Object[] expectedValidationResult = new Object[2];
//		expectedValidationResult[0] = false;
//		
//		new Expectations(txDataRow) {
//			{
//				transactionDataRowService.isValidTransactionRow(txStringArray);
//				result = expectedValidationResult;
//				times = 1;
//				
//				txDataRow.setCreditAmount(anyFloat);
//				times = 0;
//				
//				txDataRow.setDebitAmount(anyFloat);
//				times = 0;
//			}
//		};
//		
//		// Method under test
//		txDataRow.setTransactionAmount(txStringArray);;
//		
//		// Verification
//		assertThat("Making sure that test txString length is correct", txStringArray.length, is(0));
//	}
//}
