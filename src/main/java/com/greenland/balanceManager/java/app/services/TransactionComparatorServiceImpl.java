package com.greenland.balanceManager.java.app.services;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
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
	private Map<LocalDate, List<TxDataRow>> remoteTransactionMap;
	private Map<LocalDate,List<TxDataRow>> localTransactionMap;
	
	@Inject
	private TransactionsReaderService transactionsReaderService;
	
	@Inject
	private TransactionsSourceDao transactionsSourceDao;

	@Override
	public void executeTransactionComparison() throws TransactionsNotFoundException, FileNotFoundException {
		
		remoteTransactionMap = new HashMap<>();
		localTransactionMap = new HashMap<>();
		
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

	public void compareTransactions(final Map<LocalDate, List<TxDataRow>> remoteTransactionMap2,
			final Map<LocalDate, List<TxDataRow>> localTransactionMap2) {
		// TODO Auto-generated method stub
		
	}
	
	
}
