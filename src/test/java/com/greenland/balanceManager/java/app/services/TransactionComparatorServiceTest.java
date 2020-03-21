package com.greenland.balanceManager.java.app.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.greenland.balanceManager.java.app.dao.TransactionsSourceDao;
import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundException;
import com.greenland.balanceManager.java.app.model.TxDataRow;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;


/**
 * @author Jura
 *
 */
public class TransactionComparatorServiceTest {
	
	@Tested
	private TransactionComparatorServiceImpl transactionComparatorService;
	
	@Injectable
	private TransactionsReaderService transactionsReaderService;
	
	@Injectable
	private TransactionsBalanceAnalyzer transactionsBalanceAnalyzer;
	
	@Injectable
	private TransactionsSourceDao transactionsSourceDao;
	
	@Injectable
	private TransactionsSizeComparator transactionsSizeComparator;
	
	private static final Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
	private static final Map<LocalDate, List<TxDataRow>> localTransactionMap = new HashMap<>();
	
	@AfterEach
	private void clearMaps() {
		remoteTransactionMap.clear();
		localTransactionMap.clear();
	}
	
    @Test
    public void executeTransactionComparison_tx_not_found_exception() throws FileNotFoundException {
    	// Setup
    	new Expectations() {
    		{
    			transactionComparatorService.getRemoteTransactionMap();
    			result = remoteTransactionMap;
    			
    			transactionComparatorService.getLocalTransactionMap();
    			result = localTransactionMap;
    			
    			transactionsReaderService.populateTxMapsFromSource(remoteTransactionMap, localTransactionMap, withInstanceOf(TransactionsSourceDao.class));
    			times = 1;
    		}
    	};
    	
        Exception exception = assertThrows(TransactionsNotFoundException.class, 
			() -> transactionComparatorService.executeTransactionComparison());

        final String expectedErrorTxt = String.format(TransactionComparatorServiceImpl.TX_NOT_FOUND_ERROR, 0, 0);
        assertEquals(expectedErrorTxt, exception.getMessage());
    }
    
    @Test
    public void executeTransactionComparison_remote_tx_found_local_not_found_exception() throws FileNotFoundException {
		// Setup
    	final TxDataRow txDataRow = new TxDataRow();
    	final List<TxDataRow> txList = Arrays.asList(txDataRow);
    	final LocalDate todayDate = LocalDate.now();
    	
    	remoteTransactionMap.put(todayDate, txList);
    	
    	new Expectations(transactionComparatorService) {
    		{
    			transactionComparatorService.getRemoteTransactionMap();
    			result = remoteTransactionMap;

    			transactionComparatorService.getLocalTransactionMap();
    			result = localTransactionMap;

    			transactionsReaderService.populateTxMapsFromSource(remoteTransactionMap, localTransactionMap, withInstanceOf(TransactionsSourceDao.class));
    			times = 1;
    		}
    	};
    	
    	// Method under test
    	Exception exception = assertThrows(TransactionsNotFoundException.class, 
    			() -> transactionComparatorService.executeTransactionComparison());
    	
    	final String expectedErrorTxt = String.format(TransactionComparatorServiceImpl.TX_NOT_FOUND_ERROR, 0, 0);
    	assertEquals(expectedErrorTxt, exception.getMessage());
    }
    
    @Test
    public void executeTransactionComparison_remote_tx_not_found_local_found_exception() throws FileNotFoundException {
    	// Setup
    	new Expectations(transactionComparatorService) {
    		{
    			transactionComparatorService.getRemoteTransactionMap();
    			result = remoteTransactionMap;

    			transactionComparatorService.getLocalTransactionMap();
    			result = localTransactionMap;
    			
    			transactionsReaderService.populateTxMapsFromSource(remoteTransactionMap, localTransactionMap, withInstanceOf(TransactionsSourceDao.class));
    			times = 1;
    			
    			transactionComparatorService.getRemoteTransactionMap();
    			result = remoteTransactionMap;
    		}
    	};
    	
    	// Method under test
    	Exception exception = assertThrows(TransactionsNotFoundException.class, 
    			() -> transactionComparatorService.executeTransactionComparison());
    	
    	final String expectedErrorTxt = String.format(TransactionComparatorServiceImpl.TX_NOT_FOUND_ERROR, 0, 0);
    	assertEquals(expectedErrorTxt, exception.getMessage());
    }
    
    @Test
    public void executeTransactionComparison_tx_found_compareTransactions() throws FileNotFoundException {
    	// Setup
    	final TxDataRow txDataRow = new TxDataRow();
    	final List<TxDataRow> txList = Arrays.asList(txDataRow);
    	final LocalDate todayDate = LocalDate.now();
    	
    	remoteTransactionMap.put(todayDate, txList);
    	localTransactionMap.put(todayDate, txList);
    	
    	new Expectations(transactionComparatorService) {
    		{
    			transactionsReaderService.populateTxMapsFromSource(remoteTransactionMap, localTransactionMap, withInstanceOf(TransactionsSourceDao.class));
    			times = 1;
    			
    			transactionComparatorService.getRemoteTransactionMap();
    			result = remoteTransactionMap;
    			
    			transactionComparatorService.getLocalTransactionMap();
    			result = localTransactionMap;
    			
    			transactionComparatorService.compareRemoteVsLocalTransactions(remoteTransactionMap, localTransactionMap);
    			times = 1;
    		}
    	};
    	
    	// Method under test
    	transactionComparatorService.executeTransactionComparison();
    }
    
