package com.greenland.balanceManager.java.app.services;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.greenland.balanceManager.java.app.exceptions.TransactionListsSizeIncorrectException;
import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundException;
import com.greenland.balanceManager.java.app.model.TransactionsSourceDao;
import com.greenland.balanceManager.java.app.model.TxDataRow;


/**
 * Service which loops through the transactions from the different resources, usually remote vs local
 * and performs the comparison / check if the number of transactions match and if balances for the given period match.
 * 
 * @author Jura
 *
 */
public class TransactionComparatorServiceImpl implements TransactionComparatorService {
	
	static final String TX_NOT_FOUND_ERROR = "Transactions were not found. Number of Remote transactions %d. Number of Local transactions %d";
	private Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
	private Map<LocalDate,List<TxDataRow>> localTransactionMap = new HashMap<>();
	
	@Inject
	private TransactionsReaderService transactionsReaderService;
	
	@Inject
	private TransactionsSourceDao transactionsSourceDao;

	@Override
	public void executeTransactionComparison() throws FileNotFoundException {
		
		transactionsReaderService.populateTxMapsFromSource(getRemoteTransactionMap(), getLocalTransactionMap(), transactionsSourceDao);
		
		if(getRemoteTransactionMap().isEmpty() || getLocalTransactionMap().isEmpty()) {
			final String errorMessage = String.format(TX_NOT_FOUND_ERROR, remoteTransactionMap.size(), localTransactionMap.size());
			throw new TransactionsNotFoundException(errorMessage);
		}
		
		compareTransactions(getRemoteTransactionMap(), getLocalTransactionMap());
	}

	public Map<LocalDate, List<TxDataRow>> getRemoteTransactionMap() {
		return remoteTransactionMap;
	}

	public Map<LocalDate, List<TxDataRow>> getLocalTransactionMap() {
		return localTransactionMap;
	}

	public void compareTransactions(final Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			final Map<LocalDate, List<TxDataRow>> localTransactionMap) {
		
		// Sort maps
		final TreeMap<LocalDate, List<TxDataRow>> remoteTransactionMapSorted = sortMapByTxDate(remoteTransactionMap);
		final TreeMap<LocalDate, List<TxDataRow>> localTransactionMapSorted = sortMapByTxDate(localTransactionMap);
		
		// Getting the date range for which we are going to analyse the transactions based on remote tx map date range
		final LocalDate remoteTrasnactionsStartDate = (LocalDate) remoteTransactionMapSorted.firstKey(); 
		final LocalDate remoteTrasnactionsEndDate = (LocalDate) remoteTransactionMapSorted.lastKey(); 
		
		// Getting local transactions for the same date range as the remote transactions.
		// Local transaction map seem to have ALL transactions of All times!
		final NavigableMap<LocalDate, List<TxDataRow>> localTxSubmap = localTransactionMapSorted.subMap(remoteTrasnactionsStartDate, true, remoteTrasnactionsEndDate, true);
		
		final boolean sizesRemoteVsLocalMatch = compareTransactionListSizes(remoteTransactionMapSorted, localTxSubmap);
		
		boolean balancesRemoteVsLocalMatch = false;
		
		if (sizesRemoteVsLocalMatch) {
			balancesRemoteVsLocalMatch = analyzeTransactionBalances(remoteTransactionMapSorted, localTxSubmap);
		}
	}

	/**
	 * @param remoteTransactionMap
	 * @param localTransactionMap
	 * @return
	 */
	boolean analyzeTransactionBalances(final Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			final Map<LocalDate, List<TxDataRow>> localTransactionMap) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Method compares the transaction list sizes: both global and per day
	 * 
	 * @param remoteTransactionMap
	 * @param localTxSubmap
	 * @return
	 */
	boolean compareTransactionListSizes(final TreeMap<LocalDate, List<TxDataRow>> remoteTransactionMap,
			final NavigableMap<LocalDate, List<TxDataRow>> localTxSubmap) {

		boolean sizesMatch = false;
		
		if (remoteTransactionMap.size() == localTxSubmap.size()) {
			sizesMatch = compareTransactionListSizesPerDay(remoteTransactionMap, localTxSubmap);
		} 
		
		if(!sizesMatch) {
			final List<Object> errorMsgAtt = 
					Arrays.asList(remoteTransactionMap.firstKey().toString(),
							remoteTransactionMap.lastKey()/*
															 * , remoteTransactionMap.size(), localTxSubmap.firstKey(),
															 * localTxSubmap.lastEntry(), localTxSubmap.size()
															 */);
			final String errorMessage = String.format("Map sizes do not match. Remote dates [%s - $s], size: d. Local dates [s - s], size d",
					errorMsgAtt);
			throw new TransactionListsSizeIncorrectException(errorMessage);
		}
		
		return sizesMatch;
	}

	/**
	 * @param remoteTransactionMap
	 * @param localTransactionMap
	 * @return
	 */
	boolean compareTransactionListSizesPerDay(final Map<LocalDate, List<TxDataRow>> remoteTransactionMap, 
			final Map<LocalDate, List<TxDataRow>> localTransactionMap) {
		
		boolean sizesPerDayMatch =  false;

		return sizesPerDayMatch;
	}
	
	TreeMap<LocalDate, List<TxDataRow>> sortMapByTxDate(final Map<LocalDate, List<TxDataRow>> unsortedMap) {
		
		return unsortedMap.entrySet().stream()
				.sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
						(oldValue, newValue) -> oldValue, TreeMap::new));
	}
	
	
}
