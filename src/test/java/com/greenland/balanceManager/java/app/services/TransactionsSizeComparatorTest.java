package com.greenland.balanceManager.java.app.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.greenland.balanceManager.java.app.CommonUtils;
import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
import com.greenland.balanceManager.java.app.model.TxDataRow;

/**
 * @author Yura
 *
 */
@DisplayName("TransactionSizeComparator should return the expected result where the input has:")
public class TransactionsSizeComparatorTest {
	
	private static TransactionsSizeComparator transactionsSizeComparator;
	
	private Map<LocalDate, List<TxDataRow>> remoteTransactions;
	private Map<LocalDate,List<TxDataRow>> localTransactions;
	private List<String> infoMessages;
	private List<String> errorMessages;
	
	private static final String SOME_LOCAL_TRANSACTIONS_ARE_MISSING_INFO = "Some localTransactions are missing";
	private static final String LOCAL = "LOCAL";
	private static final LocalDate MAY_22_2021 = LocalDate.of(2021, 5, 22);
	private static final LocalDate MAY_28_2021 = LocalDate.of(2021, 5, 28);
	private static final LocalDate MAY_29_2021 = LocalDate.of(2021, 5, 29);
	private static final LocalDate JUN_12_2021 = LocalDate.of(2021, 6, 12);
	private static final LocalDate JUL_2_2021 = LocalDate.of(2021, 7, 2);
	private static final LocalDate JUL_13_2021 = LocalDate.of(2021, 7, 13);
	
	private static final String TX_ACC_NAME_1="Bank account";
	private static final String TX_CAT_NAME_1="Groceries";
	private static final String TX_DESC_1="some groceries";
	private static final BigDecimal TX_AMOUNT_1 = new BigDecimal("23.18");
	private static final BigDecimal TX_AMOUNT_2 = new BigDecimal("77.11");
	private static final BigDecimal TX_AMOUNT_3 = new BigDecimal("101.86");
	private static final BigDecimal TX_AMOUNT_4 = new BigDecimal("57.89");
	
	private static final TxDataRow txDataRow1 = new TxDataRow();
	private static final TxDataRow txDataRow2 = new TxDataRow();
	private static final TxDataRow txDataRow3 = new TxDataRow();
	private static final TxDataRow txDataRow4 = new TxDataRow();
	private static final TxDataRow txDataRow5 = new TxDataRow();
	private static final TxDataRow txDataRow6 = new TxDataRow();
	
	private final static String NUMBER_OF_DAYS_WITH_TX_INFO_MSG = 
			"Comparing transactions for each date Remote [%d days with transactions] vs Local [%d days with transactions].";
	
	private final static String TRANSACTIONS_NOT_FOUND_FOR_DATE_ERROR_MSG = 
			"%s Transactions not found for the date: %s";
	private final static String TRANSACTION_MAP_DONT_MATCH_ERROR_MSG = 
			"Map sizes do not match. Remote dates [%s - %s], size: %d. Local dates [%s - %s], size %d";
	
	@BeforeAll
	static void init() {
		transactionsSizeComparator = new TransactionsSizeComparatorImpl();
		setupTestTransactions();
	}
	
	@BeforeEach
	void setup() {
		remoteTransactions = new TreeMap<>();
		localTransactions = new TreeMap<>();
		infoMessages = new ArrayList<>();
		errorMessages = new ArrayList<>();
	}
	
	@Test
	@DisplayName("no remote nor local transactions")
	void compareTransactionListSizes_empty_lists() throws TransactionsNotFoundAtSourceException {
		// Setup
    	final String expectedInfoMessage = String.format(NUMBER_OF_DAYS_WITH_TX_INFO_MSG, 0, 0);
    	
		// Method under test
		final Map<String, Map<LocalDate, List<TxDataRow>>> result = transactionsSizeComparator.compareRemoteVsLocalTransactions(remoteTransactions, localTransactions, infoMessages, errorMessages);
		
		// Verifications		
		assertNotNull(result);
		assertThat("There is no missing remote transactions", result.get(CommonUtils.REMOTE_TRANSACTIONS_JSON_KEY), is(Collections.EMPTY_MAP));
		assertThat("There is no missing local transactions", result.get(CommonUtils.LOCAL_TRANSACTIONS_JSON_KEY), is(Collections.EMPTY_MAP));
		assertThat("There is one info message", infoMessages.size(), is(1));
		assertThat("Info text message as expected", infoMessages.get(0), is(expectedInfoMessage));
		assertThat("There are no error messages", errorMessages.size(), is(0));
	}
	
