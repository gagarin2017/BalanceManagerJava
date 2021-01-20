package com.greenland.balanceManager.java.app.dao;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
import com.greenland.balanceManager.java.app.model.TxDataRow;

public class TransactionsJsonReader implements TransactionsSourceJsonDao {

	public static final String LOCAL_TRANSACTIONS_JSON_KEY = "localTransactions";
	public static final String REMOTE_TRANSACTIONS_JSON_KEY = "remoteTransactions";

	public static final String TX_DATE_JSON_KEY = "Date";
	public static final String TX_ACCOUNT_NAME_JSON_KEY = "Account";
	public static final String TX_DESCRIPTION_JSON_KEY = "Description";
	public static final String TX_MEMO_JSON_KEY = "Memo";
	public static final String TX_CATEGORY_NAME_JSON_KEY = "Category";
	public static final String TX_TAG_JSON_KEY = "Tag";
	public static final String TX_RECONCILED_JSON_KEY = "Clr";
	public static final String TX_AMOUNT_JSON_KEY = "Amount";

	public static Logger logger = LogManager.getLogger(TransactionsJsonReader.class);

	@Override
	public void populateTxMapsFromSource(Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			Map<LocalDate, List<TxDataRow>> localTransactionMap, Object... sources)
			throws TransactionsNotFoundAtSourceException {

		// expecting two JSONObject sources: remote and local
		if (sources != null && sources.length == 2 && 
				sources[0] instanceof JSONObject && sources[1] instanceof JSONObject) {
			
			final JSONObject remoteTransactionsJson = (JSONObject) sources[0];
			final JSONObject localTransactionsJson = (JSONObject) sources[1];
			
			if(transacationsMissing(remoteTransactionsJson, REMOTE_TRANSACTIONS_JSON_KEY)) {
				throw new TransactionsNotFoundAtSourceException("The sources remote transactions json doesnt contain remote transactions",
						new IOException());
			}
			
			if(transacationsMissing(localTransactionsJson, LOCAL_TRANSACTIONS_JSON_KEY)) {
				throw new TransactionsNotFoundAtSourceException("The sources local transactions json doesnt contain local transactions",
						new IOException());
			}
			
			final JSONArray remoteTransactions = remoteTransactionsJson.getJSONArray(REMOTE_TRANSACTIONS_JSON_KEY);
			final JSONArray localTransactions = localTransactionsJson.getJSONArray(LOCAL_TRANSACTIONS_JSON_KEY);
			
			final Pair<JSONArray, JSONArray> inputTransactions = Pair.of(remoteTransactions, localTransactions);

			logger.debug("Getting remote transactions from the passed Json: {}", inputTransactions.getLeft());
			remoteTransactionMap.putAll(readTransactionsFromJson(inputTransactions.getLeft()));

			logger.debug("Getting local transactions from the passed Json: {}", inputTransactions.getRight());
			localTransactionMap.putAll(readTransactionsFromJson(inputTransactions.getRight()));
		} else {
			throw new TransactionsNotFoundAtSourceException("The sources for the transactions are not provided",
					new IOException());
		}

	}

	/**
	 * @param remoteTransactionsJson
	 * @return
	 */
	private boolean transacationsMissing(final JSONObject remoteTransactionsJson, final String transactionsJsonKey) {
		
		final boolean isMissing;
		
		final boolean txListMissing = !remoteTransactionsJson.has(transactionsJsonKey);
		
		if (txListMissing) {
			isMissing = true;
		} else {
			final Object txsJsonObject = txListMissing ? null : remoteTransactionsJson.get(transactionsJsonKey);
			final JSONArray txJsonArray = (txsJsonObject != null && txsJsonObject instanceof JSONArray) ? (JSONArray)remoteTransactionsJson.get(transactionsJsonKey) : null;
			isMissing = (txJsonArray != null && txJsonArray.length() > 0) ? false : true;
		}
		
		return isMissing;
	}

	/**
	 * Method creates the collection of {@link TxDataRow} and group them by tx row date.
	 * 
	 * @param transactions
	 * @return {@link HashMap} where key is the {@link TxDataRow} date and value is the transaction list for that date
	 */
	private Map<LocalDate, List<TxDataRow>> readTransactionsFromJson(final JSONArray transactions) {
		
		final List<TxDataRow> allRows = new ArrayList<>();
		
		for (int i = 0; i < transactions.length(); i++) {
			
			final JSONObject jsonRow = transactions.getJSONObject(i);
			final LocalDate txDate = LocalDate.ofEpochDay(jsonRow.getLong(TX_DATE_JSON_KEY));
			
			final TxDataRow txDataRow = new TxDataRow();
			txDataRow.setTxDate(txDate);
			txDataRow.setAccountName(jsonRow.getString(TX_ACCOUNT_NAME_JSON_KEY));
			txDataRow.setDescription(jsonRow.getString(TX_DESCRIPTION_JSON_KEY));
			txDataRow.setMemo(jsonRow.getString(TX_MEMO_JSON_KEY));
			txDataRow.setCategoryName(jsonRow.getString(TX_CATEGORY_NAME_JSON_KEY));
			txDataRow.setTag(jsonRow.getString(TX_TAG_JSON_KEY));
			txDataRow.setReconsiled(jsonRow.getString(TX_RECONCILED_JSON_KEY));
			txDataRow.setAmount(jsonRow.getBigDecimal(TX_AMOUNT_JSON_KEY));
			
			allRows.add(txDataRow);
        }
		
		final Map<LocalDate, List<TxDataRow>> grupedByTxDateMap = allRows.stream()
				.collect(Collectors.groupingBy(row -> row.getTxDate()));
		return grupedByTxDateMap;
	}

}
