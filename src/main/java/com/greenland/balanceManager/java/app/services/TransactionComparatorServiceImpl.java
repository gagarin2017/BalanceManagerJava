package com.greenland.balanceManager.java.app.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.greenland.balanceManager.java.app.CommonUtils;
import com.greenland.balanceManager.java.app.dao.TransactionsSourceDao;
import com.greenland.balanceManager.java.app.exceptions.MissingTransactionOnDateException;
import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundException;
import com.greenland.balanceManager.java.app.model.TxDataRow;


/**
 * Service which loops through the transactions from the different resources, usually remote vs local
 * and performs the comparison / check if the number of transactions match and if balances for the given period match.
 * 
 * @author Jura
 *
 */
public class TransactionComparatorServiceImpl implements TransactionComparatorService {
	
	private static Logger logger = LogManager.getLogger(TransactionComparatorServiceImpl.class);
	
	static final String TX_NOT_FOUND_ERROR = "Transactions were not found. Number of Remote transactions %d. Number of Local transactions %d";
	
	private Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
	private Map<LocalDate,List<TxDataRow>> localTransactionMap = new HashMap<>();
	
	@Inject
	private TransactionsReaderService transactionsReaderService;
	
	@Inject @Named("TransactionsFileReader")
	private TransactionsSourceDao transactionsSourceFileDao;
	
	@Inject @Named("TransactionsJsonReader")
	private TransactionsSourceDao transactionsSourceJsonDao;
	
	@Inject
	private TransactionsBalanceAnalyzer transactionsBalanceAnalyzer;
	
	@Inject
	private TransactionsSizeComparator transactionsSizeComparator;

//	@Inject
//	public TransactionComparatorServiceImpl(final TransactionsReaderService transactionsReaderService) {
//		
//	}

	/**
	 * Populating transaction maps using the implementation of {@link TransactionsSourceDao} which is binded in com.google.inject.AbstractModule
	 * 
	 * @throws TransactionsNotFoundAtSourceException
	 */
	private void populateTxDataRows() throws TransactionsNotFoundAtSourceException {
		logger.info("Using an instance of [{}] to get the transactions", transactionsSourceFileDao.getClass().getName());
		
		transactionsReaderService.populateTxMapsFromSource(getRemoteTransactionMap(), getLocalTransactionMap(), transactionsSourceFileDao);
		
		logger.info("Found {} remote days with transactions and {} local days with transactions", getRemoteTransactionMap().size(), getLocalTransactionMap().size());
	}

	@Override
	public void executeTransactionComparison() throws TransactionsNotFoundAtSourceException {
		populateTxDataRows();
		if(getRemoteTransactionMap().isEmpty() || getLocalTransactionMap().isEmpty()) {
			final String errorMessage = String.format(TX_NOT_FOUND_ERROR, remoteTransactionMap.size(), localTransactionMap.size());
			throw new TransactionsNotFoundException(errorMessage);
		}
		
		compareRemoteVsLocalTransactions(getRemoteTransactionMap(), getLocalTransactionMap(), CommonUtils.START_AMOUNT);
	}
	
	@Override
	public JSONObject executeTransactionComparison(final String remoteFileName, final String localFileName, final BigDecimal startingBalance) throws TransactionsNotFoundAtSourceException {
		
		logger.info("Using an instance of [{}] to get the transactions", transactionsSourceFileDao.getClass().getName());
		
		transactionsReaderService.populateTxMapsFromSource(getRemoteTransactionMap(), getLocalTransactionMap(), transactionsSourceFileDao);
		
		logger.info("Found {} remote days with transactions and {} local days with transactions", getRemoteTransactionMap().size(), getLocalTransactionMap().size());
		if(getRemoteTransactionMap().isEmpty() || getLocalTransactionMap().isEmpty()) {
			final String errorMessage = String.format(TX_NOT_FOUND_ERROR, remoteTransactionMap.size(), localTransactionMap.size());
			throw new TransactionsNotFoundException(errorMessage);
		}
		
		return compareRemoteVsLocalTransactions(getRemoteTransactionMap(), getLocalTransactionMap(), startingBalance);
	}

	@Override
	public JSONObject executeTransactionComparison(final JSONObject remoteTransactions,	final JSONObject localTransactions,
			final BigDecimal startingBalance) throws TransactionsNotFoundAtSourceException {

		logger.info("Using an instance of [{}] to get the transactions", transactionsSourceJsonDao.getClass().getName());

		transactionsReaderService.populateTxMapsFromSource(getRemoteTransactionMap(), getLocalTransactionMap(), transactionsSourceJsonDao, remoteTransactions, localTransactions);

		logger.info("Found {} remote days with transactions and {} local days with transactions", getRemoteTransactionMap().size(), getLocalTransactionMap().size());
		if (getRemoteTransactionMap().isEmpty() || getLocalTransactionMap().isEmpty()) {
			final String errorMessage = String.format(TX_NOT_FOUND_ERROR, remoteTransactionMap.size(),
					localTransactionMap.size());
			throw new TransactionsNotFoundException(errorMessage);
		}

		return compareRemoteVsLocalTransactions(getRemoteTransactionMap(), getLocalTransactionMap(), startingBalance);
	}

	public Map<LocalDate, List<TxDataRow>> getRemoteTransactionMap() {
		return remoteTransactionMap;
	}

	public Map<LocalDate, List<TxDataRow>> getLocalTransactionMap() {
		return localTransactionMap;
	}