	@Test
	@DisplayName("the REMOTE map with 1 day and 2 transactions; LOCAL map has 0 days with но transactions.")
	public void compareTransactionListSizes_remote_1_tx_local_empty() throws TransactionsNotFoundAtSourceException {
		// Setup
		final List<TxDataRow> date1Transactions = Arrays.asList(txDataRow1, txDataRow2);
		remoteTransactions.put(MAY_22_2021, date1Transactions);
		
		final Map<LocalDate, List<TxDataRow>> expectedLocalTransactions = new HashMap<>();
		expectedLocalTransactions.put(MAY_22_2021, date1Transactions);
		
		// Method under test
		final Map<String, Map<LocalDate, List<TxDataRow>>> result = transactionsSizeComparator.compareRemoteVsLocalTransactions(remoteTransactions, localTransactions, infoMessages, errorMessages);
		
		// Verifications
		assertTransactions(result, Collections.emptyMap(), expectedLocalTransactions);
		
		// Verify info messages
		assertThat(infoMessages.size(), is(2));
		assertThat("Info text message as expected", infoMessages.get(0), is(String.format(NUMBER_OF_DAYS_WITH_TX_INFO_MSG, 1, 0)));
		assertThat("Info text message as expected", infoMessages.get(1), is(SOME_LOCAL_TRANSACTIONS_ARE_MISSING_INFO));

		// Verify error messages
		assertThat("There are no error messages", errorMessages.size(), is(0));
	}
	
	@Test
	@DisplayName("the LOCAL map with 2 days and 3 transactions [2,1]; REMOTE map has 0 days with transactions.")
	public void compareTransactionListSizes_remote_2_days_local_none() throws TransactionsNotFoundAtSourceException {
		// Setup
		final List<TxDataRow> expectedDate1Transactions = Arrays.asList(txDataRow1, txDataRow2);
		final List<TxDataRow> expectedDate2Transactions = Arrays.asList(txDataRow1);
		
		localTransactions.put(MAY_28_2021, expectedDate1Transactions);
		localTransactions.put(MAY_29_2021, expectedDate2Transactions);
		
		final String expectedInfoMessage = String.format(NUMBER_OF_DAYS_WITH_TX_INFO_MSG, 0, 2);
		
		final Map<LocalDate, List<TxDataRow>> expectedRemoteTransactions = new HashMap<>();
		expectedRemoteTransactions.put(MAY_28_2021, expectedDate1Transactions);
		expectedRemoteTransactions.put(MAY_29_2021, expectedDate2Transactions);
		
		// Method under test
		final Map<String, Map<LocalDate, List<TxDataRow>>> result = transactionsSizeComparator.compareRemoteVsLocalTransactions(remoteTransactions, localTransactions, infoMessages, errorMessages);
		
		// Verifications		
		assertTransactions(result, expectedRemoteTransactions, Collections.emptyMap());
		
		// Verify info messages
		assertThat("There are two info messages", infoMessages.size(), is(2));
		assertThat("Info text message as expected", infoMessages.get(0), is(expectedInfoMessage));
		assertThat("Info text message as expected", infoMessages.get(1), is("Some remoteTransactions are missing"));
		
		// Verify error messages
		assertThat("There are no error messages", errorMessages.size(), is(0));
	}
	
