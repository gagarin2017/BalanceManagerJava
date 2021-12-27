package com.greenland.balanceManager.java.app.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.greenland.balanceManager.java.app.external.domain.OutputTxData;
import com.greenland.balanceManager.java.app.model.TxDataRow;

/**
 * @author Jura
 *
 */
public interface TransactionsSizeComparator {

	/**
	 * Compare both collections and figure out if the number of transactions match remote vs local
	 * 
	 * @param remoteTransactionMapSorted
	 * @param localTxSubmap
	 * @param infoMessages
	 * 			StringBuilder collection of info messages	
	 * @param errorMessages
	 * 			StringBuilder collection of error messages	
	 */
	@Deprecated
	Map<String, Map<LocalDate, List<TxDataRow>>> compareRemoteVsLocalTransactions(final Map<LocalDate, List<TxDataRow>> remoteTransactionMapSorted,
			Map<LocalDate, List<TxDataRow>> localTxSubmap, List<String> infoMessages, List<String> errorMessages);

	Map<String, Map<LocalDate, List<TxDataRow>>> compareRemoteVsLocalTransactions(TreeMap<LocalDate, List<TxDataRow>> remoteTransactionMapSorted,
			NavigableMap<LocalDate, List<TxDataRow>> localTxSubmap, OutputTxData outputTxData);


}
