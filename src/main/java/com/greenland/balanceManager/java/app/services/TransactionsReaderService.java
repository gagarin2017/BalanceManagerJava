package com.greenland.balanceManager.java.app.services;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.greenland.balanceManager.java.app.dao.TransactionsSourceDao;
import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
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
	 * @throws FileNotFoundException 
	 */
	void populateTxMapsFromSource(
			Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			Map<LocalDate, List<TxDataRow>> localTransactionMap, 
			TransactionsSourceDao transactionsSourceDao,
			Object... sources) throws TransactionsNotFoundAtSourceException;

}
