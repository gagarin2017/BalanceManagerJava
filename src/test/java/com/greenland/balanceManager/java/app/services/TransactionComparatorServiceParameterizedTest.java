package com.greenland.balanceManager.java.app.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.greenland.balanceManager.java.app.exceptions.TransactionListsSizeIncorrectException;
import com.greenland.balanceManager.java.app.model.TxDataRow;

import mockit.Expectations;
import mockit.Tested;

public class TransactionComparatorServiceParameterizedTest {
	
	private static final String SCENARIO_DESC = "Remote transactions [%d days, %d transaction(s)], Local transactions [%d days, %d transaction(s)]";

	@Tested
	private TransactionComparatorServiceImpl transactionComparatorService;
    
    /**
     * @param remoteTransactionMap
     * @param localTransactionMap
     * @param scenarioNo
     * @param scenarioDesc
     */
    @ParameterizedTest(name = "#{index} - Test scenario : {3}")
    @MethodSource("buildScenariosData")
    void testCompareTransactions(final Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
    		final Map<LocalDate, List<TxDataRow>> localTransactionMap, final int scenarioNo, final String scenarioDesc ) {
    	
    	switch(scenarioNo) {
	    	case 1: {
	        	// Method under test + Verifications
	    		assertThat("Remote tx size (days) correct", remoteTransactionMap.size(), is(3));
	    		assertThat("Local tx size (days) correct", localTransactionMap.size(), is(2));
	    		assertThrows(TransactionListsSizeIncorrectException.class, 
	        			() -> transactionComparatorService.compareTransactions(remoteTransactionMap, localTransactionMap));
	    		break;
	    	}
	    	case 2: {
	    		// Method under test + Verifications
	    		assertThat("Remote tx size (days) correct", remoteTransactionMap.size(), is(4));
	    		assertThat("Local tx size (days) correct", localTransactionMap.size(), is(7));
	    		assertThrows(TransactionListsSizeIncorrectException.class, 
	    				() -> transactionComparatorService.compareTransactions(remoteTransactionMap, localTransactionMap));
	    		break;
	    	}
	    	case 3: {
	    		// Method under test + Verifications
	    		assertThat("Remote tx size (days) correct", remoteTransactionMap.size(), is(4));
	    		assertThat("Local tx size (days) correct", localTransactionMap.size(), is(7));
	    		
	    		// expecting these to be removed
	    		localTransactionMap.remove(LocalDate.of(2017, 12, 4));
	    		localTransactionMap.remove(LocalDate.of(2018, 1, 1));
	    		localTransactionMap.remove(LocalDate.of(2018, 1, 4));
	    		
	    		new Expectations(transactionComparatorService) {
	    			{
	    				transactionComparatorService.compareTransactionListSizesPerDay(remoteTransactionMap, localTransactionMap);
	    				result = true;
	    				times = 1;
	    			}
	    		};
	    		
	    		transactionComparatorService.compareTransactions(remoteTransactionMap, localTransactionMap);
	    		break;
	    	}
	    	case 4: {
	    		// Method under test + Verifications
	    		assertThat("Remote tx size (days) correct", remoteTransactionMap.size(), is(4));
	    		assertThat("Local tx size (days) correct", localTransactionMap.size(), is(6));
	    		
	    		// expecting these to be removed
	        	localTransactionMap.remove(LocalDate.of(2018, 1, 8));
	        	localTransactionMap.remove(LocalDate.of(2018, 1, 4));
	        	
	    		new Expectations(transactionComparatorService) {
	    			{
	    				transactionComparatorService.compareTransactionListSizesPerDay(remoteTransactionMap, localTransactionMap);
	    				result = true;
	    				times = 1;
	    			}
	    		};
	    		
	    		transactionComparatorService.compareTransactions(remoteTransactionMap, localTransactionMap);
	    		break;
	    	}
    	}
    }

