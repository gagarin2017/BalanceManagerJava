package com.greenland.balanceManager.java.app.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.inject.Inject;
import com.greenland.balanceManager.java.app.CommonUtils;
import com.greenland.balanceManager.java.app.TransactionsUtility;
import com.greenland.balanceManager.java.app.external.domain.DailyTransactions;
import com.greenland.balanceManager.java.app.external.domain.OutputTxData;
import com.greenland.balanceManager.java.app.model.TxDataRow;

/**
 * @author Jura
 *
 */
public class TransactionsBalanceAnalyzerImpl implements TransactionsBalanceAnalyzer {
	
	private static Logger logger = LogManager.getLogger(TransactionsBalanceAnalyzerImpl.class);
	
	private AsciiTableDrawingService asciiTableDrawingService;

	public TransactionsBalanceAnalyzerImpl() { }
	
	@Inject
	public TransactionsBalanceAnalyzerImpl(AsciiTableDrawingService asciiTableDrawingService) {
		this.asciiTableDrawingService = asciiTableDrawingService;
	}

	@Deprecated
	@Override
	public JSONObject analyzeTransactionBalances(final Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			final Map<LocalDate, List<TxDataRow>> localTransactionMap, final BigDecimal startingBalance,
			final Map<String, Map<LocalDate, List<TxDataRow>>> missingTransactions, final List<String> infoMessages,
			final List<String> errorMessages) {

		logger.info("Starting to calculate balance for remote.");
		final Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> remoteTransactionBalances = buildTransactionsBalancesPerDateMap(
				remoteTransactionMap);

		logger.info("Starting to calculate balance for local.");
		final Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> localTransactionBalances = buildTransactionsBalancesPerDateMap(
				localTransactionMap);

		logger.info("Check if transactions are missing Remote/Local");
		final Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> missingTransactionBalances = buildTransactionsBalancesPerDateMap(
				localTransactionMap);

//		final boolean doRemoteVsLocalBalancesMatch = compareOverallTransactionBalances(remoteTransactionBalances, localTransactionBalances);
		
//		asciiTableDrawingService.drawAsciiTable(remoteTransactionBalances, localTransactionBalances, startingBalance);

		final JSONObject resultJSONOBject = new JSONObject();

		resultJSONOBject.put(CommonUtils.STARTING_BALANCE_JSON_KEY, startingBalance.setScale(2, RoundingMode.HALF_UP))
				.put(CommonUtils.REMOTE_TRANSACTIONS_JSON_KEY, createJsonTransactionsArray(remoteTransactionBalances))
				.put(CommonUtils.LOCAL_TRANSACTIONS_JSON_KEY, createJsonTransactionsArray(localTransactionBalances))
				.put(CommonUtils.MISSING_TRANSACTIONS_BALANCES_JSON_KEY, createJsonTransactionsArray(missingTransactionBalances))
				.put(CommonUtils.MISSING_TRANSACTIONS_JSON_KEY, buildMissingTransactionsJsonArray(missingTransactions));

		return resultJSONOBject;
	}
	
	@Override
	public void analyzeTransactionBalances(final TreeMap<LocalDate, List<TxDataRow>> remoteTransactionMap,
			final NavigableMap<LocalDate, List<TxDataRow>> localTransactionMap, final Map<String, Map<LocalDate, List<TxDataRow>>> missingTxsMap, 
			final OutputTxData outputTxData) {

		logger.info("Starting to calculate balance for remote.");
		final Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> remoteTransactionBalances = 
				buildTransactionsBalancesPerDateMap(remoteTransactionMap);

		logger.info("Starting to calculate balance for local.");
		final Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> localTransactionBalances = 
				buildTransactionsBalancesPerDateMap(localTransactionMap);
		
		final List<DailyTransactions> remoteTransactions = new ArrayList<>();
		final List<DailyTransactions> localTransactions = new ArrayList<>();

		final Map<String, List<DailyTransactions>> missingTransactions = new HashMap<>();

		remoteTransactionBalances.forEach((date, transactions) -> {
			final DailyTransactions dailyTransactions = DailyTransactions.builder().date(date)
					.transactions(transactions.getLeft()).total(transactions.getRight()).build();
			remoteTransactions.add(dailyTransactions);
		});
		
		localTransactionBalances.forEach((date, transactions) -> {
			final DailyTransactions dailyTransactions = DailyTransactions.builder().date(date)
					.transactions(transactions.getLeft()).total(transactions.getRight()).build();
			localTransactions.add(dailyTransactions);
		});
		
		for (final Entry<String, Map<LocalDate, List<TxDataRow>>> entry : missingTxsMap.entrySet()) {
			
			final List<DailyTransactions> transactions = new ArrayList<>();
			
			entry.getValue().forEach((date, txs) -> {
				final DailyTransactions dailyTransactions = 
						DailyTransactions.builder().date(date).transactions(txs).build();
				transactions.add(dailyTransactions);
			});
			
			missingTransactions.put(entry.getKey(), transactions);
		}

		outputTxData.setRemoteTransactions(remoteTransactions );
		outputTxData.setLocalTransactions(localTransactions);
		outputTxData.setMissingTransactions(missingTransactions);
	}

