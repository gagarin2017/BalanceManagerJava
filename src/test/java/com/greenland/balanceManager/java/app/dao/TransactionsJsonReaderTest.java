package com.greenland.balanceManager.java.app.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.greenland.balanceManager.java.app.CommonUtils;
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
	private static final String MINUS_FIVE_SPACE = "-5.00 ";
	
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
    	remoteTxsJson.put(CommonUtils.REMOTE_TRANSACTIONS_JSON_KEY, remoteTransactions);
    	final JSONObject localTxsJson = new JSONObject();
    	final JSONArray localTransactions = new JSONArray();
    	localTxsJson.put(CommonUtils.LOCAL_TRANSACTIONS_JSON_KEY, localTransactions);
    	
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
    	
    	final JSONObject allTxsSourceJson = new JSONObject();

    	final JSONArray remoteTransactions = new JSONArray();
    	
    	final JSONObject remoteTx = new JSONObject();
    	remoteTx.put(CommonUtils.TX_DATE_JSON_KEY, LocalDate.now().toEpochDay());
    	remoteTx.put(CommonUtils.TX_ACCOUNT_NAME_JSON_KEY, ACC_NAME_1);
    	remoteTx.put(CommonUtils.TX_DESCRIPTION_JSON_KEY, DESCRIPTION_1);
    	remoteTx.put(CommonUtils.TX_MEMO_JSON_KEY, MEMO_1);
    	remoteTx.put(CommonUtils.TX_CATEGORY_NAME_JSON_KEY, CATEGORY_NAME_1);
    	remoteTx.put(CommonUtils.TX_TAG_JSON_KEY, TAG_1);
    	remoteTx.put(CommonUtils.TX_RECONCILED_JSON_KEY, RECONCILE_STRING_1);
    	remoteTx.put(CommonUtils.TX_AMOUNT_JSON_KEY, TX_AMOUNT_1);
    	
    	final JSONObject remoteTx1 = new JSONObject();
    	remoteTx1.put(CommonUtils.TX_DATE_JSON_KEY, LocalDate.now().toEpochDay());
    	remoteTx1.put(CommonUtils.TX_ACCOUNT_NAME_JSON_KEY, ACC_NAME_1);
    	remoteTx1.put(CommonUtils.TX_DESCRIPTION_JSON_KEY, DESCRIPTION_1);
    	remoteTx1.put(CommonUtils.TX_MEMO_JSON_KEY, MEMO_1);
    	remoteTx1.put(CommonUtils.TX_CATEGORY_NAME_JSON_KEY, CATEGORY_NAME_1);
    	remoteTx1.put(CommonUtils.TX_TAG_JSON_KEY, TAG_1);
    	remoteTx1.put(CommonUtils.TX_RECONCILED_JSON_KEY, RECONCILE_STRING_1);
    	remoteTx1.put(CommonUtils.TX_AMOUNT_JSON_KEY, TX_AMOUNT_1);
    	
    	remoteTransactions.put(remoteTx);
    	allTxsSourceJson.put(CommonUtils.INPUT_REMOTE_TRANSACTIONS_JSON_KEY, remoteTransactions);
    	
    	final JSONArray localTransactions = new JSONArray();
    	
    	final JSONObject localTx = new JSONObject();
    	localTx.put(CommonUtils.TX_DATE_JSON_KEY, LocalDate.now().toEpochDay());
    	localTx.put(CommonUtils.TX_ACCOUNT_NAME_JSON_KEY, ACC_NAME_1);
    	localTx.put(CommonUtils.TX_DESCRIPTION_JSON_KEY, DESCRIPTION_1);
    	localTx.put(CommonUtils.TX_MEMO_JSON_KEY, MEMO_1);
    	localTx.put(CommonUtils.TX_CATEGORY_NAME_JSON_KEY, CATEGORY_NAME_1);
    	localTx.put(CommonUtils.TX_TAG_JSON_KEY, TAG_1);
    	localTx.put(CommonUtils.TX_RECONCILED_JSON_KEY, RECONCILE_STRING_1);
    	localTx.put(CommonUtils.TX_AMOUNT_JSON_KEY, TX_AMOUNT_1);
    	
    	localTransactions.put(localTx);
    	
    	allTxsSourceJson.put(CommonUtils.INPUT_LOCAL_TRANSACTIONS_JSON_KEY, localTransactions);
    	
    	
    	// Method under test
    	transactionsJsonReader.populateTxMapsFromSource(remoteTransactionMap, localTransactionMap, allTxsSourceJson);
    	
    	// Method under test + Verifications
    	assertThat(remoteTransactionMap.size(), is(1));
    	assertThat(localTransactionMap.size(), is(1));
    }
    
    @Disabled
    @Test
    @DisplayName("TransactionsNotFoundAtSourceException exception is thrown when sources json have invalid amount - empty string")
    void populateTxMapsFromSource_source_jsons_have_invalid_amount_empty_string() throws TransactionsNotFoundAtSourceException {
    	// Setup
    	final Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
    	final Map<LocalDate, List<TxDataRow>> localTransactionMap = new HashMap<>();
    	
    	final JSONObject allTxsSourceJson = new JSONObject();
    	
    	final JSONArray remoteTransactions = new JSONArray();
    	
    	final JSONObject remoteTx = new JSONObject();
    	remoteTx.put(CommonUtils.TX_DATE_JSON_KEY, LocalDate.now().toEpochDay());
    	remoteTx.put(CommonUtils.TX_ACCOUNT_NAME_JSON_KEY, ACC_NAME_1);
    	remoteTx.put(CommonUtils.TX_DESCRIPTION_JSON_KEY, DESCRIPTION_1);
    	remoteTx.put(CommonUtils.TX_MEMO_JSON_KEY, MEMO_1);
    	remoteTx.put(CommonUtils.TX_CATEGORY_NAME_JSON_KEY, CATEGORY_NAME_1);
    	remoteTx.put(CommonUtils.TX_TAG_JSON_KEY, TAG_1);
    	remoteTx.put(CommonUtils.TX_RECONCILED_JSON_KEY, RECONCILE_STRING_1);
    	remoteTx.put(CommonUtils.TX_AMOUNT_JSON_KEY, "");
    	
    	remoteTransactions.put(remoteTx);
    	allTxsSourceJson.put(CommonUtils.INPUT_REMOTE_TRANSACTIONS_JSON_KEY, remoteTransactions);
    	
    	final JSONArray localTransactions = new JSONArray();
    	
    	final JSONObject localTx = new JSONObject();
    	localTx.put(CommonUtils.TX_DATE_JSON_KEY, LocalDate.now().toEpochDay());
    	localTx.put(CommonUtils.TX_ACCOUNT_NAME_JSON_KEY, ACC_NAME_1);
    	localTx.put(CommonUtils.TX_DESCRIPTION_JSON_KEY, DESCRIPTION_1);
    	localTx.put(CommonUtils.TX_MEMO_JSON_KEY, MEMO_1);
    	localTx.put(CommonUtils.TX_CATEGORY_NAME_JSON_KEY, CATEGORY_NAME_1);
    	localTx.put(CommonUtils.TX_TAG_JSON_KEY, TAG_1);
    	localTx.put(CommonUtils.TX_RECONCILED_JSON_KEY, RECONCILE_STRING_1);
    	localTx.put(CommonUtils.TX_AMOUNT_JSON_KEY, MINUS_FIVE_SPACE);
    	
    	localTransactions.put(localTx);
    	
    	allTxsSourceJson.put(CommonUtils.INPUT_LOCAL_TRANSACTIONS_JSON_KEY, localTransactions);
    	
    	
    	// Method under test
    	transactionsJsonReader.populateTxMapsFromSource(remoteTransactionMap, localTransactionMap, allTxsSourceJson);
    	
    	// Method under test + Verifications
    	assertThat(remoteTransactionMap.size(), is(1));
    	final LocalDate actualRowDate = remoteTransactionMap.keySet().stream().findFirst().get();
		assertThat("Amount is set to Long.MIN_VALUE", remoteTransactionMap.get(actualRowDate).get(0).getDebitAmount(), is(new BigDecimal(Long.MIN_VALUE)));
    	assertThat(localTransactionMap.size(), is(1));
    }
    
}