    /**
     * @return
     */
    static Stream<Arguments> buildScenariosData() {
    	final Object[] scenario1 = buildMapsForScenario1();
    	final Object[] scenario2 = buildMapsForScenario2();
    	final Object[] scenario3 = buildMapsForScenario3();
    	final Object[] scenario4 = buildMapsForScenario4();
    	
        return Stream.of(
                arguments(scenario1[0], scenario1[1], 1, scenario1[2]),
                arguments(scenario2[0], scenario2[1], 2, scenario2[2]),
                arguments(scenario3[0], scenario3[1], 3, scenario3[2]),
                arguments(scenario4[0], scenario4[1], 4, scenario4[2])
        );
    }


    /**
     * @return
     */
    private static Object[] buildMapsForScenario4() {
    	final Object[] result = new Object[3];
    	result[2] = String.format(SCENARIO_DESC, 4, 3, 7, 1); 
    	
    	final Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
    	final Map<LocalDate,List<TxDataRow>> localTransactionMap = new HashMap<>();
    	
    	final TxDataRow remoteTx = new TxDataRow();
    	remoteTx.setAccountName("Remote Acc name");
    	
    	final List<TxDataRow> remoteTxList1 = new ArrayList<>();
    	remoteTxList1.add(remoteTx);
    	
    	remoteTransactionMap.put(LocalDate.of(2017, 12, 8), remoteTxList1);
    	remoteTransactionMap.put(LocalDate.of(2018, 1, 10), remoteTxList1);
    	remoteTransactionMap.put(LocalDate.of(2018, 1, 1), remoteTxList1);
    	remoteTransactionMap.put(LocalDate.of(2018, 1, 15), remoteTxList1);
    	
    	result[0] = remoteTransactionMap;
    	
    	final TxDataRow localTx = new TxDataRow();
    	localTx.setAccountName("Local Acc name");
    	
    	final List<TxDataRow> localTxList1 = new ArrayList<>();
    	localTxList1.add(localTx);
    	
    	localTransactionMap.put(LocalDate.of(2017, 12, 8), localTxList1);
    	localTransactionMap.put(LocalDate.of(2018, 1, 8), localTxList1);
    	localTransactionMap.put(LocalDate.of(2018, 1, 10), localTxList1);
    	localTransactionMap.put(LocalDate.of(2018, 1, 10), localTxList1);
    	localTransactionMap.put(LocalDate.of(2018, 1, 15), localTxList1);
    	localTransactionMap.put(LocalDate.of(2018, 1, 4), localTxList1);
    	localTransactionMap.put(LocalDate.of(2018, 1, 1), localTxList1);
    	
    	result[1] = localTransactionMap;
    	
    	return result;
    }
    
	/**
	 * @return
	 */
	private static Object[] buildMapsForScenario3() {
		final Object[] result = new Object[3];
		result[2] = String.format(SCENARIO_DESC, 4, 3, 7, 1); 
		
    	final Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
    	final Map<LocalDate,List<TxDataRow>> localTransactionMap = new HashMap<>();
		
		final TxDataRow remoteTx = new TxDataRow();
		remoteTx.setAccountName("Remote Acc name");

		final List<TxDataRow> remoteTxList1 = new ArrayList<>();
		final List<TxDataRow> remoteTxList2 = new ArrayList<>();
		remoteTxList1.add(remoteTx);
		remoteTxList2.add(remoteTx);
		remoteTxList2.add(remoteTx);
		
		remoteTransactionMap.put(LocalDate.of(2018, 1, 8), remoteTxList1);
		remoteTransactionMap.put(LocalDate.of(2018, 1, 10), remoteTxList2);
		remoteTransactionMap.put(LocalDate.of(2018, 1, 11), remoteTxList1);
		remoteTransactionMap.put(LocalDate.of(2018, 1, 15), remoteTxList2);
		
		result[0] = remoteTransactionMap;
		
		final TxDataRow localTx = new TxDataRow();
		localTx.setAccountName("Local Acc name");

		final List<TxDataRow> localTxList1 = new ArrayList<>();
		localTxList1.add(localTx);
		
		localTransactionMap.put(LocalDate.of(2017, 12, 4), localTxList1);
		localTransactionMap.put(LocalDate.of(2018, 1, 8), localTxList1);
		localTransactionMap.put(LocalDate.of(2018, 1, 10), localTxList1);
		localTransactionMap.put(LocalDate.of(2018, 1, 11), localTxList1);
		localTransactionMap.put(LocalDate.of(2018, 1, 15), localTxList1);
		localTransactionMap.put(LocalDate.of(2018, 1, 4), localTxList1);
		localTransactionMap.put(LocalDate.of(2018, 1, 1), localTxList1);
		
		result[1] = localTransactionMap;

		return result;
	}
    
