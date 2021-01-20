package com.greenland.balanceManager.java.app.external;

import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
import com.greenland.balanceManager.java.app.model.TxDataRow;

import java.math.BigDecimal;
import java.util.List;

import org.json.JSONObject;

public interface BalanceManagerExternal {
	
    public List<TxDataRow> getAllTransactions() throws TransactionsNotFoundAtSourceException;
    
    /**
     * @param remoteTransactions
     * @param localTransactions
     * @return {@link JSONObject} containing the balance summary (with transaction details) for the transactions passed
     * @throws TransactionsNotFoundAtSourceException 
     */
    public JSONObject getBalanceComparison(
    		JSONObject remoteTransactions, JSONObject localTransactions, BigDecimal startingBalance) throws TransactionsNotFoundAtSourceException;
}
