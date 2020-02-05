package com.greenland.balanceManager.java.app.model;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TransactionsSourceDao {

	void populateTxMapsFromSource(Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			Map<LocalDate, List<TxDataRow>> localTransactionMap) throws FileNotFoundException;

}
