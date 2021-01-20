package com.greenland.balanceManager.java.app.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
import com.greenland.balanceManager.java.app.model.TxDataRow;

import mockit.Tested;

public class TransactionsJsonReaderTest {
	
	private static final String ACC_NAME_1 = "Bank acc";
	private static final String DESCRIPTION_1 = "description";
	private static final String MEMO_1 = "memo";
	private static final String CATEGORY_NAME_1 = "Category";
	private static final String TAG_1 = "tag";
	private static final String RECONCILE_STRING_1 = "R";
	private static final String TX_AMOUNT_1 = "-15.11";
	@Tested
	private TransactionsJsonReader transactionsJsonReader;
	
    @Test
    @DisplayName("TransactionsNotFoundAtSourceException exception is thrown when sources passed as null")
    void populateTxMapsFromSource_sources_null_exception() throws TransactionsNotFoundAtSourceException {
    	// Setup
    	final Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
		final Map<LocalDate, List<TxDataRow>> localTransactionMap = new HashMap<>();
    	
    	// Method under test + Verifications
		assertThrows(TransactionsNotFoundAtSourceException.class, 
    			() -> transactionsJsonReader.populateTxMapsFromSource(remoteTransactionMap, localTransactionMap, null));
    }
    
    @Test
    @DisplayName("TransactionsNotFoundAtSourceException exception is thrown when sources not passed")
    void populateTxMapsFromSource_sources_not_passed_exception() throws TransactionsNotFoundAtSourceException {
    	// Setup
    	final Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
    	final Map<LocalDate, List<TxDataRow>> localTransactionMap = new HashMap<>();
    	
    	// Method under test + Verifications
    	assertThrows(TransactionsNotFoundAtSourceException.class, 
    			() -> transactionsJsonReader.populateTxMapsFromSource(remoteTransactionMap, localTransactionMap));
    }
    
    @Test
    @DisplayName("TransactionsNotFoundAtSourceException exception is thrown when sources json objects both are empty")
    void populateTxMapsFromSource_sources_are_empty_json_exception() throws TransactionsNotFoundAtSourceException {
    	// Setup
    	final Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
    	final Map<LocalDate, List<TxDataRow>> localTransactionMap = new HashMap<>();
    	
    	final JSONObject remoteTxsJson = new JSONObject();
    	final JSONObject localTxsJson = new JSONObject();
    	
    	// Method under test + Verifications
    	assertThrows(TransactionsNotFoundAtSourceException.class, 
    			() -> transactionsJsonReader.populateTxMapsFromSource(remoteTransactionMap, localTransactionMap, remoteTxsJson, localTxsJson));
    }
    
    @Test
    @DisplayName("TransactionsNotFoundAtSourceException exception is thrown when sources json have empty lists of remote and local transactions")
    void populateTxMapsFromSource_source_jsons_have_empty_transaction_lists_exception() throws TransactionsNotFoundAtSourceException {
    	// Setup
    	final Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
    	final Map<LocalDate, List<TxDataRow>> localTransactionMap = new HashMap<>();
    	
    	final JSONObject remoteTxsJson = new JSONObject();
    	final JSONArray remoteTransactions = new JSONArray();
    	remoteTxsJson.put(TransactionsJsonReader.REMOTE_TRANSACTIONS_JSON_KEY, remoteTransactions);
    	final JSONObject localTxsJson = new JSONObject();
    	final JSONArray localTransactions = new JSONArray();
    	localTxsJson.put(TransactionsJsonReader.LOCAL_TRANSACTIONS_JSON_KEY, localTransactions);
    	
    	// Method under test + Verifications
    	assertThrows(TransactionsNotFoundAtSourceException.class, 
    			() -> transactionsJsonReader.populateTxMapsFromSource(remoteTransactionMap, localTransactionMap, remoteTxsJson, localTxsJson));
    }
    