	/**
	 * @param remoteTransactionMap
	 * @param localTransactionMap
	 */
	public JSONObject compareRemoteVsLocalTransactions(final Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			final Map<LocalDate, List<TxDataRow>> localTransactionMap, final BigDecimal startingBalance) {
		
		logger.info("Comparing transactions maps. Remote: {} overall transaction. Local: {} overall transactions.", remoteTransactionMap.size(), localTransactionMap.size());
		
		// Sort maps
		final TreeMap<LocalDate, List<TxDataRow>> remoteTransactionMapSorted = sortMapByTxDate(remoteTransactionMap);
		final TreeMap<LocalDate, List<TxDataRow>> localTransactionMapSorted = sortMapByTxDate(localTransactionMap);
		
		// Getting the date range for which we are going to analyse the transactions based on remote tx map date range
		final LocalDate remoteTrasnactionsStartDate = (LocalDate) remoteTransactionMapSorted.firstKey(); 
		final LocalDate remoteTrasnactionsEndDate = (LocalDate) remoteTransactionMapSorted.lastKey(); 
		
		logger.info("The considered transaction data range for Remote: {} - {}. Size: {} days with transactions.", 
				remoteTrasnactionsStartDate.format(CommonUtils.DATE_TIME_FORMATTER), 
				remoteTrasnactionsEndDate.format(CommonUtils.DATE_TIME_FORMATTER),
				remoteTransactionMap.size());
		
		// Getting local transactions for the same date range as the remote transactions.
		// Local transaction map seem to have ALL transactions of All times!
		final NavigableMap<LocalDate, List<TxDataRow>> localTxSubmap = localTransactionMapSorted.subMap(remoteTrasnactionsStartDate, true, remoteTrasnactionsEndDate, true);

		logger.info("The considered transaction data range for Local subset: {} - {}. Size: {} days with transactions.", 
				localTxSubmap.firstKey().format(CommonUtils.DATE_TIME_FORMATTER), 
				localTxSubmap.lastKey().format(CommonUtils.DATE_TIME_FORMATTER),
				localTxSubmap.size());
		
		transactionsSizeComparator.compareTransactionListSizes(remoteTransactionMapSorted, localTxSubmap);
		return transactionsBalanceAnalyzer.analyzeTransactionBalances(remoteTransactionMapSorted, localTxSubmap, startingBalance);
	}


	/**
	 * Method compares the transaction list sizes: both global and per day
	 * 
	 * @param remoteTransactionMap
	 * @param localTxSubmap
	 * @return
	 * 
	 * @deprecated
	 */
	void compareTransactionListSizes(final TreeMap<LocalDate, List<TxDataRow>> remoteTransactionMap,
			final NavigableMap<LocalDate, List<TxDataRow>> localTxSubmap) {
		
		logger.debug("Comparing Remote vs Local days with transactions maps sizes.");

		if(remoteTransactionMap.size() != localTxSubmap.size()) {
			final Object[] errorMessageAttributes = new Object[] {
														remoteTransactionMap.firstKey().format(CommonUtils.DATE_TIME_FORMATTER),
														remoteTransactionMap.lastKey().format(CommonUtils.DATE_TIME_FORMATTER), 
														remoteTransactionMap.size(), 
														localTxSubmap.firstKey().format(CommonUtils.DATE_TIME_FORMATTER), 
														localTxSubmap.lastKey().format(CommonUtils.DATE_TIME_FORMATTER), 
														localTxSubmap.size()};
			
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
	 * 
	 * @deprecated
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
	 * 
	 * @deprecated
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
	
	/**
	 * @param unsortedMap
	 * @return
	 */
	TreeMap<LocalDate, List<TxDataRow>> sortMapByTxDate(final Map<LocalDate, List<TxDataRow>> unsortedMap) {
		
		return unsortedMap == null ? (new TreeMap<LocalDate, List<TxDataRow>>())
				: unsortedMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(
						Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(oldValue, newValue) -> oldValue, TreeMap::new));
	}

	@Override
	public List<TxDataRow> getAllTransactions() throws TransactionsNotFoundAtSourceException {
		populateTxDataRows();

		final List<TxDataRow> allTransactions = new ArrayList<>();
		populateTxDataRows(allTransactions, getRemoteTransactionMap(), true);
		populateTxDataRows(allTransactions, getLocalTransactionMap(), false);

//		if (!getRemoteTransactionMap().isEmpty()) {
//			for (final Entry<LocalDate, List<TxDataRow>> mapEntry : getRemoteTransactionMap().entrySet()) {
//				for (final TxDataRow txDataRow : mapEntry.getValue()) {
//					txDataRow.setRemote(true);
//				}
//				allTransactions.addAll(mapEntry.getValue());
//			}
//		}
//
//		if (!getLocalTransactionMap().isEmpty()) {
//			for (final Entry<LocalDate, List<TxDataRow>> mapEntry : getLocalTransactionMap().entrySet()) {
//				for (final TxDataRow txDataRow : mapEntry.getValue()) {
//					txDataRow.setRemote(false);
//				}
//				allTransactions.addAll(mapEntry.getValue());
//			}
//		}

		return allTransactions;
	}
	
	/**
	 * Populate all transcation collection and update the isremote flag
	 *
	 * @param allTxDataRows
	 * @param txDataRows
	 * 			can be Remote or Local Map where key is the tx date and value is the list of tx for this date
	 * @param isRemote
	 */
	private void populateTxDataRows(final List<TxDataRow> allTxDataRows,
			final Map<LocalDate, List<TxDataRow>> txDataRows, boolean isRemote) {

		if (!txDataRows.isEmpty()) {
			for (final Entry<LocalDate, List<TxDataRow>> mapEntry : txDataRows.entrySet()) {
				for (final TxDataRow txDataRow : mapEntry.getValue()) {
					txDataRow.setRemote(isRemote);
				}
				allTxDataRows.addAll(mapEntry.getValue());
			}
		}
	}
	
}
