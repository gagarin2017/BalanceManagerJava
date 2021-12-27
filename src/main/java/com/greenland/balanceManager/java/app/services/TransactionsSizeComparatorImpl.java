package com.greenland.balanceManager.java.app.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import com.greenland.balanceManager.java.app.CommonUtils;
import com.greenland.balanceManager.java.app.exceptions.MissingTransactionOnDateException;
import com.greenland.balanceManager.java.app.external.domain.OutputTxData;
import com.greenland.balanceManager.java.app.model.TxDataRow;

public class TransactionsSizeComparatorImpl implements TransactionsSizeComparator {
	
	private static Logger logger = LogManager.getLogger(TransactionsSizeComparatorImpl.class);

	@Override
	public Map<String, Map<LocalDate, List<TxDataRow>>> compareRemoteVsLocalTransactions(final Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			final Map<LocalDate, List<TxDataRow>> localTxSubmap, final List<String> infoMessages, final List<String> errorMessages) {
		
		Map<String, Map<LocalDate, List<TxDataRow>>> missingTransactions = new HashMap<>();
		
		logger.debug("Comparing Remote vs Local days with transactions maps sizes.");

		final TreeMap<LocalDate, List<TxDataRow>> remoteTransactionsMap = (TreeMap<LocalDate, List<TxDataRow>>) remoteTransactionMap;
		final NavigableMap<LocalDate, List<TxDataRow>> localTransactionsMap = (NavigableMap<LocalDate, List<TxDataRow>>) localTxSubmap;
		
		if(!remoteTransactionsMap.isEmpty() && !localTransactionsMap.isEmpty() 
				&& remoteTransactionsMap.size() != localTxSubmap.size()) {
			final Object[] errorMessageAttributes = new Object[] {
					remoteTransactionsMap.firstKey().format(CommonUtils.DATE_TIME_FORMATTER),
					remoteTransactionsMap.lastKey().format(CommonUtils.DATE_TIME_FORMATTER), 
					remoteTransactionsMap.size(), 
					localTransactionsMap.firstKey().format(CommonUtils.DATE_TIME_FORMATTER), 
					localTransactionsMap.lastKey().format(CommonUtils.DATE_TIME_FORMATTER), 
					localTransactionsMap.size()};
			
			final String errorMessage = String.format("Map sizes do not match. Remote dates [%s - %s], size: %d. Local dates [%s - %s], size %d",
					errorMessageAttributes);
			addErrorMessage(errorMessages, errorMessage);
		}
		
		try {
			missingTransactions = compareTransactionListSizesPerDay(remoteTransactionMap, localTxSubmap, infoMessages, errorMessages);
		} catch (MissingTransactionOnDateException e) {
			errorMessages.add(e.getMessage());
			e.printStackTrace();
		}

		return missingTransactions;
	}

	@Override
	public Map<String, Map<LocalDate, List<TxDataRow>>> compareRemoteVsLocalTransactions(final TreeMap<LocalDate, List<TxDataRow>> remoteTransactionMap, 
			final NavigableMap<LocalDate, List<TxDataRow>> localTxSubmap, final OutputTxData outputTxData) {
		
		Map<String, Map<LocalDate, List<TxDataRow>>> missingTransactions = new HashMap<>();
		
		logger.debug("Comparing Remote vs Local days with transactions maps sizes.");

		final TreeMap<LocalDate, List<TxDataRow>> remoteTransactionsMap = (TreeMap<LocalDate, List<TxDataRow>>) remoteTransactionMap;
		final NavigableMap<LocalDate, List<TxDataRow>> localTransactionsMap = (NavigableMap<LocalDate, List<TxDataRow>>) localTxSubmap;
		
		if(!remoteTransactionsMap.isEmpty() && !localTransactionsMap.isEmpty() 
				&& remoteTransactionsMap.size() != localTxSubmap.size()) {
			final Object[] errorMessageAttributes = new Object[] {
					remoteTransactionsMap.firstKey().format(CommonUtils.DATE_TIME_FORMATTER),
					remoteTransactionsMap.lastKey().format(CommonUtils.DATE_TIME_FORMATTER), 
					remoteTransactionsMap.size(), 
					localTransactionsMap.firstKey().format(CommonUtils.DATE_TIME_FORMATTER), 
					localTransactionsMap.lastKey().format(CommonUtils.DATE_TIME_FORMATTER), 
					localTransactionsMap.size()};
			
			final String errorMessage = String.format("Map sizes do not match. Remote dates [%s - %s], size: %d. Local dates [%s - %s], size %d",
					errorMessageAttributes);
			outputTxData.getErrors().add(errorMessage);
		}
		
		try {
			missingTransactions = compareTransactionListSizesPerDay(remoteTransactionMap, localTxSubmap, outputTxData);
		} catch (MissingTransactionOnDateException e) {
			outputTxData.getErrors().add(e.getMessage());
			e.printStackTrace();
		}

		return missingTransactions;
	}

