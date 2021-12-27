package com.greenland.balanceManager.java.app.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
import com.greenland.balanceManager.java.app.external.domain.OutputTxData;
import com.greenland.balanceManager.java.app.model.TxDataRow;

/**
 * Generic interface for different transaction sources
 * 
 * @author Yura
 *
 */
public interface TransactionsSourceDao {

	/**
	 * Populates the transactions from the files from the source.
	 * 
	 * @param remoteTransactionMap
	 * @param localTransactionMap
	 * @param sources
	 * 			defines the sources via the transactions are passed through e.g. files, JSONObjects, streams
	 * @throws TransactionsNotFoundAtSourceException
	 */
	@Deprecated
	void populateTxMapsFromSource(Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			Map<LocalDate, List<TxDataRow>> localTransactionMap, Object... sources) throws TransactionsNotFoundAtSourceException;
	
	/**
	 * Populates the transactions from the files from the source.
	 * 
	 * @param remoteTransactionMap
	 * @param localTransactionMap
	 * @param inputTxData
	 * @param outputTxData
	 * 			expecting a POJO containing remote and local transactions
	 */
	void populateTxMapsFromRemoteApp(Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			Map<LocalDate, List<TxDataRow>> localTransactionMap, Object inputTxData, OutputTxData outputTxData);

}
