package com.greenland.balanceManager.java.app.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.greenland.balanceManager.java.app.model.TxDataRow;

/**
 * @author Jura
 *
 */
public interface TransactionsBalanceAnalyzer {

	JSONObject analyzeTransactionBalances(Map<LocalDate, List<TxDataRow>> remoteTransactionMapSorted,
			Map<LocalDate, List<TxDataRow>> localTxSubmap, final BigDecimal startingBalance);

}