	@Test
	@DisplayName("the REMOTE map with 2 days and 3 transactions [2,1]; LOCAL map has only 1 day (the same date) with [2] transactions.")
	public void compareTransactionListSizes() throws TransactionsNotFoundAtSourceException {
		// Setup
		remoteTransactions.put(MAY_22_2021, Arrays.asList(txDataRow1, txDataRow2));
		remoteTransactions.put(MAY_28_2021, Arrays.asList(txDataRow3));
		
		localTransactions.put(MAY_22_2021, Arrays.asList(txDataRow1, txDataRow2));
		
		final Map<LocalDate, List<TxDataRow>> expectedLocalTransactions = new HashMap<>();
		expectedLocalTransactions.put(MAY_28_2021, Arrays.asList(txDataRow3));
		
		final String expectedInfoMessage = String.format(NUMBER_OF_DAYS_WITH_TX_INFO_MSG, 2, 1);
		final String expectedErrorMessage1 = String.format(TRANSACTION_MAP_DONT_MATCH_ERROR_MSG, 
				MAY_22_2021.format(CommonUtils.DATE_TIME_FORMATTER),
				MAY_28_2021.format(CommonUtils.DATE_TIME_FORMATTER),
				2,
				MAY_22_2021.format(CommonUtils.DATE_TIME_FORMATTER),
				MAY_22_2021.format(CommonUtils.DATE_TIME_FORMATTER),
				1);
		
		// Method under test
		final Map<String, Map<LocalDate, List<TxDataRow>>> result = transactionsSizeComparator.compareRemoteVsLocalTransactions(remoteTransactions, localTransactions, infoMessages, errorMessages);
		
		// Verifications		
		assertTransactions(result, Collections.emptyMap(), expectedLocalTransactions);
		
		// Verify info messages
		assertThat("There are two info messages", infoMessages.size(), is(2));
		assertThat("Info text message is as expected", infoMessages.get(0), is(expectedInfoMessage));
		assertThat("Info text message is as expected", infoMessages.get(1), is(SOME_LOCAL_TRANSACTIONS_ARE_MISSING_INFO));

		// Verify error messages
		assertThat("There are 2 error messages", errorMessages.size(), is(1));
		assertThat("Error text message as expected", errorMessages.get(0), is(expectedErrorMessage1));
	}
	
	@Test
	@DisplayName("the REMOTE map with 2 days and 4 transactions [2, 2]; LOCAL map has 2 days (the same date) with 3 transactions [2, 1].")
	public void compareTransactionListSizes_remote_2_days_4_tx_local_2_days_3_tx() throws TransactionsNotFoundAtSourceException {
		// Setup
		remoteTransactions.put(MAY_22_2021, Arrays.asList(txDataRow1, txDataRow2));
		remoteTransactions.put(MAY_28_2021, Arrays.asList(txDataRow3, txDataRow4));

		localTransactions.put(MAY_22_2021, Arrays.asList(txDataRow1, txDataRow2));
		localTransactions.put(MAY_28_2021, Arrays.asList(txDataRow3));
		
		final Map<LocalDate, List<TxDataRow>> expectedLocalTransactions = new HashMap<>();
		expectedLocalTransactions.put(MAY_28_2021, Arrays.asList(txDataRow4));
		
		final String expectedInfoMessage = String.format(NUMBER_OF_DAYS_WITH_TX_INFO_MSG, 2, 2);
		final String expectedErrorMessage = String.format(TRANSACTIONS_NOT_FOUND_FOR_DATE_ERROR_MSG, LOCAL, MAY_22_2021.format(CommonUtils.DATE_TIME_FORMATTER));
		
		// Method under test
		final Map<String, Map<LocalDate, List<TxDataRow>>> result = transactionsSizeComparator.compareRemoteVsLocalTransactions(remoteTransactions, localTransactions, infoMessages, errorMessages);
		
		// Verifications		
		assertTransactions(result, Collections.emptyMap(), expectedLocalTransactions);
		
		// Verify info messages
		assertThat("There are two info messages", infoMessages.size(), is(1));
		assertThat("Info text message as expected", infoMessages.get(0), is(expectedInfoMessage));
	
		// Verify error messages
		assertThat("There are 2 error messages", errorMessages.size(), is(0));
	}

	@Test
	@DisplayName("the REMOTE map with 2 days and 5 transactions [2, 3]; LOCAL map has 2 days (the same date) and 3 transactions [1, 2].")
	public void compareTransactionListSizes_remote_2_days_5_tx_local_2_days_3_tx() throws TransactionsNotFoundAtSourceException {
		// Setup
		remoteTransactions.put(MAY_22_2021, Arrays.asList(txDataRow1, txDataRow2));
		remoteTransactions.put(MAY_28_2021, Arrays.asList(txDataRow1, txDataRow2, txDataRow3));

		localTransactions.put(MAY_22_2021, Arrays.asList(txDataRow1));
		localTransactions.put(MAY_28_2021, Arrays.asList(txDataRow1, txDataRow2));
		
		final Map<LocalDate, List<TxDataRow>> expectedLocalTransactions = new HashMap<>();
		expectedLocalTransactions.put(MAY_22_2021, Arrays.asList(txDataRow2));
		expectedLocalTransactions.put(MAY_28_2021, Arrays.asList(txDataRow3));
		
		final String expectedInfoMessage = String.format(NUMBER_OF_DAYS_WITH_TX_INFO_MSG, 2, 2);
		final String expectedErrorMessage = String.format(TRANSACTIONS_NOT_FOUND_FOR_DATE_ERROR_MSG, LOCAL, MAY_22_2021.format(CommonUtils.DATE_TIME_FORMATTER));
		
		// Method under test
		final Map<String, Map<LocalDate, List<TxDataRow>>> result = transactionsSizeComparator.compareRemoteVsLocalTransactions(remoteTransactions, localTransactions, infoMessages, errorMessages);
		
		// Verifications
		assertTransactions(result, Collections.emptyMap(), expectedLocalTransactions);
		
		// Verify info messages
		assertThat("There are two info messages", infoMessages.size(), is(1));
		assertThat("Info text message as expected", infoMessages.get(0), is(expectedInfoMessage));
	
		// Verify error messages
		assertThat("There are 2 error messages", errorMessages.size(), is(0));
	}
	