	private Map<String, Map<LocalDate, List<TxDataRow>>> compareTransactionListSizesPerDay(final TreeMap<LocalDate, List<TxDataRow>> remoteTransactionMap,
			final NavigableMap<LocalDate, List<TxDataRow>> localTransactionMap, final OutputTxData outputTxData) throws MissingTransactionOnDateException  {

		final String numberOfDaysWithTransactionsInfoMsg = String.format("Comparing transactions for each date Remote [%d days with transactions] vs Local [%d days with transactions].",
				remoteTransactionMap.size(), localTransactionMap.size());
		
		outputTxData.getInfoMessages().add(numberOfDaysWithTransactionsInfoMsg);
		
		final Map<String, Map<LocalDate, List<TxDataRow>>> missingTransactions = new HashMap<>();
		Map<LocalDate, List<TxDataRow>> missingRemoteTransactions = new HashMap<>();
		Map<LocalDate, List<TxDataRow>> missingLocalTransactions = new HashMap<>();

		String transactionsMissingInfo = "Some %s are missing";

		// Check if dates (can be many) Local and Remote match
		if (remoteDatesMatchLocalDates(remoteTransactionMap, localTransactionMap)) {
			// check transactions for each day
			final MapDifference<LocalDate, List<TxDataRow>> mapDifference = Maps.difference(remoteTransactionMap, localTransactionMap);
			
			final Map<LocalDate, ValueDifference<List<TxDataRow>>> entriesDiffering = mapDifference.entriesDiffering();
			
			for (final Entry<LocalDate, ValueDifference<List<TxDataRow>>> entry : entriesDiffering.entrySet()) {

				final LocalDate date = entry.getKey();
				
				final List<TxDataRow> transactionsSet01ForTheDate = entry.getValue().leftValue();
				final List<TxDataRow> transactionsSet02ForTheDate = entry.getValue().rightValue();

				if (transactionsSet01ForTheDate.size() > transactionsSet02ForTheDate.size()) {
					
					missingLocalTransactions.put(date, findMissingTransactions(remoteTransactionMap.get(date), localTransactionMap.get(date)));
				
				} else if (transactionsSet01ForTheDate.size() < transactionsSet02ForTheDate.size()) {
					
					missingRemoteTransactions.put(date, findMissingTransactions(remoteTransactionMap.get(date), localTransactionMap.get(date)));					
				
				} else {
					// validate if transactions are equal for the date
					
				}
			}
			
			
		} else if (remoteTransactionMap.keySet().size() > localTransactionMap.keySet().size()) {
			transactionsMissingInfo = String.format(transactionsMissingInfo, CommonUtils.LOCAL_TRANSACTIONS_JSON_KEY);
			
			outputTxData.getInfoMessages().add(numberOfDaysWithTransactionsInfoMsg);
			
			missingLocalTransactions = remoteTransactionMap.entrySet().stream()
			        .filter(x -> !localTransactionMap.containsKey(x.getKey()))
			        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		} else {
			transactionsMissingInfo = String.format(transactionsMissingInfo, CommonUtils.REMOTE_TRANSACTIONS_JSON_KEY);
			
			outputTxData.getInfoMessages().add(transactionsMissingInfo);
			
			missingRemoteTransactions = localTransactionMap.entrySet().stream()
			        .filter(x -> !remoteTransactionMap.containsKey(x.getKey()))
			        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
		
		if (!missingRemoteTransactions.isEmpty()) {
			missingTransactions.put(CommonUtils.REMOTE_TRANSACTIONS_JSON_KEY, getSortedByDate(missingRemoteTransactions));
		}
		
		if (!missingLocalTransactions.isEmpty()) {
			missingTransactions.put(CommonUtils.LOCAL_TRANSACTIONS_JSON_KEY, getSortedByDate(missingLocalTransactions));
		}
		
		return missingTransactions;
		
	}

	/**
	 * Compare days with transactions. Make sure that each date exist in both maps.
	 * 
	 * @param remoteTransactionMap
	 * @param localTransactionMap
	 * @param errorMessages 
	 * @param infoMessages 
	 * @return 
	 * @throws MissingTransactionOnDateException 
	 */
	@Deprecated
	private Map<String, Map<LocalDate, List<TxDataRow>>> compareTransactionListSizesPerDay(final Map<LocalDate, List<TxDataRow>> remoteTransactionMap, 
			final Map<LocalDate, List<TxDataRow>> localTransactionMap, final List<String> infoMessages, final List<String> errorMessages) throws MissingTransactionOnDateException {
		
		
		final String numberOfDaysWithTransactions = String.format("Comparing transactions for each date Remote [%d days with transactions] vs Local [%d days with transactions].",
						remoteTransactionMap.size(), localTransactionMap.size());
		
		addInforMessage(infoMessages, numberOfDaysWithTransactions);
		
		return findMissingTransactions(remoteTransactionMap, localTransactionMap, true, infoMessages, errorMessages);
		
//		int[] overallTransactions = new int[2];
//		
//		// Remote has more or equal number of days with transactions. 
//		if (remoteTransactionMap.size() >= localTransactionMap.size()) {
//			overallTransactions = inspectDaysWithTransactions(remoteTransactionMap, localTransactionMap, true);
//			logger.info("Overall transactions for all days remote: {}, local: {} ", overallTransactions[0], overallTransactions[1]);
//		} else {
//			overallTransactions = inspectDaysWithTransactions(localTransactionMap, remoteTransactionMap, false);
//			logger.info("Overall transactions for all days local: {}, remote: {} ", overallTransactions[0], overallTransactions[1]);
//		}
		
	}
	
	/**
	 * Compare two maps and find the difference on dates with transactions i.e. date with transactions missing in one map or another
	 * 
	 * @param transactionMap1
	 * 				Map<LocalDate, List<TxDataRow>> - transactions map (LocalDate, transactions list for that date). Can be either Remote txs or Local
	 * @param transactionMap2
	 * 				Map<LocalDate, List<TxDataRow>> - transactions map (LocalDate, transactions list for that date). Can be either Remote txs or Local
	 * @param isFirstParamRemoteTransactions - flag which defines the order. If true, then first passed param is Remote tx map, else Local
	 * 
	 * @throws MissingTransactionOnDateException 
	 */
	@Deprecated
	private int[] inspectDaysWithTransactions(final Map<LocalDate, List<TxDataRow>> transactionMap1,
			final Map<LocalDate, List<TxDataRow>> transactionMap2, final boolean isFirstParamRemoteTransactions) throws MissingTransactionOnDateException {
		
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
				
				final String errorText = String.format("%s Transactions not found for the date: %s", 
						isFirstParamRemoteTransactions ? "LOCAL" : "REMOTE",
						mapEntry1.getKey().format(CommonUtils.DATE_TIME_FORMATTER));
				throw new MissingTransactionOnDateException(errorText);
			}
			
		}
		
