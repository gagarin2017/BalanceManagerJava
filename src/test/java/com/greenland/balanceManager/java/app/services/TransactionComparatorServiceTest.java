package com.greenland.balanceManager.java.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundException;
import com.greenland.balanceManager.java.app.model.TransactionsSourceDao;
import com.greenland.balanceManager.java.app.model.TxDataRow;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;

public class TransactionComparatorServiceTest {
	
	@Tested
	private TransactionComparatorServiceImpl transactionComparatorService;
	
	@Injectable
	private TransactionsReaderService transactionsReaderService;
	
	@Injectable
	private TransactionsSourceDao transactionsSourceDao;
	
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
    			
    			transactionComparatorService.compareTransactions(remoteTransactionMap, localTransactionMap);
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
    			
    			transactionComparatorService.compareTransactionListSizes(remoteTransactionMapSorted, withInstanceOf(NavigableMap.class));
    			result = true;
    			times = 1;
    			
    			transactionComparatorService.analyzeTransactionBalances(remoteTransactionMapSorted, withInstanceOf(NavigableMap.class));
    			times = 1;
    		}
    	};
    	
    	// Method under test
    	transactionComparatorService.compareTransactions(remoteTransactionMap, localTransactionMap);
    }
    
    @Test
    @DisplayName("compareTransactions remote has two txs, but local is empty")
    public void compareTransactions_remote_twoTxs_local_empty() {
    	// Setup
    	final TxDataRow txDataRow = new TxDataRow();
    	final TxDataRow txDataRow1 = new TxDataRow();
    	final List<TxDataRow> txList = Arrays.asList(txDataRow, txDataRow1);
    	final LocalDate todayDate = LocalDate.now();
    	
    	remoteTransactionMap.put(todayDate, txList);
    	
    	new Expectations(transactionComparatorService) {
    		{
    			transactionComparatorService.compareTransactionListSizes(withInstanceLike(new TreeMap<LocalDate, List<TxDataRow>>()), withInstanceOf(NavigableMap.class));
    			result = false;
    			times = 1;
    			
    			transactionComparatorService.analyzeTransactionBalances(withInstanceLike(new TreeMap<LocalDate, List<TxDataRow>>()), withInstanceOf(NavigableMap.class));
    			times = 0;
    		}
    	};
    	
    	// Method under test
    	transactionComparatorService.compareTransactions(remoteTransactionMap, localTransactionMap);
    }
}