	/**
	 * Create a pretty JSON for the missing transactions
	 * 
	 * @param missingTransactions
	 * @return missingTransactions (date : txsOnThisDate) for REMOTE and LOCAL
	 */
	private JSONArray buildMissingTransactionsJsonArray(final Map<String, Map<LocalDate, List<TxDataRow>>> missingTransactions) {
		final JSONArray missingTransactionsGroups = new JSONArray();
		
		missingTransactions.forEach((groupName, datesWithTransactions) -> {
			final JSONObject group = new JSONObject();
			group.append(CommonUtils.MISSING_TXS_GROUP_NAME_JSON_KEY, groupName);
			group.append(CommonUtils.MISSING_GROUP_TRANSACTIONS_JSON_KEY, buildDaysWithTransactionsArray(missingTransactions.get(groupName)));
			missingTransactionsGroups.put(group);
		});
		
		return missingTransactionsGroups;
	}

	/**
	 * Create JSON array (date : txsOnThisDate)
	 * 
	 * @param map with transaction days
	 * @return JSON array (date : txsOnThisDate)
	 */
	private JSONArray buildDaysWithTransactionsArray(final Map<LocalDate, List<TxDataRow>> map) {
		final JSONArray transactionDays = new JSONArray();
		
		map.forEach((date, transactions) -> {
			final JSONObject missedDate = new JSONObject();
			missedDate.append(CommonUtils.MISSED_TRANSACTIONS_DATE_JSON_KEY, date);
			missedDate.append(CommonUtils.MISSED_TRANSACTIONS_ON_THE_DATE_JSON_KEY, transactions);
			transactionDays.put(missedDate);
		});
		
		return transactionDays;
	}

	/**
	 * Method creates the collection with the transactions per day + total balance for that day
	 * 
	 * @param transactionMap
	 * @return {@link JSONArray}
	 */
	private JSONArray createJsonTransactionsArray(final Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> transactionMap) {
    	
		final JSONArray totalTransactions = new JSONArray();
    	
    	transactionMap.forEach((date, transactionForDate) -> {
//    		final JSONObject transactionsPerDay = new JSONObject();
//    		transactionsPerDay.append(CommonUtils.TOTAL_TX_PER_DAY_JSON_KEY, transactionForDate.getLeft());
    		
    		final String txDate = date.toString();
    		
    		final JSONObject totalTransactionsForTheDay = new JSONObject();
    		totalTransactionsForTheDay.put(CommonUtils.TX_DATE_JSON_KEY, txDate);
    		totalTransactionsForTheDay.put(CommonUtils.TOTAL_AMOUNT_PER_DAY_JSON_KEY, transactionForDate.getRight());
    		totalTransactionsForTheDay.put(CommonUtils.TX_LIST_PER_DAY_JSON_KEY, transactionForDate.getLeft());
    		
    		totalTransactions.put(totalTransactionsForTheDay);
    	});
    	

		return totalTransactions;
	}
	
	/**
	 * Method builds a map with Date is the key and map value is 
	 * a {@link Pair} of List of transactions for the date as a key and summary balance for the date as a {@link Pair} value
	 * 
	 * @param transactionsMap
	 * @param startAmount
	 * @return
	 */
	private Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> buildTransactionsBalancesPerDateMap (
			final Map<LocalDate, List<TxDataRow>> transactionsMap) {
		
		final Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> transactionBalancesPerDate = new TreeMap<>();
		
		for (final Entry<LocalDate, List<TxDataRow>> transactionsMapEntry : transactionsMap.entrySet()) {
			
			final LocalDate currentDate = transactionsMapEntry.getKey();
			final List<TxDataRow> transactionsForDate = transactionsMapEntry.getValue();
			
			// sorting transactions by amounts
	        Collections.sort(transactionsForDate, new TransactionsUtility.TransactionsByAmountComparator()); 
			final Pair<List<TxDataRow>, BigDecimal> txsBalancesForDatePair = Pair.of(transactionsForDate, calculateBalanceForTheDate(transactionsForDate));
			transactionBalancesPerDate.put(currentDate, txsBalancesForDatePair);
		}
		
		return transactionBalancesPerDate;
	}
	
	/**
	 * Calculate balance for the date by adding Credit and Debit amounts to the startAmount.
	 * 
	 * @param transactionsForDate
	 * @param startAmount
	 * @return
	 */
	private BigDecimal calculateBalanceForTheDate(final List<TxDataRow> transactionsForDate) {
		BigDecimal finalBalanceForDate = new BigDecimal("0");
		
		for (final TxDataRow txDataRow : transactionsForDate) {
			
			final BigDecimal creditAmt = txDataRow.getCreditAmount();
			final BigDecimal debitAmt = txDataRow.getDebitAmount();
			
			final BigDecimal signedAmt = creditAmt.compareTo(BigDecimal.ZERO) > 0 ? creditAmt : debitAmt.negate();
			
			finalBalanceForDate = finalBalanceForDate.add(signedAmt);
		}
		
		return finalBalanceForDate;
	}
	
}
