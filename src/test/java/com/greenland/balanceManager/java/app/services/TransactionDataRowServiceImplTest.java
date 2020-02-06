package com.greenland.balanceManager.java.app.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
		
		new Expectations(transactionDataRowService) {
			{
				transactionDataRowService.isValidTransactionRow(txStringArray);
				result = false;
				times = 1;
			}
		};
		
		// Method under test
		final TxDataRow resultTxDataRow = transactionDataRowService.parseLocalFileTransaction(txString);
		
		assertNull(resultTxDataRow);
	}
	
	@Test
	@DisplayName("parseLocalFileTransaction passed transaction string is valid, but date is LocalDate MIN")
	public void parseLocalFileTransaction_transaction_valid_date_LocalDate_MIN() {
		// Setup
		final String txString = "	"+LocalDate.MIN+"	AIB-VISA	";
		final String[] txStringArray = txString.split("\t");
		
		new Expectations(transactionDataRowService) {
			{
				transactionDataRowService.isValidTransactionRow(txStringArray);
				result = true;
				times = 1;
			}
		};
		
		// Method under test
		final TxDataRow resultTxDataRow = transactionDataRowService.parseLocalFileTransaction(txString);
		
		assertNull(resultTxDataRow);
	}
	
	@Test
	@DisplayName("parseLocalFileTransaction passed transaction string is valid, but date is invalid")
	public void parseLocalFileTransaction_transaction_valid_date_invalid() {
		// Setup
		final String invalidDateString = "23/0A/20AA";
		final String txString = "	" + invalidDateString + "	AIB-VISA	";
		final String[] txStringArray = txString.split("\t");
		
		new Expectations(transactionDataRowService) {
			{
				transactionDataRowService.isValidTransactionRow(txStringArray);
				result = true;
				times = 1;
			}
		};
		
		// Method under test
		final TxDataRow resultTxDataRow = transactionDataRowService.parseLocalFileTransaction(txString);
		
		assertNull(resultTxDataRow);
	}
	
	@Test
	@DisplayName("parseLocalFileTransaction passed transaction string is valid, date is valid, credit")
	public void parseLocalFileTransaction_transaction_valid_date_valid( @Mocked TxDataRow txDataRow ) {
		// Setup
		final String txString = "	23/05/2018	AIB-VISA				[Family_Budget_ACC]		R	46.95	";
		final String[] txStringArray = txString.split("\t");
		
		new Expectations(transactionDataRowService) {
			{
				transactionDataRowService.isValidTransactionRow(txStringArray);
				result = true;
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
				txDataRow.setCreditAmount(46.95f);
				txDataRow.setDebitAmount(0f); times = 0;
			}
		};
	}
	
	@Test
	@DisplayName("parseLocalFileTransaction passed transaction string is valid, date is valid, debit")
	public void parseLocalFileTransaction_transaction_valid_date_valid_credit_negative( @Mocked TxDataRow txDataRow ) {
		// Setup
		final String txString = "	23/05/2018	AIB-VISA				Bills & Utilities:Mobile Phone		R	-20.27	";
		final String[] txStringArray = txString.split("\t");
		
		new Expectations(transactionDataRowService) {
			{
				transactionDataRowService.isValidTransactionRow(txStringArray);
				result = true;
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
				txDataRow.setCategoryName("Bills & Utilities:Mobile Phone");
				txDataRow.setReconsiled(true);
				txDataRow.setCreditAmount(46.95f); times = 0;
				txDataRow.setDebitAmount(20.27f);
			}
		};
	}

}
