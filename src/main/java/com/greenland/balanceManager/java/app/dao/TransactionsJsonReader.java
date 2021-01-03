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

	private static Logger logger = LogManager.getLogger(TransactionsJsonReader.class);

	@Override
	public void populateTxMapsFromSource(Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			Map<LocalDate, List<TxDataRow>> localTransactionMap, Object... sources)
			throws TransactionsNotFoundAtSourceException {

		// expecting two JSONObject sources: remote and local
		if (sources.length == 2 && sources[0] instanceof JSONObject && sources[1] instanceof JSONObject) {
			
			final JSONObject remoteTransactionsJson = (JSONObject) sources[0];
			final JSONObject localTransactionsJson = (JSONObject) sources[1];
			
			final JSONArray remoteTransactions = remoteTransactionsJson.getJSONArray("remoteTransactions");
			final JSONArray localTransactions = localTransactionsJson.getJSONArray("localTransactions");
			
			final Pair<JSONArray, JSONArray> inputTransactions = Pair.of(remoteTransactions, localTransactions);

			logger.debug("Getting remote transactions from the passed Json: {}", inputTransactions.getLeft());
			remoteTransactionMap.putAll(readTransactionsFromJson(inputTransactions.getLeft()));

			logger.debug("Getting local transactions from the passed Json: {}", inputTransactions.getRight());
			localTransactionMap.putAll(readTransactionsFromJson(inputTransactions.getRight()));
		} else {
			throw new TransactionsNotFoundAtSourceException("The sources for the transactions is not provided",
					new IOException());
		}

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
			final LocalDate txDate = LocalDate.ofEpochDay(jsonRow.getLong("txDate"));
			
			final TxDataRow txDataRow = new TxDataRow();
			txDataRow.setTxDate(txDate);
			txDataRow.setAccountName(jsonRow.getString("txAccountName"));
			txDataRow.setCategoryName(jsonRow.getString("txCategoryName"));
			txDataRow.setReconsiled(jsonRow.getString("txReconciled"));
			txDataRow.setAmount(jsonRow.getBigDecimal("txAmount"));
			
			allRows.add(txDataRow);
        }
		
		final Map<LocalDate, List<TxDataRow>> grupedByTxDateMap = allRows.stream()
				.collect(Collectors.groupingBy(row -> row.getTxDate()));
		return grupedByTxDateMap;
	}

}
