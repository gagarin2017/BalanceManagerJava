package com.greenland.balanceManager.java.app.external;

import java.util.List;

import org.json.JSONObject;

import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
import com.greenland.balanceManager.java.app.external.domain.InputTxData;
import com.greenland.balanceManager.java.app.external.domain.OutputTxData;
import com.greenland.balanceManager.java.app.model.TxDataRow;

public interface BalanceManagerExternal {
	
    public List<TxDataRow> getAllTransactions() throws TransactionsNotFoundAtSourceException;
    
    /**
     * @param remoteTransactions containing remote and local file transactions plust starting balance
     * @return {@link JSONObject} containing the balance summary (with transaction details) for the transactions passed
     * @throws TransactionsNotFoundAtSourceException 
     */
    @Deprecated
    public JSONObject getBalanceComparison(
    		JSONObject remoteTransactionJSONObject) throws TransactionsNotFoundAtSourceException;
    
    /**
     * @param remoteTransactionsJsonObject
     * 			{@link InputTxData} remoteTransactions containing remote and local file transactions plust starting balance
     * @return {@link OutputTxData} containing the balance summary (with transaction details) for the transactions passed
     * @throws TransactionsNotFoundAtSourceException 
     */
    public OutputTxData analyzeTransactions(final InputTxData remoteTransactionsJsonObject) throws TransactionsNotFoundAtSourceException;

    @Deprecated
	String analyzeTransactionsAsString(InputTxData remoteTransactionsJsonObject);
}
