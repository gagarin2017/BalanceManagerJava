package com.greenland.balanceManager.java.app.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.greenland.balanceManager.java.app.CommonUtils;
import com.greenland.balanceManager.java.app.model.TxDataRow;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;

public class TransactionDataRowServiceImplTest {
	
	@Tested
	private TransactionDataRowServiceImpl transactionDataRowService;
	
	@Test
	@DisplayName("parseLocalFileTransaction passed transaction string is invalid")
	public void parseLocalFileTransaction_transaction_not_valid() {
		// Setup
		final String txString = "	transa	ction	";
		final String[] txStringArray = txString.split("\t");
		final Object[] expectedResult = new Object[2];
		expectedResult[0] = false;
		expectedResult[1] = null;
		
		new Expectations(transactionDataRowService) {
			{
				transactionDataRowService.isValidTransactionRow(txStringArray);
				result = expectedResult;
				times = 1;
			}
		};
		
		// Method under test
		final TxDataRow resultTxDataRow = transactionDataRowService.parseLocalFileTransaction(txString);
		
		assertNull(resultTxDataRow);
	}
	
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
		final Object[] result = transactionDataRowService.isValidTransactionRow(txStringArray);
		
		// Verification
		assertThat("Transaction string invalid", result, is(expectedResult));
	}
	
	@Test
	@DisplayName("parseLocalFileTransaction passed transaction string is valid, date is valid, credit")
	public void parseLocalFileTransaction_transaction_valid_date_valid( @Mocked TxDataRow txDataRow ) {
		// Setup
		final String txString = "	23/05/2018	AIB-VISA				[Family_Budget_ACC]		R	46.95	";
		final String[] txStringArray = txString.split("\t");
		final Object[] expectedResult = new Object[2];
		expectedResult[0] = true;
		expectedResult[1] = LocalDate.of(2018, 05, 23);
		
		new Expectations(transactionDataRowService) {
			{
				transactionDataRowService.isValidTransactionRow(txStringArray);
				result = expectedResult;
				times = 1;
			}
		};
		
		// Method under test
		final TxDataRow resultTxDataRow = transactionDataRowService.parseLocalFileTransaction(txString);
		
		// Verification
		assertNotNull(resultTxDataRow);
		assertThat("Making sure that test txString is correct", txStringArray.length, is(10));
		
		new Verifications() {
			{
				final List<TxDataRow> list = withCapture(new TxDataRow());
				assertThat("Make sure one TxDataRow instance is created", list.size(), is(1));
				
				final TxDataRow txDataRow = list.get(0);
				txDataRow.setTxDate(LocalDate.of(2018, 05, 23));
				txDataRow.setAccountName("AIB-VISA");
				txDataRow.setCategoryName("[Family_Budget_ACC]");
				txDataRow.setReconsiled(true);
				txDataRow.setTransactionAmount(txStringArray);
			}
		};
	}
	
	@Test
	@DisplayName("parseLocalFileTransaction passed transaction string is valid, date is valid, debit")
	public void parseLocalFileTransaction_transaction_valid_date_valid_credit_negative( @Mocked TxDataRow txDataRow ) {
		// Setup
		final String txString = "	23/05/2018	AIB-VISA				Bills & Utilities:Mobile Phone		R	-20.27	";
		final LocalDate txDate = LocalDate.of(2018, 05, 23);
		final String[] txStringArray = txString.split("\t");
		
		final Object[] expectedResult = new Object[2];
		expectedResult[0] = true;
		expectedResult[1] = txDate;
		
		new Expectations(transactionDataRowService) {
			{
				transactionDataRowService.isValidTransactionRow(txStringArray);
				result = expectedResult;
				times = 1;
			}
		};
		
		// Method under test
		final TxDataRow resultTxDataRow = transactionDataRowService.parseLocalFileTransaction(txString);
		
		// Verification
		assertNotNull(resultTxDataRow);
		assertThat("Making sure that test txString is correct", txStringArray.length, is(10));
		
		new Verifications() {
			{
				final List<TxDataRow> list = withCapture(new TxDataRow());
				assertThat("Make sure one TxDataRow instance is created", list.size(), is(1));
				
				final TxDataRow txDataRow = list.get(0);
				txDataRow.setTxDate(txDate);
				txDataRow.setAccountName("AIB-VISA");
				txDataRow.setCategoryName("Bills & Utilities:Mobile Phone");
				txDataRow.setReconsiled(true);
				txDataRow.setTransactionAmount(txStringArray);
			}
		};
	}
	
	@Test
	@DisplayName("parseRemoteFileTransaction passed transaction string is valid, date is valid, credit")
	public void parseRemoteFileTransaction_transaction_valid_date_valid_credit( @Mocked TxDataRow txDataRow ) {
		// Setup
		final String txString = 
				"1234 **** **** 5678,03/05/2018,\"PAYMENT THANK YOU\",\"0.00 \",\" 35.17 \",\"EUR\",\"Credit\",\" 35.17 \",\"EUR\"\r\n";
		final LocalDate txDate = LocalDate.of(2018, 05, 03);
		final String[] txStringArray = txString.split(CommonUtils.REMOTE_TX_REGEX);

		final int txStringLength = 9;
		
		final Object[] expectedResult = new Object[2];
		expectedResult[0] = true;
		expectedResult[1] = txDate;
		
		new Expectations(transactionDataRowService) {
			{
				transactionDataRowService.isValidTransactionRow(txStringArray);
				result = expectedResult;
				times = 1;
			}
		};
		
		// Method under test
		final TxDataRow resultTxDataRow = transactionDataRowService.parseRemoteFileTransaction(txString);
		
		// Verification
		assertThat("Making sure that test txString is correct", txStringArray.length, is(txStringLength));
		assertNotNull(resultTxDataRow);
		
		new Verifications() {
			{
				final List<TxDataRow> list = withCapture(new TxDataRow());
				assertThat("Make sure one TxDataRow instance is created", list.size(), is(1));
				
				final TxDataRow txDataRow = list.get(0);
				txDataRow.setTxDate(txDate);
				txDataRow.setAccountName("1234 **** **** 5678");
				txDataRow.setCategoryName("\"PAYMENT THANK YOU\"");
				txDataRow.setTransactionAmount(txStringArray);
			}
		};
	}
	
	@Test
	@Disabled
	@DisplayName("parseRemoteFileTransaction passed transaction string is not valid")
	public void parseRemoteFileTransaction_transaction_invalid( @Mocked TxDataRow txDataRow ) {
		// Setup
		final String txString = 
				"1234 **** **** 5678,03/05/2018,\"PAYMENT THANK YOU\",\"0.00 \",\" 35.17 \",\"EUR\",\"Credit\",\" 35.17 \",\"EUR\"\r\n";
		final String[] txStringArray = txString.split(CommonUtils.REMOTE_TX_REGEX);
		final int txStringLength = 9;
		
		new Expectations(transactionDataRowService) {
			{
				transactionDataRowService.isValidTransactionRow(txStringArray);
				result = false;
				times = 1;
			}
		};
		
		// Method under test
		final TxDataRow resultTxDataRow = transactionDataRowService.parseRemoteFileTransaction(txString);
		
		// Verification
		assertThat("Making sure that test txString is correct", txStringArray.length, is(txStringLength));
		assertNull(resultTxDataRow);
	}
	
	@Test
	@DisplayName("parseRemoteFileTransaction passed transaction string is valid, date is valid, debit")
	public void parseRemoteFileTransaction_transaction_valid_date_valid_debit( @Mocked TxDataRow txDataRow ) {
		// Setup
		final String txString = 
				"1234 **** **** 5678,28/12/2017,\"Malson\",\"50.00 \",\"  \",\"EUR\",\"Debit\",\" 50.0 \",\"EUR\"\r\n";
		final LocalDate txDate = LocalDate.of(2017, 12, 28);
		final String[] txStringArray = txString.split(CommonUtils.REMOTE_TX_REGEX);
		
		final int txStringLength = 9;

		final Object[] expectedResult = new Object[2];
		expectedResult[0] = true;
		expectedResult[1] = txDate;
		
		new Expectations(transactionDataRowService) {
			{
				transactionDataRowService.isValidTransactionRow(txStringArray);
				result = expectedResult;
				times = 1;
			}
		};
		
		// Method under test
		final TxDataRow resultTxDataRow = transactionDataRowService.parseRemoteFileTransaction(txString);
		
		// Verification
		assertThat("Making sure that test txString is correct", txStringArray.length, is(txStringLength));
		assertNotNull(resultTxDataRow);
		
		new Verifications() {
			{
				final List<TxDataRow> list = withCapture(new TxDataRow());
				assertThat("Make sure one TxDataRow instance is created", list.size(), is(1));
				
				final TxDataRow txDataRow = list.get(0);
				txDataRow.setTxDate(txDate);
				txDataRow.setAccountName("1234 **** **** 5678");
				txDataRow.setCategoryName("\"Malson\"");
				txDataRow.setTransactionAmount(txStringArray);
			}
		};
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
		final Object[] result = transactionDataRowService.isValidTransactionRow(txStringArray);
		
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
		final Object[] result = transactionDataRowService.isValidTransactionRow(txStringArray);
		
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
		final Object[] result = transactionDataRowService.isValidTransactionRow(txStringArray);
		
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
		final Object[] result = transactionDataRowService.isValidTransactionRow(txStringArray);
		
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
		final Object[] result = transactionDataRowService.isValidTransactionRow(txStringArray);
		
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
		final Object[] result = transactionDataRowService.isValidTransactionRow(txStringArray);
		
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
		final Object[] result = transactionDataRowService.isValidTransactionRow(txStringArray);
		
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
		final Object[] result = transactionDataRowService.isValidTransactionRow(txStringArray);
		
		// Verification
		assertThat("Transaction string invalid", result, is(expectedResult));
	}
}
