package com.greenland.balanceManager.java.app.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.json.JSONObject;

import com.greenland.balanceManager.java.app.external.domain.OutputTxData;
import com.greenland.balanceManager.java.app.model.TxDataRow;

/**
 * @author Jura
 *
 */
public interface TransactionsBalanceAnalyzer {

	@Deprecated
	JSONObject analyzeTransactionBalances(Map<LocalDate, List<TxDataRow>> remoteTransactionMapSorted,
			Map<LocalDate, List<TxDataRow>> localTxSubmap, final BigDecimal startingBalance, Map<String, Map<LocalDate, List<TxDataRow>>> missingTransactions, List<String> infoMessages, List<String> errorMessages);

	void analyzeTransactionBalances(TreeMap<LocalDate, List<TxDataRow>> remoteTransactionMap,
			NavigableMap<LocalDate, List<TxDataRow>> localTransactionMap, Map<String, Map<LocalDate, List<TxDataRow>>> missingTransactions, OutputTxData outputTxData);

}
