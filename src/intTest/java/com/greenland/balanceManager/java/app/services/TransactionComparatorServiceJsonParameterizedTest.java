package com.greenland.balanceManager.java.app.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.stream.Stream;

import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenland.balanceManager.java.app.CommonUtils;
import com.greenland.balanceManager.java.app.external.BalanceManagerExternal;
import com.greenland.balanceManager.java.app.external.BalanceManagerExternalImpl;
import com.greenland.balanceManager.java.app.external.domain.DailyTransactions;
import com.greenland.balanceManager.java.app.external.domain.InputTxData;
import com.greenland.balanceManager.java.app.external.domain.OutputTxData;

/**
 * @author Jura
 *
 */
public class TransactionComparatorServiceJsonParameterizedTest extends TestBase {

	private static final String TEST_DATA_DIR = "build\\resources\\intTest";
	private static final String DATA_JSON_FILE_NAME = "TransactionComparatorService_sc";
	private static final String RESULT_JSON_FILE_SUFFIX = "_expected";
	private static final String INPUT_JSON = TEST_DATA_DIR.concat(File.separator).concat(DATA_JSON_FILE_NAME)
			.concat("%s").concat(".json");
	private static final String OUTPUT_JSON = TEST_DATA_DIR.concat(File.separator).concat(DATA_JSON_FILE_NAME)
			.concat("%s").concat(RESULT_JSON_FILE_SUFFIX).concat(".json");

	private BalanceManagerExternal balanceManagerExternal = new BalanceManagerExternalImpl();
	
	final static ObjectMapper objectMapper;
	
	static {
		objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();
	}
	
	@BeforeEach
	public void setup() {
		injector.injectMembers(balanceManagerExternal);
	}

	/**
	 * @ParameterizedTest
	 * 
	 * @param scenarioNo
	 * @param scenarioDesc
	 * @param inputTransactionsJsonObject
	 * @param expectedResultJSONString
	 * @param extraVerifications
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@DisplayName("analyzeTransactions should:")
	@ParameterizedTest(name = "#{0} - {1}")
	@MethodSource("buildScenariosData")
	void testAnalyzeTransactions(final int scenarioNo, final String scenarioDesc,
			final InputTxData inputTransactionsJsonObject, final String expectedResultJSONString, final boolean extraVerifications) 
					throws JsonMappingException, JsonProcessingException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

		// Method under test
		final OutputTxData result = balanceManagerExternal.analyzeTransactions(inputTransactionsJsonObject);

		// Verifications
		runCommonValidations(result, expectedResultJSONString);
		runExtraVerifications(scenarioNo, result, extraVerifications);
	}

	/**
	 * IF extra verifications needed, - then run them
	 * 
	 * @param scenarioNo
	 * @param result
	 * @param extraVerifications
	 * 
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private void runExtraVerifications(final int scenarioNo, final OutputTxData result, final boolean extraVerifications)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		
		if (extraVerifications) {
			final Method verifyMethod = TransactionComparatorServiceJsonParameterizedTest.class
					.getDeclaredMethod("verifyScenario" + scenarioNo, OutputTxData.class);
			verifyMethod.invoke(this, result);
		}
	}

	/**
	 * Compare two JSONs actual vs expected
	 * 
	 * @param actual
	 * @param expected
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	private void runCommonValidations(final OutputTxData actual, final String expected)
			throws JsonProcessingException, JsonMappingException {
		
		assertNotNull(actual);
		assertNotNull(expected);

		String resultAsString = null;

		try {
			resultAsString = objectMapper.writeValueAsString(actual);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		System.out.println("Result \n"+resultAsString);
		
		assertEquals(objectMapper.readTree(resultAsString), objectMapper.readTree(expected));
	}

	/**
	 * @return 
	 * 		the Stream of arguments to be executed by test method
	 * @throws DatabindException 
	 * @throws StreamReadException 
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	static Stream<Arguments> buildScenariosData() throws StreamReadException, DatabindException, IOException {

		final String scenario1Desc1 = "Return no missing transactions when Remote and Local have 2 days with matching transactions.";
		final String scenario1Desc2 = "Return 1 missing transaction when Remote has 4 transactions for 1 day and Local has 3 transactions fot the same day.";

		final Object[] scenarioDataArgList01 = CommonIntTestUtils.buildParamsForScenario("01", INPUT_JSON, OUTPUT_JSON, scenario1Desc1, false);
		final Object[] scenarioDataArgList02 = CommonIntTestUtils.buildParamsForScenario("02", INPUT_JSON, OUTPUT_JSON, scenario1Desc2, true);

		return Stream.of(
				arguments(scenarioDataArgList01[0], 
						scenarioDataArgList01[1], 
						scenarioDataArgList01[2],
						scenarioDataArgList01[3],
						scenarioDataArgList01[4]),
				arguments(scenarioDataArgList02[0], 
						scenarioDataArgList02[1],
						scenarioDataArgList02[2],
						scenarioDataArgList02[3],
						scenarioDataArgList02[4])
		);
	}
	
	@SuppressWarnings("unused")
	private boolean verifyScenario2(final OutputTxData outputTxData) {
		assertThat("only local transactions missing", outputTxData.getMissingTransactions().size(), is(1));
		assertThat("only 1 day missing", outputTxData.getMissingTransactions().get(CommonUtils.LOCAL_TRANSACTIONS_JSON_KEY).size(), is(1));
		assertThat("number of days for the group match", outputTxData.getMissingTransactions().get(CommonUtils.LOCAL_TRANSACTIONS_JSON_KEY).size(), is(1));
		
		final DailyTransactions actualDateTransactions = outputTxData.getMissingTransactions().get(CommonUtils.LOCAL_TRANSACTIONS_JSON_KEY).get(0);
		
		assertThat("date of missing transactions match", actualDateTransactions.getDate(), is(LocalDate.of(2020, 5, 13)));
		assertThat("number of transactions for the date match", actualDateTransactions.getTransactions().size(), is(1));
		return true;
	}

}
