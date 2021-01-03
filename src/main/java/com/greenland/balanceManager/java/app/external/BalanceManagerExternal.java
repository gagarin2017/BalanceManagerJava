package com.greenland.balanceManager.java.app.external;

import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
import com.greenland.balanceManager.java.app.model.TxDataRow;
import java.util.List;

public interface BalanceManagerExternal {
	
    public List<TxDataRow> getAllTransactions() throws TransactionsNotFoundAtSourceException;
}
