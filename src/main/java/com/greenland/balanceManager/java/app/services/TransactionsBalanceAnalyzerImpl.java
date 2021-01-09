package com.greenland.balanceManager.java.app.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.google.inject.Inject;
import com.greenland.balanceManager.java.app.TransactionsUtility;
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

	@Override
	public JSONObject analyzeTransactionBalances(final Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			final Map<LocalDate, List<TxDataRow>> localTransactionMap, final BigDecimal startingBalance) {
		
		logger.info("Starting to calculate balance for remote.");
		final Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> remoteTransactionBalances = buildTransactionsBalancesPerDateMap(remoteTransactionMap);
		
		logger.info("Starting to calculate balance for local.");
		final Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> localTransactionBalances = buildTransactionsBalancesPerDateMap(localTransactionMap);
		
//		final boolean doRemoteVsLocalBalancesMatch = compareOverallTransactionBalances(remoteTransactionBalances, localTransactionBalances);


		asciiTableDrawingService.drawAsciiTable(remoteTransactionBalances, localTransactionBalances, startingBalance);
		final JSONObject resultJSONOBject = new JSONObject();
		
		
		resultJSONOBject.put("JSON1", "Hello World!")
        .put("JSON2", "Hello my World!")
        .put("JSON3", new JSONObject()
             .put("key1", "value1"));
		
		return resultJSONOBject;
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
	 * @param remoteTransactionBalances
	 * @param localTransactionBalances
	 * @return
	 */
//	private boolean compareOverallTransactionBalances(
//			final Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> remoteTransactionBalances,
//			final Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> localTransactionBalances) {
//		
//		final boolean doRemoteVsLocalBalancesMatch;
//		
//		BigDecimal remoteTotal = new BigDecimal("0");
//		BigDecimal localTotal = new BigDecimal("0");
//		
//		for (final Entry<LocalDate, Pair<List<TxDataRow>, BigDecimal>> remoteMapEntry : remoteTransactionBalances.entrySet()) {
//			remoteTotal = remoteTotal.add(remoteMapEntry.getValue().getRight());
//		}
//		
//		for (final Entry<LocalDate, Pair<List<TxDataRow>, BigDecimal>> localMapEntry : localTransactionBalances.entrySet()) {
//			localTotal = localTotal.add(localMapEntry.getValue().getRight());
//		}
//		
//		if (remoteLocalPositiveAndMatch(remoteTotal, localTotal)) {
//			logger.info("Overall balances match! We are all good! Remote: {} euros vs Local: {} euros.", remoteTotal, localTotal);
//			doRemoteVsLocalBalancesMatch = true;
//		} else {
//			logger.info("Overall balances 0 or  DID NOT match! Remote: {} euros vs Local: {} euros.", remoteTotal, localTotal);
////			handleBalancesDidNotMatch();
//			doRemoteVsLocalBalancesMatch = false;
//		}
//		
//		return doRemoteVsLocalBalancesMatch;
//	}
	
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
	
	/**
	 * @param remoteTotal
	 * @param localTotal
	 * @return
	 */
//	private boolean remoteLocalPositiveAndMatch(BigDecimal remoteTotal, BigDecimal localTotal) {
//		return remoteTotal.compareTo(BigDecimal.ZERO) > 0 && localTotal.compareTo(BigDecimal.ZERO) > 0 && remoteTotal.compareTo(localTotal) == 0;
//	}
	
	
}