		return overallTransactionsForPeriod;
	}
	
	
	
	/**
	 * Compare two maps and find the difference on dates with transactions i.e. date with transactions missing in one map or another
	 * 
	 * @param transactionMap1
	 * 				Map<LocalDate, List<TxDataRow>> - transactions map (LocalDate, transactions list for that date). Can be either Remote txs or Local
	 * @param transactionMap2
	 * 				Map<LocalDate, List<TxDataRow>> - transactions map (LocalDate, transactions list for that date). Can be either Remote txs or Local
	 * @param isFirstParamRemoteTransactions - flag which defines the order. If true, then first passed param is Remote tx map, else Local
	 * 
	 * @throws MissingTransactionOnDateException 
	 */
	@Deprecated
	private Map<String, Map<LocalDate, List<TxDataRow>>> findMissingTransactions(final Map<LocalDate, List<TxDataRow>> transactionMap1,
			final Map<LocalDate, List<TxDataRow>> transactionMap2, final boolean isFirstParamRemoteTransactions, final List<String> infoMessages, final List<String> errorMessages) 
					throws MissingTransactionOnDateException {
		
		final Map<String, Map<LocalDate, List<TxDataRow>>> missingTransactions = new HashMap<>();
		Map<LocalDate, List<TxDataRow>> missingRemoteTransactions = new HashMap<>();
		Map<LocalDate, List<TxDataRow>> missingLocalTransactions = new HashMap<>();

		String transactionsMissingInfo = "Some %s are missing";

		// Check if dates (can be many) Local and Remote match
		if (remoteDatesMatchLocalDates(transactionMap1, transactionMap2)) {
			// check transactions for each day
			final MapDifference<LocalDate, List<TxDataRow>> mapDifference = Maps.difference(transactionMap1, transactionMap2);
			
			final Map<LocalDate, ValueDifference<List<TxDataRow>>> entriesDiffering = mapDifference.entriesDiffering();
			
			for (final Entry<LocalDate, ValueDifference<List<TxDataRow>>> entry : entriesDiffering.entrySet()) {

				final LocalDate date = entry.getKey();
				
				final List<TxDataRow> transactionsSet01ForTheDate = entry.getValue().leftValue();
				final List<TxDataRow> transactionsSet02ForTheDate = entry.getValue().rightValue();

				if (transactionsSet01ForTheDate.size() > transactionsSet02ForTheDate.size()) {
					
					missingLocalTransactions.put(date, findMissingTransactions(transactionMap1.get(date), transactionMap2.get(date)));
				
				} else if (transactionsSet01ForTheDate.size() < transactionsSet02ForTheDate.size()) {
					
					missingRemoteTransactions.put(date, findMissingTransactions(transactionMap1.get(date), transactionMap2.get(date)));					
				
				} else {
					// validate if transactions are equal for the date
					
				}
			}
			
			
		} else if (transactionMap1.keySet().size() > transactionMap2.keySet().size()) {
			transactionsMissingInfo = String.format(transactionsMissingInfo, isFirstParamRemoteTransactions ? CommonUtils.LOCAL_TRANSACTIONS_JSON_KEY : CommonUtils.REMOTE_TRANSACTIONS_JSON_KEY);
			addInforMessage(infoMessages, transactionsMissingInfo);
			missingLocalTransactions = transactionMap1.entrySet().stream()
			        .filter(x -> !transactionMap2.containsKey(x.getKey()))
			        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		} else {
			transactionsMissingInfo = String.format(transactionsMissingInfo, isFirstParamRemoteTransactions ? CommonUtils.REMOTE_TRANSACTIONS_JSON_KEY : CommonUtils.LOCAL_TRANSACTIONS_JSON_KEY);
			addInforMessage(infoMessages, transactionsMissingInfo);
			missingRemoteTransactions = transactionMap2.entrySet().stream()
			        .filter(x -> !transactionMap1.containsKey(x.getKey()))
			        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
		
		missingTransactions.put(CommonUtils.REMOTE_TRANSACTIONS_JSON_KEY, getSortedByDate(missingRemoteTransactions));
		missingTransactions.put(CommonUtils.LOCAL_TRANSACTIONS_JSON_KEY, getSortedByDate(missingLocalTransactions));
		
		return missingTransactions;
	}

	private boolean remoteDatesMatchLocalDates(final Map<LocalDate, List<TxDataRow>> transactionMap1,
			final Map<LocalDate, List<TxDataRow>> transactionMap2) {
		return transactionMap1.keySet().equals(transactionMap2.keySet());
	}

	/**
	 * @param remoteTxForDate
	 * @param localTxForDate
	 * @return
	 */
	private List<TxDataRow> findMissingTransactions(final List<TxDataRow> remoteTxForDate, final List<TxDataRow> localTxForDate) {
		
		// passed lists are Unmodifiable hence creating the local instances
		final List<TxDataRow> remote = new ArrayList<>(remoteTxForDate);
		final List<TxDataRow> local = new ArrayList<>(localTxForDate);
		
		if (remote.size() > local.size()) {
			remote.removeAll(local);
			return remote;
		} else {
			local.removeAll(remote);
			return local;			
		}
	}

	/**
	 * @param transactionsMap
	 * @return LinkedHashMap of transactions sorted by date
	 */
	private LinkedHashMap<LocalDate, List<TxDataRow>> getSortedByDate(
			final Map<LocalDate, List<TxDataRow>> transactionsMap) {
		return transactionsMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors
				.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}


	/**
	 * Updated passed collection with given info message
	 * 
	 * @param infoMessages
	 * 			info message collection
	 * @param infoMessage
	 * 			message to be added
	 */
	private void addInforMessage(final List<String> infoMessages, final String infoMessage) {
		logger.info(infoMessage);
		infoMessages.add(infoMessage);
	}

	/**
	 * Updated passed collection with given error message
	 * 
	 * @param errorMessages
	 * 			info message collection
	 * @param errorMessage
	 * 			message to be added
	 */
	private void addErrorMessage(final List<String> errorMessages, final String errorMessage) {
		errorMessages.add(errorMessage);
		logger.error(errorMessage);
	}
	
	private class DateComparator implements Comparator<LocalDate> {
		
		@Override
		public int compare(LocalDate date1, LocalDate date2) {
			if(date1.isBefore(date2)) {
				return -1;
			} else if (date1.isAfter(date2)) {
				return 1;
			} else {
				return 0;
			}
		}
		
	}

}
