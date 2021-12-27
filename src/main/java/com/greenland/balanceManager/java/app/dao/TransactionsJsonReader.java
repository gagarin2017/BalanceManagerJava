package com.greenland.balanceManager.java.app.dao;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.greenland.balanceManager.java.app.CommonUtils;
import com.greenland.balanceManager.java.app.exceptions.JsonValuesParsingException;
import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
import com.greenland.balanceManager.java.app.external.domain.InputTxData;
import com.greenland.balanceManager.java.app.external.domain.OutputTxData;
import com.greenland.balanceManager.java.app.model.TxDataRow;

public class TransactionsJsonReader implements TransactionsSourceJsonDao {

	private static final String SOURCE_DATA_MISSING_OR_NOT_SUPPORTED_FORMAT_ERR = "The sources for the transactions are not provided or source data format is not supported";
	private static final String TX_GROUP_REMOTE = "remote";
	private static final String TX_GROUP_LOCAL = "local";

	public static Logger logger = LogManager.getLogger(TransactionsJsonReader.class);

	private static final String THE_INPUT_JSON_DOESNT_CONTAIN_TRANSACTIONS_GROUP_ERR = "The input json doesnt contain %s transactions";

	@Deprecated
	@Override
	public void populateTxMapsFromSource(Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			Map<LocalDate, List<TxDataRow>> localTransactionMap, Object... sources)
			throws TransactionsNotFoundAtSourceException {

		// expecting one JSONObject source containing both remote and local transactions
		if (sources != null && sources.length == 1 && sources[0] instanceof JSONObject) {
			
			final JSONObject allTransactionsJson = (JSONObject) sources[0];
			
			if (CommonUtils.transacationsMissing(allTransactionsJson, CommonUtils.INPUT_REMOTE_TRANSACTIONS_JSON_KEY)) {
				throw new TransactionsNotFoundAtSourceException(
						"The input json doesnt contain remote transactions key: "
								+ CommonUtils.INPUT_REMOTE_TRANSACTIONS_JSON_KEY,
						new IOException());
			}

			if (CommonUtils.transacationsMissing(allTransactionsJson, CommonUtils.INPUT_LOCAL_TRANSACTIONS_JSON_KEY)) {
				throw new TransactionsNotFoundAtSourceException("The input json doesnt contain local transactions key: "
						+ CommonUtils.INPUT_LOCAL_TRANSACTIONS_JSON_KEY, new IOException());
			}
			
			final JSONArray remoteTransactions = allTransactionsJson.getJSONArray(CommonUtils.INPUT_REMOTE_TRANSACTIONS_JSON_KEY);
			final JSONArray localTransactions = allTransactionsJson.getJSONArray(CommonUtils.INPUT_LOCAL_TRANSACTIONS_JSON_KEY);
			
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
	
	@Override
	public void populateTxMapsFromRemoteApp(final Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			final Map<LocalDate, List<TxDataRow>> localTransactionMap, final Object inputData, final OutputTxData outputTxData) {
		
		// expecting one POJO source containing both remote and local transactions
		if (inputData != null && inputData instanceof InputTxData) {
			
			final InputTxData inputTxData = (InputTxData) inputData;
			
			if (inputTxData.getRemoteAccountTxsData() == null || inputTxData.getRemoteAccountTxsData().isEmpty()) {
				logErrorThrowException(outputTxData, THE_INPUT_JSON_DOESNT_CONTAIN_TRANSACTIONS_GROUP_ERR, TX_GROUP_REMOTE);
			}
			
			if (inputTxData.getLocalAccountTxsData() == null || inputTxData.getLocalAccountTxsData().isEmpty()) {
				logErrorThrowException(outputTxData, THE_INPUT_JSON_DOESNT_CONTAIN_TRANSACTIONS_GROUP_ERR, TX_GROUP_LOCAL);
			}
			
			logger.debug("Building map of remote transactions from the passed Json: {}", inputTxData.getRemoteAccountTxsData());
			remoteTransactionMap.putAll(groupTransactionsByDate(inputTxData.getRemoteAccountTxsData()));
			
			logger.debug("Building map of local transactions from the passed Json: {}", inputTxData.getLocalAccountTxsData());
			localTransactionMap.putAll(groupTransactionsByDate(inputTxData.getLocalAccountTxsData()));
			
		} else {
			outputTxData.getErrors().add(SOURCE_DATA_MISSING_OR_NOT_SUPPORTED_FORMAT_ERR);
			throw new TransactionsNotFoundAtSourceException(SOURCE_DATA_MISSING_OR_NOT_SUPPORTED_FORMAT_ERR, new IllegalArgumentException());
		}
	}

	private void logErrorThrowException(final OutputTxData outputTxData, final String errorTemplate, final String groupName) {
		final String errorText = String.format(errorTemplate, groupName);
		outputTxData.getErrors().add(errorText);
		throw new TransactionsNotFoundAtSourceException(errorText, new IllegalArgumentException());
	}

	/**
	 * Method creates the collection of {@link TxDataRow} and group them by tx row date.
	 * 
	 * @param transactions
	 * @return {@link HashMap} where key is the {@link TxDataRow} date and value is the transaction list for that date
	 */
	@Deprecated
	private Map<LocalDate, List<TxDataRow>> readTransactionsFromJson(final JSONArray transactions) {
		
		final List<TxDataRow> allRows = new ArrayList<>();
		
		transactions.forEach(row -> {
			final JSONObject jsonRow = (JSONObject) row;
			
			TxDataRow txDataRow = null;
			
			try {
				txDataRow = convertJsonToTxDataRow(jsonRow);
			} catch (final Exception e) {
				throw new JsonValuesParsingException("Failed to parse the JSON to TxDataRow \n"+jsonRow, e);
			}
			allRows.add(txDataRow);
		});
		
		final Map<LocalDate, List<TxDataRow>> grupedByTxDateMap = allRows.stream()
				.collect(Collectors.groupingBy(row -> row.getTxDate()));
		return grupedByTxDateMap;
	}
	
	/**
	 * Method creates the collection of {@link TxDataRow}s and groups them by tx row date.
	 * 
	 * @param transactions
	 * @return {@link HashMap} where key is the {@link TxDataRow} date and value is the transaction list for that date
	 */
	private Map<LocalDate, List<TxDataRow>> groupTransactionsByDate(final List<TxDataRow> transactions) {
		
		final Map<LocalDate, List<TxDataRow>> grupedByTxDateMap = transactions.stream()
				.collect(Collectors.groupingBy(row -> row.getTxDate()));
		
		return grupedByTxDateMap;
	}

	/**
	 * Read json object values and convert this object to {@link TxDataRow}
	 * 
	 * @param jsonRow
	 * @return TxDataRow {@link TxDataRow}
	 */
	@Deprecated
	private TxDataRow convertJsonToTxDataRow(final JSONObject jsonRow) {
		
		// Date can be String or Number
		final Object txDateObj = jsonRow.get(CommonUtils.TX_DATE_JSON_KEY);
		
		final String txDateString;
		
		if (txDateObj instanceof String) {
			txDateString = (String) txDateObj;
		} else {
			final ZonedDateTime dateTime = Instant.ofEpochMilli((long) txDateObj).atZone(ZoneId.of("Europe/Paris"));
			txDateString = dateTime.format(CommonUtils.DATE_TIME_FORMATTER);
		}
		
		final LocalDate txDate = LocalDate.parse(txDateString, CommonUtils.DATE_TIME_FORMATTER);
		
		final TxDataRow txDataRow = new TxDataRow();
		txDataRow.setTxDate(txDate);
		txDataRow.setAccountName(getStringFromJSONIgnoreCase(jsonRow, CommonUtils.TX_ACCOUNT_NAME_JSON_KEY));
		txDataRow.setDescription(getStringFromJSONIgnoreCase(jsonRow, CommonUtils.TX_DESCRIPTION_JSON_KEY));
		
		txDataRow.setMemo(hasTheKeyIgnoringCase(jsonRow, CommonUtils.TX_MEMO_JSON_KEY) ? getStringFromJSONIgnoreCase(jsonRow, CommonUtils.TX_MEMO_JSON_KEY) : null);
		txDataRow.setCategoryName(hasTheKeyIgnoringCase(jsonRow, CommonUtils.TX_CATEGORY_NAME_JSON_KEY) ? getStringFromJSONIgnoreCase(jsonRow, CommonUtils.TX_CATEGORY_NAME_JSON_KEY) : null);
		txDataRow.setTag(hasTheKeyIgnoringCase(jsonRow, CommonUtils.TX_TAG_JSON_KEY) ? getStringFromJSONIgnoreCase(jsonRow, CommonUtils.TX_TAG_JSON_KEY) : null);
		txDataRow.setReconsiled(hasTheKeyIgnoringCase(jsonRow, CommonUtils.TX_RECONCILED_JSON_KEY) ? getStringFromJSONIgnoreCase(jsonRow, CommonUtils.TX_RECONCILED_JSON_KEY) : null);
		
		final String amountString = getStringFromJSONIgnoreCase(jsonRow, CommonUtils.TX_AMOUNT_JSON_KEY);
		
		try {
			// the amount can be 2,150 so need to parse to BigDecimal manually
			final String amountStringNoCommas = amountString.replace(",", "").trim();
			txDataRow.setAmount(new BigDecimal(amountStringNoCommas));
		} catch (final NumberFormatException nfex) {
			logger.error("Failed while trying to parse amount String [\""+amountString+"\"]", nfex);
			txDataRow.setAmount(new BigDecimal(Long.MIN_VALUE));
		}
		
		return txDataRow;
	}
	
	private boolean hasTheKeyIgnoringCase(final JSONObject jobj, final String key) {

		final Iterator<String> iter = jobj.keySet().iterator();
		while (iter.hasNext()) {
			final String key1 = iter.next();
			if (key1.equalsIgnoreCase(key)) {
				return true;
			}
		}

		return false;
	}
	
	public String getStringFromJSONIgnoreCase(final JSONObject jobj, final String key) {

	    final Iterator<String> iter = jobj.keySet().iterator();
	    while (iter.hasNext()) {
	        final String key1 = iter.next();
	        if (key1.equalsIgnoreCase(key)) {
	            return (String) jobj.get(key1);
	        }
	    }

	    return null;
	}

}
