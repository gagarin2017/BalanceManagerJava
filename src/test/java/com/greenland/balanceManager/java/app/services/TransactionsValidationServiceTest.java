package com.greenland.balanceManager.java.app.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.greenland.balanceManager.java.app.CommonUtils;

import mockit.Tested;


/**
 * @author Jura
 *
 */
public class TransactionsValidationServiceTest {
	
	@Tested
	private TransactionsValidationServiceImpl transactionsValidationService;
	
	@Test
	@DisplayName("parseLocalFileTransaction passed transaction string is not valid, as date is invalid")
	public void parseLocalFileTransaction_transaction_invalid_date_invalid() {
		// Setup
		final String invalidDate = "03/AA/201B";
		final String txString = "	" + invalidDate + "	AIB-VISA				[Family_Budget_ACC]		R	46.95	";
		final String[] txStringArray = txString.split("\t");
		final Object[] expectedResult = new Object[2];
		expectedResult[0] = false;
		expectedResult[1] = null;
		
		// Method under test
		final Object[] result = transactionsValidationService.isValidTransactionRow(txStringArray);
		
		// Verification
		assertThat("Transaction string invalid", result, is(expectedResult));
	}
	
	
	@Test
	@DisplayName("isValidTransactionRow passed string is a valid remote transaction")
	public void isValidTransactionRow_remote_valid() {
		// Setup
		final String txString = 
				"1234 **** **** 5678,28/12/2017,\"Malson\",\"50.00 \",\"  \",\"EUR\",\"Debit\",\" 50.0 \",\"EUR\"\r\n";
		final String[] txStringArray = txString.split(CommonUtils.REMOTE_TX_REGEX);
		final Object[] expectedResult = new Object[2];
		expectedResult[0] = true;
		expectedResult[1] = LocalDate.of(2017, 12, 28);
		
		// Method under test
		final Object[] result = transactionsValidationService.isValidTransactionRow(txStringArray);
		
		// Verification
		assertThat("Transaction string is valid", result, is(expectedResult));
	}
	
	@Test
	@DisplayName("isValidTransactionRow transaction string is empy, hence invalid")
	public void isValidTransactionRow_remote_emptyString_invalid() {
		// Setup
		final String txString = " 								";
		final String[] txStringArray = txString.split(CommonUtils.REMOTE_TX_REGEX);
		final Object[] expectedResult = new Object[2];
		expectedResult[0] = false;
		expectedResult[1] = null;
		
		// Method under test
		final Object[] result = transactionsValidationService.isValidTransactionRow(txStringArray);
		
		// Verification
		assertThat("Transaction string invalid", result, is(expectedResult));
	}
	
	@Test
	@DisplayName("isValidTransactionRow transaction string is a remote file header, hence invalid")
	public void isValidTransactionRow_remote_fileHeader_invalid() {
		// Setup
		final String txString = "Masked Card Number, Posted Transactions Date, Description, Debit Amount, Credit Amount, Posted Currency, Transaction Type, Local Currency Amount, Local Currency";
		final String[] txStringArray = txString.split(CommonUtils.REMOTE_TX_REGEX);
		final Object[] expectedResult = new Object[2];
		expectedResult[0] = false;
		expectedResult[1] = null;
		
		// Method under test
		final Object[] result = transactionsValidationService.isValidTransactionRow(txStringArray);
		
		// Verification
		assertThat("Making sure the test data string is correct", txStringArray.length, is(9));
		assertThat("Transaction string invalid", result, is(expectedResult));
	}
	
	@Test
	@DisplayName("isValidTransactionRow passed string is a valid local transaction")
	public void isValidTransactionRow_local_valid() {
		// Setup
		final String txString = "	23/05/2018	AIB-VISA				[Family_Budget_ACC]		R	46.95	";
		final String[] txStringArray = txString.split("\t");
		final Object[] expectedResult = new Object[2];
		expectedResult[0] = true;
		expectedResult[1] = LocalDate.of(2018, 05, 23);
		
		// Method under test
		final Object[] result = transactionsValidationService.isValidTransactionRow(txStringArray);
		
		// Verification
		assertThat("Making sure the test data string is correct", txStringArray.length, is(10));
		assertThat("Transaction string invalid", result, is(expectedResult));
	}

	@Test
	@DisplayName("isValidTransactionRow transaction string is local file header, hence invalid")
	public void isValidTransactionRow_local_fileHeader_invalid() {
		// Setup
		final String txString = "	Date	Account	Num	Description	Memo	Category	Tag	Clr	Amount	";
		final String[] txStringArray = txString.split("\t");
		final Object[] expectedResult = new Object[2];
		expectedResult[0] = false;
		expectedResult[1] = null;
		
		// Method under test
		final Object[] result = transactionsValidationService.isValidTransactionRow(txStringArray);
		
		// Verification
		assertThat("Making sure the test data string is correct", txStringArray.length, is(10));
		assertThat("Transaction string invalid", result, is(expectedResult));
	}
	
	@Test
	@DisplayName("isValidTransactionRow transaction string is blank, hence invalid")
	public void isValidTransactionRow_local_blankString_invalid() {
		// Setup
		final String txString = "";
		final String[] txStringArray = txString.split("\t");
		final Object[] expectedResult = new Object[2];
		expectedResult[0] = false;
		expectedResult[1] = null;
		
		// Method under test
		final Object[] result = transactionsValidationService.isValidTransactionRow(txStringArray);
		
		// Verification
		assertThat("Making sure the test data string is correct", txStringArray.length, is(1));
		assertThat("Transaction string invalid", result, is(expectedResult));
	}
	
	@Test
	@DisplayName("isValidTransactionRow passed transaction string is not valid, as date is LocalDate MIN")
	public void isValidTransactionRow_transaction_invalid_date_LocalDate_MIN() {
		// Setup
		final String txString = "	" + LocalDate.MIN + "	AIB-VISA				[Family_Budget_ACC]		R	46.95	";
		final String[] txStringArray = txString.split("\t");
		final Object[] expectedResult = new Object[2];
		expectedResult[0] = false;
		expectedResult[1] = null;
		
		// Method under test
		final Object[] result = transactionsValidationService.isValidTransactionRow(txStringArray);
		
		// Verification
		assertThat("Transaction string invalid", result, is(expectedResult));
	}
	
	@Test
	@DisplayName("isValidTransactionRow passed transaction string is not valid, as date is LocalDate MAX")
	public void isValidTransactionRow_transaction_invalid_date_LocalDate_MAX() {
		// Setup
		final String txString = "	" + LocalDate.MAX + "	AIB-VISA				[Family_Budget_ACC]		R	46.95	";
		final String[] txStringArray = txString.split("\t");
		final Object[] expectedResult = new Object[2];
		expectedResult[0] = false;
		expectedResult[1] = null;
		
		// Method under test
		final Object[] result = transactionsValidationService.isValidTransactionRow(txStringArray);
		
		// Verification
		assertThat("Transaction string invalid", result, is(expectedResult));
	}
}