	/**
	 * @return
	 */
	private static Object[] buildMapsForScenario2() {
		final Object[] result = new Object[3];
		result[2] = String.format(SCENARIO_DESC, 4, 3, 7, 1); 
		
    	final Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
    	final Map<LocalDate,List<TxDataRow>> localTransactionMap = new HashMap<>();
		
		final TxDataRow remoteTx = new TxDataRow();
		remoteTx.setAccountName("Remote Acc name");

		final List<TxDataRow> remoteTxList1 = new ArrayList<>();
		final List<TxDataRow> remoteTxList2 = new ArrayList<>();
		remoteTxList1.add(remoteTx);
		remoteTxList2.add(remoteTx);
		remoteTxList2.add(remoteTx);
		
		remoteTransactionMap.put(LocalDate.of(2018, 1, 8), remoteTxList1);
		remoteTransactionMap.put(LocalDate.of(2018, 1, 10), remoteTxList1);
		remoteTransactionMap.put(LocalDate.of(2018, 1, 11), remoteTxList1);
		remoteTransactionMap.put(LocalDate.of(2018, 1, 15), remoteTxList2);
		
		result[0] = remoteTransactionMap;
		
		final TxDataRow localTx = new TxDataRow();
		localTx.setAccountName("Local Acc name");

		final List<TxDataRow> localTxList1 = new ArrayList<>();
		localTxList1.add(localTx);
		
		localTransactionMap.put(LocalDate.of(2018, 1, 4), localTxList1);
		localTransactionMap.put(LocalDate.of(2018, 1, 7), localTxList1);
		localTransactionMap.put(LocalDate.of(2018, 1, 8), localTxList1);
		localTransactionMap.put(LocalDate.of(2018, 1, 12), localTxList1);
		localTransactionMap.put(LocalDate.of(2018, 1, 13), localTxList1);
		localTransactionMap.put(LocalDate.of(2018, 1, 14), localTxList1);
		localTransactionMap.put(LocalDate.of(2018, 1, 10), localTxList1);
		
		result[1] = localTransactionMap;

		return result;
	}

	/**
	 * @return
	 */
	private static Object[] buildMapsForScenario1() {
		final Object[] result = new Object[3];
		
		result[2] = String.format(SCENARIO_DESC, 3, 3, 2, 1); 
    	final Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
    	final Map<LocalDate,List<TxDataRow>> localTransactionMap = new HashMap<>();
		final TxDataRow remoteTx = new TxDataRow();

		final List<TxDataRow> remoteTxList1 = new ArrayList<>();
		final List<TxDataRow> remoteTxList2 = new ArrayList<>();
		final List<TxDataRow> remoteTxList3 = new ArrayList<>();
		remoteTxList1.add(remoteTx);
		remoteTxList2.add(remoteTx);
		remoteTxList2.add(remoteTx);
		remoteTxList3.add(remoteTx);
		
		remoteTransactionMap.put(LocalDate.of(2018, 1, 8), remoteTxList1);
		remoteTransactionMap.put(LocalDate.of(2018, 1, 10), remoteTxList2);
		remoteTransactionMap.put(LocalDate.of(2018, 1, 17), remoteTxList3);
		
		result[0] = remoteTransactionMap;
		
		final TxDataRow localTx = new TxDataRow();
		final List<TxDataRow> localTxList1 = new ArrayList<>();
		localTxList1.add(localTx);
		
		localTransactionMap.put(LocalDate.of(2018, 1, 8), localTxList1);
		localTransactionMap.put(LocalDate.of(2018, 1, 10), localTxList1);
		
		result[1] = localTransactionMap;

		return result;
	}
}
