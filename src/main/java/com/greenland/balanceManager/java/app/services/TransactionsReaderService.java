package com.greenland.balanceManager.java.app.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.greenland.balanceManager.java.app.model.TransactionsSourceDao;
import com.greenland.balanceManager.java.app.model.TxDataRow;

/**
 * Service to read transactions from the source.
 * 
 * @author Jura
 *
 */
public interface TransactionsReaderService {

	/**
	 * Populate transactions maps from the source
	 * 
	 * @param remoteTransactionMap
	 * @param localTransactionMap
	 * @param transactionsSourceDao - the source where transactions are stored: files, databases, web services etc.
	 */
	void populateTxMapsFromSource(Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			Map<LocalDate, List<TxDataRow>> localTransactionMap, TransactionsSourceDao transactionsSourceDao);

}