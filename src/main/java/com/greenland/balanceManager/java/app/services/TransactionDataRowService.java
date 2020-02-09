package com.greenland.balanceManager.java.app.services;

import com.greenland.balanceManager.java.app.model.TxDataRow;

public interface TransactionDataRowService {

	TxDataRow parseRemoteFileTransaction(String data);

	TxDataRow parseLocalFileTransaction(String data);

	Object[] isValidTransactionRow(String[] txRowArray);

}
