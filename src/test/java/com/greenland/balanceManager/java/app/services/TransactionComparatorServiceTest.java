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
	
    @Test
    void executeTransactionComparison_tx_not_found_exception() throws FileNotFoundException {
    	// Setup
    	final Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
    	final Map<LocalDate, List<TxDataRow>> localTransactionMap = new HashMap<>();
    	
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
    void executeTransactionComparison_remote_tx_found_local_not_found_exception() throws FileNotFoundException {
		// Setup
    	final TxDataRow txDataRow = new TxDataRow();
    	final List<TxDataRow> txList = Arrays.asList(txDataRow);
    	final LocalDate todayDate = LocalDate.now();
    	
    	final Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
    	remoteTransactionMap.put(todayDate, txList);
    	
    	final Map<LocalDate, List<TxDataRow>> localTransactionMap = new HashMap<>();
    	
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
    void executeTransactionComparison_remote_tx_not_found_local_found_exception() throws FileNotFoundException {
    	// Setup
    	final Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
    	final Map<LocalDate, List<TxDataRow>> localTransactionMap = new HashMap<>();
    	
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
    void executeTransactionComparison_tx_found_compareTransactions() throws FileNotFoundException {
    	// Setup
    	final TxDataRow txDataRow = new TxDataRow();
    	final List<TxDataRow> txList = Arrays.asList(txDataRow);
    	final LocalDate todayDate = LocalDate.now();
    	
    	final Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
    	remoteTransactionMap.put(todayDate, txList);
    	
    	final Map<LocalDate, List<TxDataRow>> localTransactionMap = new HashMap<>();
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
    @DisplayName("compareTransactions")
    public void compareTransactions_() {
    	// Setup
    	final Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
    	final Map<LocalDate, List<TxDataRow>> localTransactionMap = new HashMap<>();

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
    			
    			transactionComparatorService.analyzeTransactionBalances(remoteTransactionMapSorted, withInstanceOf(NavigableMap.class));
    			times = 1;
    		}
    	};
    	
    	// Method under test
    	transactionComparatorService.compareTransactions(remoteTransactionMap, localTransactionMap);
    	
    }
    

}