    @Test
    @DisplayName("compareTransactions remote and local tx lists are populated with the same single Transaction.")
    public void compareTransactions_remote_local_have_singleSameTransaction() {
    	// Setup
    	final TxDataRow txDataRow = new TxDataRow();
    	final List<TxDataRow> txList = Arrays.asList(txDataRow);
    	final LocalDate todayDate = LocalDate.now();
    	
    	final TreeMap<LocalDate, List<TxDataRow>> remoteTransactionMapSorted = new TreeMap<>();
    	remoteTransactionMapSorted.put(todayDate, txList);
    	final TreeMap<LocalDate, List<TxDataRow>> localTransactionMapSorted = new TreeMap<>();
    	localTransactionMapSorted.put(todayDate, txList);
    	
    	new Expectations(transactionComparatorService) {
    		{
    			transactionComparatorService.sortMapByTxDate(remoteTransactionMap);
    			result = remoteTransactionMapSorted;
    			
    			transactionComparatorService.sortMapByTxDate(localTransactionMap);
    			result = localTransactionMapSorted;
    			
    			transactionsSizeComparator.compareTransactionListSizes(remoteTransactionMapSorted, localTransactionMapSorted);
    			times = 1;
    			
    			transactionsBalanceAnalyzer.analyzeTransactionBalances(remoteTransactionMapSorted, localTransactionMapSorted);
    			times = 1;
    		}
    	};
    	
    	// Method under test
    	transactionComparatorService.compareRemoteVsLocalTransactions(remoteTransactionMap, localTransactionMap);
    }
    
    @Test
    @DisplayName("sortMapByTxDate passing an empty map")
    public void sortMapByTxDate_empty_map() {
    	// Method under test
    	final Map<LocalDate, List<TxDataRow>> resultMap = transactionComparatorService.sortMapByTxDate((Map<LocalDate, List<TxDataRow>>) Collections.EMPTY_MAP);
    	
    	// Verification
    	assertNotNull(resultMap);
    	assertThat(resultMap.size(), is(0));
    }
    
    @Test
    @DisplayName("sortMapByTxDate passing null")
    public void sortMapByTxDate_pass_null() {
    	// Method under test
    	final Map<LocalDate, List<TxDataRow>> resultMap = transactionComparatorService.sortMapByTxDate(null);
    	
    	// Verification
    	assertNotNull(resultMap);
    	assertThat(resultMap.size(), is(0));
    }
    
    @Test
    @DisplayName("sortMapByTxDate passing map with Single entry, multiple transactions")
    public void sortMapByTxDate_map_single_entry_multiple_transaction() {
    	// Setup
    	final TxDataRow txDataRow = new TxDataRow();
    	final List<TxDataRow> txList = Arrays.asList(txDataRow, txDataRow, txDataRow);
    	final Map<LocalDate, List<TxDataRow>> transactionMap = new TreeMap<>();
    	transactionMap.put(LocalDate.now(), txList);
    	
    	// Method under test
    	final Map<LocalDate, List<TxDataRow>> resultMap = transactionComparatorService.sortMapByTxDate(transactionMap);
    	
    	// Verification
    	assertNotNull(resultMap);
    	assertThat(resultMap.size(), is(1));
    }
    
    @Test
    @DisplayName("sortMapByTxDate passing map with multiple entries, multiple transactions")
    public void sortMapByTxDate_map_multiple_entries_multiple_transaction() {
    	// Setup
    	final LocalDate nowDate = LocalDate.now();
    	final TxDataRow txDataRow = new TxDataRow();
    	final List<TxDataRow> txList = Arrays.asList(txDataRow, txDataRow, txDataRow);
    	
    	final Map<LocalDate, List<TxDataRow>> transactionMap = new TreeMap<>();
    	transactionMap.put(nowDate, txList);
    	transactionMap.put(LocalDate.of(2000, 10, 10), txList);
    	transactionMap.put(LocalDate.of(2010, 2, 28), txList);
    	
    	final TreeMap<LocalDate, List<TxDataRow>> expectedMap = new TreeMap<>();
    	expectedMap.put(LocalDate.of(2010, 2, 28), txList);
    	expectedMap.put(LocalDate.of(2000, 10, 10), txList);
    	expectedMap.put(nowDate, txList);
    	
    	// Method under test
    	final TreeMap<LocalDate, List<TxDataRow>> resultMap = transactionComparatorService.sortMapByTxDate(transactionMap);
    	
    	// Verification
    	assertNotNull(resultMap);
    	assertThat(resultMap.size(), is(3));
    	assertThat(resultMap, is(expectedMap));
    }
    
    @Test
    @DisplayName("analyzeTransactionBalances passing remote and local transactions. Balances match")
    public void analyzeTransactionBalances_balancesMatch_success() {
    	// Setup
    	
    	
    	// Method under test
    	final boolean balanceMatch = 
    			transactionsBalanceAnalyzer.analyzeTransactionBalances(remoteTransactionMap, localTransactionMap);    	
    	// Verification
//    	assertTrue(balanceMatch);
    }
}
