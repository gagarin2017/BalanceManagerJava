package com.greenland.balanceManager.java.app.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.greenland.balanceManager.java.app.dao.TransactionsJsonReader;
import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;

/**
 * @author Jura
 *
 */
public class TransactionComparatorServiceJsonParameterizedTest extends TestBase {

	private static final String SCENARIO_DESC = "Remote transactions [%s], Local transactions [%s]. Starting balance: %s.";

	private TransactionComparatorServiceImpl transactionComparatorService = new TransactionComparatorServiceImpl();
	
	private static final String TEST_DATA_DIR = "build\\resources\\intTest";
	private static final String DATA_REMOTE_TXS_FILE_NAME = "TransactionComparatorService_remote_txs_";
	private static final String DATA_LOCAL_TXS_FILE_NAME = "TransactionComparatorService_local_txs_";
	private static final String FS = File.separator;
	
	private static final String REMOTE_DATA = TEST_DATA_DIR.concat(FS).concat("%s").concat("%s").concat(".json");
	
	private final static JSONParser parser = new JSONParser();
	
	@BeforeEach
	public void setup() {
		injector.injectMembers(transactionComparatorService);
	}

	// Test Data
	private static final String ACC_NAME_1 = "Bank acc";
	private static final String DESCRIPTION_1 = "description";
	private static final String MEMO_1 = "memo";
	private static final String CATEGORY_NAME_1 = "Category";
	private static final String TAG_1 = "tag";
	private static final String RECONCILE_STRING_1 = "R";
	private static final String TX_AMOUNT_1 = "-15.11";

	@ParameterizedTest(name = "#{index} - Test scenario : {4}")
	@MethodSource("buildScenariosData")
	void testCompareTransactions(final JSONObject remoteTransactionsJsonObject, final JSONObject localTransactionsJsonObject, 
			final BigDecimal startingBalance, final int scenarioNo, final String scenarioDesc) throws TransactionsNotFoundAtSourceException {

		switch (scenarioNo) {
		case 1:
			// Method under test + Verifications
//			assertThat("Remote tx size (days) correct", remoteTransactionMap.size(), is(3));
//			assertThat("Local tx size (days) correct", localTransactionMap.size(), is(2));
//
//			new Expectations() {
//				{
//					transactionsSizeComparator.compareTransactionListSizes(remoteTransactionMap, localTransactionMap);
//					times = 1;
//
//					transactionsBalanceAnalyzer.analyzeTransactionBalances(remoteTransactionMap, localTransactionMap,
//							startingBalance);
//					times = 1;
//				}
//			};

			// Method under test
			final JSONObject resultJsonObject_01 = transactionComparatorService.executeTransactionComparison(remoteTransactionsJsonObject, localTransactionsJsonObject, startingBalance);
			assertNotNull(resultJsonObject_01);
			break;
		case 2:
			final JSONObject resultJsonObject_02 = transactionComparatorService.executeTransactionComparison(remoteTransactionsJsonObject, localTransactionsJsonObject, startingBalance);
			assertNotNull(resultJsonObject_02);
			break;			
		}
	}
	
    /**
     * @return
     * @throws ParseException 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    static Stream<Arguments> buildScenariosData() throws FileNotFoundException, IOException, ParseException {
    	final Object[] scenarioDataArgList01 = buildMapsForScenario1();
    	final Object[] scenarioDataArgList02 = buildMapsForScenario2();
    	
        return Stream.of(
                arguments(scenarioDataArgList01[0], scenarioDataArgList01[1], scenarioDataArgList01[2], 1, scenarioDataArgList01[3]),
                arguments(scenarioDataArgList02[0], scenarioDataArgList02[1], scenarioDataArgList02[2], 2, scenarioDataArgList02[3])
                );
    }

	private static Object[] buildMapsForScenario2() throws FileNotFoundException, IOException, ParseException {
		final Object[] argList = new Object[4];
		
		final String remoteTxsFile = String.format(REMOTE_DATA, DATA_REMOTE_TXS_FILE_NAME, "01");
		final String localTxsFile = String.format(REMOTE_DATA, DATA_LOCAL_TXS_FILE_NAME, "01");
        
		final org.json.simple.JSONObject remoteTxSimpleObj = (org.json.simple.JSONObject)parser.parse(new FileReader(remoteTxsFile));
		final org.json.simple.JSONObject localTxsSimpleObj = (org.json.simple.JSONObject)parser.parse(new FileReader(localTxsFile));
		
		final JSONObject remoteTxsJson = new JSONObject(remoteTxSimpleObj.toJSONString());
		final JSONObject localTxsJson = new JSONObject(localTxsSimpleObj.toJSONString());
		
//        final JSONArray remoteTxList = (JSONArray)remoteTxsObj.get(TransactionsJsonReader.REMOTE_TRANSACTIONS_JSON_KEY);
//        final Iterator iterator = remoteTxList.iterator();
//        while (iterator.hasNext()) {
//        
//           System.out.println(iterator.next());
//        }
		
		argList[0] = remoteTxsJson;
		argList[1] = localTxsJson;
		argList[2] = new BigDecimal("18.11");
		argList[3] = String.format(SCENARIO_DESC, remoteTxsJson.toMap().values().size(), localTxsJson.toMap().values().size(), NumberFormat.getCurrencyInstance().format(new BigDecimal("18.11")));
		return argList;
	}
    
	/**
	 * @return Object[] where each element is a parameter for the parameterised test.
	 */
	private static Object[] buildMapsForScenario1() {
		final Object[] argList = new Object[4];
		
		final BigDecimal startingBalance = new BigDecimal("148.23");
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
		
		argList[0] = remoteTxsJson;
		argList[1] = localTxsJson;
		argList[2] = startingBalance;
		argList[3] = String.format(SCENARIO_DESC, remoteTxsJson.toMap().values().size(), localTxsJson.toMap().values().size(), NumberFormat.getCurrencyInstance().format(startingBalance));
		
		return argList;
	}

}
