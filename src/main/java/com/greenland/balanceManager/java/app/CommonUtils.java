package com.greenland.balanceManager.java.app;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.greenland.balanceManager.java.app.external.domain.InputTxData;

public interface CommonUtils {
	
	public static final String REMOTE_TX_REGEX = "(,)(?=(?:[^\"]|\"[^\"]*\")*$)";
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	// The default amount to start balance calculations with. Its used if nothing got passed through
	public static final BigDecimal START_AMOUNT = new BigDecimal("77.94");
	
	public static final String TX_RULE = "txs_rule";
	
	//JSON keys
	
	// JSON keys for the input JSON
	public static final String INPUT_REMOTE_TRANSACTIONS_JSON_KEY = "remoteAccountTxsData";
	public static final String INPUT_LOCAL_TRANSACTIONS_JSON_KEY = "localAccountTxsData";
	
	// JSON keys for the output JSON
	public static final String LOCAL_TRANSACTIONS_JSON_KEY = "localTransactions";
	public static final String REMOTE_TRANSACTIONS_JSON_KEY = "remoteTransactions";
	public static final String STARTING_BALANCE_JSON_KEY = "startingBalance";
	public static final String MISSING_TRANSACTIONS_JSON_KEY = "missingTransactions";
	public static final String MISSING_TRANSACTIONS_BALANCES_JSON_KEY = "missingTransactionsBalances";

	public static final String TX_DATE_JSON_KEY = "date";
	public static final String TX_ACCOUNT_NAME_JSON_KEY = "account";
	public static final String TX_DESCRIPTION_JSON_KEY = "description";
	public static final String TX_MEMO_JSON_KEY = "memo";
	public static final String TX_CATEGORY_NAME_JSON_KEY = "category";
	public static final String TX_TAG_JSON_KEY = "Tag";
	public static final String TX_RECONCILED_JSON_KEY = "clr";
	public static final String TX_AMOUNT_JSON_KEY = "amount";

	public static final String TX_LIST_PER_DAY_JSON_KEY = "transactionsPerDay";
	public static final String TOTAL_AMOUNT_PER_DAY_JSON_KEY = "totalAmmountPerDay";
	
	// Missing transactions keys
	public static final String MISSED_TRANSACTIONS_ON_THE_DATE_JSON_KEY = "transactionsOnThisDate";
	public static final String MISSED_TRANSACTIONS_DATE_JSON_KEY = "missedTransactionsDate";
	public static final String MISSING_GROUP_TRANSACTIONS_JSON_KEY = "transactions";
	public static final String MISSING_TXS_GROUP_NAME_JSON_KEY = "groupName";
	
	/**
	 * @param transactionsJson
	 * @return true if there are no {@link JSONArray} transactions with the given key
	 */
	@Deprecated
	public static boolean transacationsMissing(final JSONObject transactionsJson, final String transactionsJsonKey) {
		
		final boolean isMissing;
		
		final boolean txListMissing = !transactionsJson.has(transactionsJsonKey);
		
		if (txListMissing) {
			isMissing = true;
		} else {
			final JSONArray txJsonArray = transactionsJson.getJSONArray(transactionsJsonKey);
			isMissing = (txJsonArray != null && txJsonArray.length() > 0) ? false : true;
		}
		
		return isMissing;
	}

}
