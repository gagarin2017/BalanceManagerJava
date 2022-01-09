package com.greenland.balanceManager.java.app.services;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.greenland.balanceManager.java.app.external.BalanceManagerExternal;
import com.greenland.balanceManager.java.app.external.BalanceManagerExternalImpl;
import com.greenland.balanceManager.java.app.external.domain.InputTxData;
import com.greenland.balanceManager.java.app.external.domain.OutputTxData;

/**
 * Parameterised test meant to be used for a quick JSON run to debug issues/bugs found
 * 
 * @author Jura
 *
 */
public class TransactionComparatorServiceJsonBugsDebugger extends TestBase {

	private BalanceManagerExternal balanceManagerExternal = new BalanceManagerExternalImpl();
	
	private static final String TEST_DATA_DIR = "WIP_BUGS";

	private static final String DATA_JSON_FILE_NAME = "test_";

	private static final String FS = File.separator;
	
	private static final String REMOTE_DATA = TEST_DATA_DIR.concat(FS).concat(DATA_JSON_FILE_NAME).concat("%s").concat(".json");

	@BeforeEach
	public void setup() {
		injector.injectMembers(balanceManagerExternal);
	}

	@Test
	void testJson() throws StreamReadException, DatabindException, IOException {
		// Setup
		final Object[] scenarioDataArgList = CommonIntTestUtils.buildParamsForScenario("01", REMOTE_DATA, "", "Debug in progress", false);

		// Method under test
		final OutputTxData result = balanceManagerExternal.analyzeTransactions((InputTxData) scenarioDataArgList[2]);

		// Verifications
		System.out.println(String.format("Remote [%s]:\n %s", result.getRemoteTransactions().size(), result.getRemoteTransactions()));
		System.out.println(String.format("\nLocal [%s]:\n %s", result.getLocalTransactions().size(), result.getLocalTransactions()));
		System.out.println(String.format("\nMissing [%s]:\n %s", result.getMissingTransactions().size(), result.getMissingTransactions()));
	}

}
