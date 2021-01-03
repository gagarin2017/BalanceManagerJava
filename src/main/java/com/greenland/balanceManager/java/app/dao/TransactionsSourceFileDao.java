package com.greenland.balanceManager.java.app.dao;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
import com.greenland.balanceManager.java.app.model.TxDataRow;

/**
 * Generic interface for different transaction sources
 * 
 * @author Yura
 *
 */
public interface TransactionsSourceFileDao extends TransactionsSourceDao {

	/**
	 * Populates the transactions from the files specified as parameters for the method:
	 * <code>remoteFile</code>, which contains the remote transactions and
	 * <code>localFile</code>, which contains local transactions 
	 * 
	 * @param remoteTransactionMap
	 * 			remote transactions map to be populated
	 * @param remoteFile
	 * 			the path of the file, which contains the remote transactions
	 * @param localTransactionMap
	 * 			local transactions map to be populated
	 * @param localFile
	 * 			the path of the file, which contains the local transactions
	 * @throws TransactionsNotFoundAtSourceException
	 */
	void populateTxMapsFromSource(Map<LocalDate, List<TxDataRow>> remoteTransactionMap, String remoteFile,
			Map<LocalDate, List<TxDataRow>> localTransactionMap, String localFile) throws TransactionsNotFoundAtSourceException;

	/**
	 * @param fileName
	 * @param isRemote
	 * @return
	 * @throws FileNotFoundException
	 */
	Map<LocalDate, List<TxDataRow>> readTransactionsFromTheFile(String fileName, boolean isRemote) throws TransactionsNotFoundAtSourceException;
	
	/**
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 */
	Map<LocalDate, List<TxDataRow>> readTransactionsFromTheFile(String fileName) throws TransactionsNotFoundAtSourceException;


}
