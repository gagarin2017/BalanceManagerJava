package com.greenland.balanceManager.java.app.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.greenland.balanceManager.java.app.CommonUtils;
import com.greenland.balanceManager.java.app.exceptions.MissingTransactionOnDateException;
import com.greenland.balanceManager.java.app.model.TxDataRow;

public class TransactionsSizeComparatorImpl implements TransactionsSizeComparator {
	
	private static Logger logger = LogManager.getLogger(TransactionsSizeComparatorImpl.class);

	@Override
	public void compareTransactionListSizes(final Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			final Map<LocalDate, List<TxDataRow>> localTxSubmap) {
		
		logger.debug("Comparing Remote vs Local days with transactions maps sizes.");

		final TreeMap<LocalDate, List<TxDataRow>> remoteTransactionsMap = (TreeMap<LocalDate, List<TxDataRow>>) remoteTransactionMap;
		final NavigableMap<LocalDate, List<TxDataRow>> localTransactionsMap = (NavigableMap<LocalDate, List<TxDataRow>>) localTxSubmap;
		
		if(remoteTransactionMap.size() != localTxSubmap.size()) {
			final Object[] errorMessageAttributes = new Object[] {
					remoteTransactionsMap.firstKey().format(CommonUtils.DATE_TIME_FORMATTER),
					remoteTransactionsMap.lastKey().format(CommonUtils.DATE_TIME_FORMATTER), 
					remoteTransactionsMap.size(), 
					localTransactionsMap.firstKey().format(CommonUtils.DATE_TIME_FORMATTER), 
					localTransactionsMap.lastKey().format(CommonUtils.DATE_TIME_FORMATTER), 
					localTransactionsMap.size()};
			
			final String errorMessage = String.format("\n\nMap sizes do not match. Remote dates [%s - %s], size: %d. Local dates [%s - %s], size %d\n\n",
					errorMessageAttributes);
			logger.error(errorMessage);
		}
		
		compareTransactionListSizesPerDay(remoteTransactionMap, localTxSubmap);

	}
	

	/**
	 * Compare days with transactions. Make sure that each date exist in both maps.
	 * 
	 * @param remoteTransactionMap
	 * @param localTransactionMap
	 */
	void compareTransactionListSizesPerDay(final Map<LocalDate, List<TxDataRow>> remoteTransactionMap, 
			final Map<LocalDate, List<TxDataRow>> localTransactionMap) {
		
		logger.info("Comparing transactions for each date Remote [{} days with transactions] vs Local [{} days with transactions].", remoteTransactionMap.size(), localTransactionMap.size());
		int[] overallTransactions = new int[2];
		
		// Remote has more or equal number of days with transactions. 
		if (remoteTransactionMap.size() >= localTransactionMap.size()) {
			overallTransactions = inspectDaysWithTransactions(remoteTransactionMap, localTransactionMap);
			logger.info("Overall transactions for all days remote: {}, local: {} ", overallTransactions[0], overallTransactions[1]);
		} else {
			overallTransactions = inspectDaysWithTransactions(localTransactionMap, remoteTransactionMap);
			logger.info("Overall transactions for all days local: {}, remote: {} ", overallTransactions[0], overallTransactions[1]);
		}
		
	}
	
	/**
	 * Compare two maps and find the difference on dates with transactions i.e. date with transactions missing in one map or another
	 * 
	 * @param transactionMap1
	 * @param transactionMap2
	 */
	private int[] inspectDaysWithTransactions(final Map<LocalDate, List<TxDataRow>> transactionMap1,
			final Map<LocalDate, List<TxDataRow>> transactionMap2) {
		
		int[] overallTransactionsForPeriod = new int[2];
		
		for (final Entry<LocalDate, List<TxDataRow>> mapEntry1 : transactionMap1.entrySet()) {
			
			if (transactionMap2.containsKey(mapEntry1.getKey())) {
				
				final List<TxDataRow> map2TransactionsSpecificDate = transactionMap2.get(mapEntry1.getKey());
				
				overallTransactionsForPeriod[0] = overallTransactionsForPeriod[0] + mapEntry1.getValue().size();
				overallTransactionsForPeriod[1] = overallTransactionsForPeriod[1] + map2TransactionsSpecificDate.size();
				
				if (mapEntry1.getValue().size() == map2TransactionsSpecificDate.size()) {
					continue;
				} else {
					final String errorText = String.format("Number of transactions do not match for the date: %s. There are %d remote transactions vs %d local transactions", 
							mapEntry1.getKey().format(CommonUtils.DATE_TIME_FORMATTER), mapEntry1.getValue().size(), map2TransactionsSpecificDate.size());
					
					throw new MissingTransactionOnDateException(errorText);
				}
				
			} else {
				
				final String errorText = String.format("Transactions not found for the date: %s", mapEntry1.getKey().format(CommonUtils.DATE_TIME_FORMATTER));
				throw new MissingTransactionOnDateException(errorText);
			}
			
		}
		
		return overallTransactionsForPeriod;
	}

}
