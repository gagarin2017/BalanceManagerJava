package com.greenland.balanceManager.java.app.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenland.balanceManager.java.app.external.domain.InputTxData;

public interface CommonIntTestUtils {
	

	/**
	 * @param scenarioNumber
	 * @param inputJson
	 * @param outputJson
	 * @param scenarioDesc
	 * @param extraVerifications 
	 * @return Object[] where each element is a parameter for the parameterised
	 *         test.
	 * @throws StreamReadException
	 * @throws DatabindException
	 * @throws IOException
	 */
	public static Object[] buildParamsForScenario(final String scenarioNumber, final String inputJson, final String outputJson, 
			final String scenarioDesc, final boolean extraVerifications) throws StreamReadException, DatabindException, IOException {
		
		final Object[] argList = new Object[5];

		final String inputTransactionsJSONFileName = String.format(inputJson, scenarioNumber);

		final ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules();

		// JSON file to Java object
		InputTxData inputData = InputTxData.builder().build();
		inputData = mapper.readValue(new File(inputTransactionsJSONFileName), InputTxData.class);
		
		argList[0] = Integer.valueOf(scenarioNumber);
		argList[1] = scenarioDesc;
		argList[2] = inputData;
		
		if (StringUtils.isNotEmpty(outputJson)) {
			final String expectedOutputJSONFileName = String.format(outputJson, scenarioNumber);
			final String expectedResultData = new String(Files.readAllBytes(Paths.get(expectedOutputJSONFileName)));
			
			argList[3] = expectedResultData;
			argList[4] = extraVerifications;
		}

		return argList;
	}

}
