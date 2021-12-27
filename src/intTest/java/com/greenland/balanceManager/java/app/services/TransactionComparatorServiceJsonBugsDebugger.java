//package com.greenland.balanceManager.java.app.services;
//
//import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.params.provider.Arguments.arguments;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.stream.Stream;
//
//import org.hamcrest.core.IsNull;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.MethodSource;
//
//import com.greenland.balanceManager.java.app.CommonUtils;
//import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
//
///**
// * Parameterised test meant to be used for a quick JSON run to debug issues/bugs found
// * 
// * @author Jura
// *
// */
//public class TransactionComparatorServiceJsonBugsDebugger extends TestBase {
//
//	private TransactionComparatorServiceImpl transactionComparatorService = new TransactionComparatorServiceImpl();
//	
//	private static final String TEST_DATA_DIR = "build\\resources\\intTest";
//
//	private static final String DATA_JSON_FILE_NAME = "TransactionComparatorService_sc";
//
//	private static final String FS = File.separator;
//	
//	private static final String REMOTE_DATA = TEST_DATA_DIR.concat(FS).concat(DATA_JSON_FILE_NAME).concat("%s").concat(".json");
//
//	private final static JSONParser parser = new JSONParser();
//	
//	@BeforeEach
//	public void setup() {
//		injector.injectMembers(transactionComparatorService);
//	}
//
//	@ParameterizedTest(name = "#{index} - Test scenario : {2}")
//	@MethodSource("buildScenariosData")
//	void testCompareTransactions(final int scenarioNo, final JSONObject remoteTransactionsJsonObject,
//			final String scenarioDesc) throws TransactionsNotFoundAtSourceException {
//
//		// Setup
//		JSONObject resultJsonObject = new JSONObject();
//
//		// Method under test
//		resultJsonObject = transactionComparatorService.executeTransactionComparison(remoteTransactionsJsonObject);
//
//		// Common validations
//		assertNotNull(resultJsonObject);
//		final JSONArray remoteDaysWithTransactions = resultJsonObject.getJSONArray(CommonUtils.REMOTE_TRANSACTIONS_JSON_KEY);
//		final JSONArray localDaysWithTransactions = resultJsonObject.getJSONArray(CommonUtils.LOCAL_TRANSACTIONS_JSON_KEY);
//
//		assertThat("There is a collection of remote transactions", remoteDaysWithTransactions, is(IsNull.notNullValue()));
//		assertThat("There is a collection of local transactions", localDaysWithTransactions, is(IsNull.notNullValue()));
//	}
//	
//    /**
//     * @return
//     * @throws ParseException 
//     * @throws IOException 
//     * @throws FileNotFoundException 
//     */
//    static Stream<Arguments> buildScenariosData() throws FileNotFoundException, IOException, ParseException {
//    	
//    	final String scenario1Desc5 = "Real financial data. DO NOT COMMIT!.";
//    	
//    	final Object[] scenarioDataArgList05 = buildMapsForScenario("05",scenario1Desc5);
//    	
//        return Stream.of(
//		        arguments( 
////		        		scenarioDataArgList05[0],
////		        		scenarioDataArgList05[1],
////		        		scenarioDataArgList05[2]
//		        		)
//		        );
//    }
//
//    /**
//	 * @param scenarioNumber
//	 * @param scenarioDesc
//	 * 
//	 * @return Object[] where each element is a parameter for the parameterised test.
//	 */
//	private static Object[] buildMapsForScenario(final String scenarioNumber, final String scenarioDesc) {
//		final Object[] argList = new Object[3];
//		
//		final String remoteTxsFile = String.format(REMOTE_DATA, scenarioNumber);
//
//		JSONObject txJson = new JSONObject();
//		
//		try {
//			final org.json.simple.JSONObject simpleJSONObject = (org.json.simple.JSONObject)parser.parse(new FileReader(remoteTxsFile));
//			txJson = new JSONObject(simpleJSONObject.toJSONString());
//			
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		
//		argList[0] = Integer.valueOf(scenarioNumber);
//		argList[1] = txJson;
//		argList[2] = scenarioDesc;
//		return argList;
//	}
//
//}