    @Test
    @DisplayName("TransactionsNotFoundAtSourceException exception is thrown when sources json have remote and local transactions")
    void populateTxMapsFromSource_source_jsons_have_remote_and_local_transaction_lists() throws TransactionsNotFoundAtSourceException {
    	// Setup
    	final Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
    	final Map<LocalDate, List<TxDataRow>> localTransactionMap = new HashMap<>();
    	
    	final JSONObject remoteTxsJson = new JSONObject();
    	final JSONArray remoteTransactions = new JSONArray();
    	
    	final JSONObject remoteTx = new JSONObject();
    	remoteTx.put(TransactionsJsonReader.TX_DATE_JSON_KEY, LocalDate.now().toEpochDay());
    	remoteTx.put(TransactionsJsonReader.TX_ACCOUNT_NAME_JSON_KEY, ACC_NAME_1);
    	remoteTx.put(TransactionsJsonReader.TX_DESCRIPTION_JSON_KEY, DESCRIPTION_1);
    	remoteTx.put(TransactionsJsonReader.TX_MEMO_JSON_KEY, MEMO_1);
    	remoteTx.put(TransactionsJsonReader.TX_CATEGORY_NAME_JSON_KEY, CATEGORY_NAME_1);
    	remoteTx.put(TransactionsJsonReader.TX_TAG_JSON_KEY, TAG_1);
    	remoteTx.put(TransactionsJsonReader.TX_RECONCILED_JSON_KEY, RECONCILE_STRING_1);
    	remoteTx.put(TransactionsJsonReader.TX_AMOUNT_JSON_KEY, TX_AMOUNT_1);
    	
    	final JSONObject remoteTx1 = new JSONObject();
    	remoteTx1.put(TransactionsJsonReader.TX_DATE_JSON_KEY, LocalDate.now().toEpochDay());
    	remoteTx1.put(TransactionsJsonReader.TX_ACCOUNT_NAME_JSON_KEY, ACC_NAME_1);
    	remoteTx1.put(TransactionsJsonReader.TX_DESCRIPTION_JSON_KEY, DESCRIPTION_1);
    	remoteTx1.put(TransactionsJsonReader.TX_MEMO_JSON_KEY, MEMO_1);
    	remoteTx1.put(TransactionsJsonReader.TX_CATEGORY_NAME_JSON_KEY, CATEGORY_NAME_1);
    	remoteTx1.put(TransactionsJsonReader.TX_TAG_JSON_KEY, TAG_1);
    	remoteTx1.put(TransactionsJsonReader.TX_RECONCILED_JSON_KEY, RECONCILE_STRING_1);
    	remoteTx1.put(TransactionsJsonReader.TX_AMOUNT_JSON_KEY, TX_AMOUNT_1);
    	
    	remoteTransactions.put(remoteTx);
    	remoteTxsJson.put(TransactionsJsonReader.REMOTE_TRANSACTIONS_JSON_KEY, remoteTransactions);
    	System.out.println(remoteTxsJson);
    	
    	final JSONObject localTxsJson = new JSONObject();
    	final JSONArray localTransactions = new JSONArray();
    	
    	final JSONObject localTx = new JSONObject();
    	localTx.put(TransactionsJsonReader.TX_DATE_JSON_KEY, LocalDate.now().toEpochDay());
    	localTx.put(TransactionsJsonReader.TX_ACCOUNT_NAME_JSON_KEY, ACC_NAME_1);
    	localTx.put(TransactionsJsonReader.TX_DESCRIPTION_JSON_KEY, DESCRIPTION_1);
    	localTx.put(TransactionsJsonReader.TX_MEMO_JSON_KEY, MEMO_1);
    	localTx.put(TransactionsJsonReader.TX_CATEGORY_NAME_JSON_KEY, CATEGORY_NAME_1);
    	localTx.put(TransactionsJsonReader.TX_TAG_JSON_KEY, TAG_1);
    	localTx.put(TransactionsJsonReader.TX_RECONCILED_JSON_KEY, RECONCILE_STRING_1);
    	localTx.put(TransactionsJsonReader.TX_AMOUNT_JSON_KEY, TX_AMOUNT_1);
    	
    	localTransactions.put(localTx);
    	
    	localTxsJson.put(TransactionsJsonReader.LOCAL_TRANSACTIONS_JSON_KEY, localTransactions);

    	System.out.println(localTxsJson);
    	
    	// Method under test
    	transactionsJsonReader.populateTxMapsFromSource(remoteTransactionMap, localTransactionMap, remoteTxsJson, localTxsJson);
    	
    	// Method under test + Verifications
    	assertThat(remoteTransactionMap.size(), is(1));
    	assertThat(localTransactionMap.size(), is(1));
    }
    
}