	@Test
	@Disabled
	@DisplayName("the REMOTE map with 3 days and 6 transactions [1, 2, 3]; LOCAL map has 2 days (the same date) and 5 transactions [0, 3, 2].")
	public void remote_3_days_6_txs_local_2_days_5_txs() throws TransactionsNotFoundAtSourceException {
		// Setup
		remoteTransactions.put(MAY_22_2021, Arrays.asList(txDataRow1));
		remoteTransactions.put(MAY_28_2021, Arrays.asList(txDataRow2, txDataRow3));
		remoteTransactions.put(MAY_29_2021, Arrays.asList(txDataRow4, txDataRow5, txDataRow6));
		
		localTransactions.put(MAY_28_2021, Arrays.asList(txDataRow1, txDataRow2, txDataRow3));
		localTransactions.put(MAY_29_2021, Arrays.asList(txDataRow5, txDataRow6));
		
		final Map<LocalDate, List<TxDataRow>> expectedRemoteTransactions = new HashMap<>();
		expectedRemoteTransactions.put(MAY_28_2021, Arrays.asList(txDataRow1));
		expectedRemoteTransactions.put(MAY_29_2021, Arrays.asList(txDataRow4));
		
		final Map<LocalDate, List<TxDataRow>> expectedLocalTransactions = new HashMap<>();
		expectedLocalTransactions.put(MAY_22_2021, Arrays.asList(txDataRow1));
		expectedLocalTransactions.put(MAY_28_2021, Arrays.asList(txDataRow2, txDataRow3));
		expectedLocalTransactions.put(MAY_29_2021, Arrays.asList(txDataRow5, txDataRow6));
		
		final String expectedInfoMessage = String.format(NUMBER_OF_DAYS_WITH_TX_INFO_MSG, 2, 2);
		final String expectedErrorMessage = String.format(TRANSACTIONS_NOT_FOUND_FOR_DATE_ERROR_MSG, LOCAL, MAY_22_2021.format(CommonUtils.DATE_TIME_FORMATTER));
		
		// Method under test
		final Map<String, Map<LocalDate, List<TxDataRow>>> result = transactionsSizeComparator.compareRemoteVsLocalTransactions(remoteTransactions, localTransactions, infoMessages, errorMessages);
		
		// Verifications
		assertTransactions(result, expectedRemoteTransactions, expectedLocalTransactions);
		
		// Verify info messages
		assertThat("There are two info messages", infoMessages.size(), is(1));
		assertThat("Info text message as expected", infoMessages.get(0), is(expectedInfoMessage));
		
		// Verify error messages
		assertThat("There are 2 error messages", errorMessages.size(), is(0));
	}
	
