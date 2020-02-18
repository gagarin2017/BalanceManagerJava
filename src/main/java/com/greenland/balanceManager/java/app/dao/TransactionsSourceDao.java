package com.greenland.balanceManager.java.app.dao;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.greenland.balanceManager.java.app.model.TxDataRow;

public interface TransactionsSourceDao {

	void populateTxMapsFromSource(Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			Map<LocalDate, List<TxDataRow>> localTransactionMap) throws FileNotFoundException;

}