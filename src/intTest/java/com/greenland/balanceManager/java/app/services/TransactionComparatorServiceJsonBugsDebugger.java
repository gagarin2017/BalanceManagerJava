package com.greenland.balanceManager.java.app.services;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;

import org.hamcrest.core.IsNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.greenland.balanceManager.java.app.CommonUtils;
import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
import com.greenland.balanceManager.java.app.external.domain.InputTxData;
import com.greenland.balanceManager.java.app.external.domain.OutputTxData;

/**
 * Parameterised test meant to be used for a quick JSON run to debug issues/bugs found
 * 
 * @author Jura
 *
 */
public class TransactionComparatorServiceJsonBugsDebugger extends TestBase {

	private TransactionComparatorServiceImpl transactionComparatorService = new TransactionComparatorServiceImpl();
	
	private static final String TEST_DATA_DIR = "WIP_BUGS";

	private static final String DATA_JSON_FILE_NAME = "test_";

	private static final String FS = File.separator;
	
	private static final String REMOTE_DATA = TEST_DATA_DIR.concat(FS).concat(DATA_JSON_FILE_NAME).concat("%s").concat(".json");

	private final static JSONParser parser = new JSONParser();
	
	@BeforeEach
	public void setup() {
		injector.injectMembers(transactionComparatorService);
	}

	@Test
	void testJson() throws StreamReadException, DatabindException, IOException {
		// Setup
		final Object[] scenarioDataArgList = CommonIntTestUtils.buildParamsForScenario("01", REMOTE_DATA, "", "Debug in progress", false);

		// Method under test
		final OutputTxData result = transactionComparatorService.analyzeTransactions((InputTxData) scenarioDataArgList[2]);

		// Verifications
		System.out.println();
	}

}