	@Test
	@DisplayName("the REMOTE map with 3 days and 6 transactions [1, 2, 3]; LOCAL map has none. The transaction dates are ordered desc")
	public void remote_3_days_6_txs_local_none() throws TransactionsNotFoundAtSourceException {
		// Setup
		remoteTransactions.put(JUL_2_2021, Arrays.asList(txDataRow1));
		remoteTransactions.put(MAY_29_2021, Arrays.asList(txDataRow1));
		remoteTransactions.put(MAY_22_2021, Arrays.asList(txDataRow2, txDataRow3));
		remoteTransactions.put(JUN_12_2021, Arrays.asList(txDataRow1));
		remoteTransactions.put(MAY_28_2021, Arrays.asList(txDataRow4, txDataRow5, txDataRow6));
		remoteTransactions.put(JUL_13_2021, Arrays.asList(txDataRow1));
		
		
		final Map<LocalDate, List<TxDataRow>> expectedLocalTransactions = new HashMap<>();
		expectedLocalTransactions.put(MAY_22_2021, Arrays.asList(txDataRow2, txDataRow3));
		expectedLocalTransactions.put(MAY_28_2021, Arrays.asList(txDataRow4, txDataRow5, txDataRow6));
		expectedLocalTransactions.put(MAY_29_2021, Arrays.asList(txDataRow1));
		expectedLocalTransactions.put(JUN_12_2021, Arrays.asList(txDataRow1));
		expectedLocalTransactions.put(JUL_2_2021, Arrays.asList(txDataRow1));
		expectedLocalTransactions.put(JUL_13_2021, Arrays.asList(txDataRow1));

		
		final String expectedInfoMessage = String.format(NUMBER_OF_DAYS_WITH_TX_INFO_MSG, 6, 0);
		
		// Method under test
		final Map<String, Map<LocalDate, List<TxDataRow>>> result = transactionsSizeComparator.compareRemoteVsLocalTransactions(remoteTransactions, localTransactions, infoMessages, errorMessages);
		
		// Verifications
		assertThat("Verify the dates order", result.get(CommonUtils.LOCAL_TRANSACTIONS_JSON_KEY).keySet(), 
				contains(MAY_22_2021, MAY_28_2021, MAY_29_2021, JUN_12_2021, JUL_2_2021, JUL_13_2021));

		assertTransactions(result, Collections.emptyMap(), expectedLocalTransactions);
		
		// Verify info messages
		assertThat("There is one info message", infoMessages.size(), is(2));
		assertThat("Info text message as expected", infoMessages.get(0), is(expectedInfoMessage));
	}
	
	/**
	 * Verify that result transactions are as expected
	 * 
	 * @param result
	 * @param expectedRemoteTransactions
	 * @param expectedLocalTransactions
	 */
	private void assertTransactions(final Map<String, Map<LocalDate, List<TxDataRow>>> result, final Map<LocalDate, List<TxDataRow>> expectedRemoteTransactions,
			final Map<LocalDate, List<TxDataRow>> expectedLocalTransactions) {

		assertNotNull(result);

		final Map<LocalDate, List<TxDataRow>> missingRemoteTransactions = result.get(CommonUtils.REMOTE_TRANSACTIONS_JSON_KEY);
		final Map<LocalDate, List<TxDataRow>> missingLocalTransactions = result.get(CommonUtils.LOCAL_TRANSACTIONS_JSON_KEY);

		assertThat("Expected remote transactions", missingRemoteTransactions, is(expectedRemoteTransactions));
		assertThat("Expected local transactions", missingLocalTransactions, is(expectedLocalTransactions));
	}
	
	/**
	 * Set up some dummy transactions to be used in tests
	 */
	static void setupTestTransactions() {
		txDataRow1.setTxDate(MAY_22_2021);
		txDataRow1.setAccountName(TX_ACC_NAME_1);
		txDataRow1.setCategoryName(TX_CAT_NAME_1);
		txDataRow1.setDescription(TX_DESC_1);
		txDataRow1.setAmount(TX_AMOUNT_1);
		
		txDataRow2.setTxDate(MAY_22_2021);
		txDataRow2.setAccountName(TX_ACC_NAME_1);
		txDataRow2.setCategoryName(TX_CAT_NAME_1);
		txDataRow2.setDescription(TX_DESC_1);
		txDataRow2.setAmount(TX_AMOUNT_2);
		
		txDataRow3.setTxDate(MAY_28_2021);
		txDataRow3.setAccountName(TX_ACC_NAME_1);
		txDataRow3.setCategoryName(TX_CAT_NAME_1);
		txDataRow3.setDescription(TX_DESC_1);
		txDataRow3.setAmount(TX_AMOUNT_3);
		
		txDataRow3.setTxDate(MAY_28_2021);
		txDataRow3.setAccountName(TX_ACC_NAME_1);
		txDataRow3.setCategoryName(TX_CAT_NAME_1);
		txDataRow3.setDescription(TX_DESC_1);
		txDataRow3.setAmount(TX_AMOUNT_4);
	}
	
}
