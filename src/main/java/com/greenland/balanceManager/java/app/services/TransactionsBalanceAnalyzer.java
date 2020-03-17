package com.greenland.balanceManager.java.app.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.greenland.balanceManager.java.app.model.TxDataRow;

/**
 * @author Jura
 *
 */
public interface TransactionsBalanceAnalyzer {

	boolean analyzeTransactionBalances(Map<LocalDate, List<TxDataRow>> remoteTransactionMapSorted,
			Map<LocalDate, List<TxDataRow>> localTxSubmap);

}
