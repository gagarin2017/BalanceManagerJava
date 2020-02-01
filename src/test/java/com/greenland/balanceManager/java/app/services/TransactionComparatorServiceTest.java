package com.greenland.balanceManager.java.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundException;
import com.greenland.balanceManager.java.app.model.TransactionsSourceDao;
import com.greenland.balanceManager.java.app.model.TxDataRow;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;

public class TransactionComparatorServiceTest {
	
	@Tested
	private TransactionComparatorServiceImpl transactionComparatorService;
	
	@Injectable
	private TransactionsReaderService transactionsReaderService;
	
	@Injectable
	private TransactionsSourceDao transactionsSourceDao;
	
    @Test
    void executeTransactionComparison_tx_not_found_exception() {
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
    void executeTransactionComparison_remote_tx_found_local_not_found_exception() {
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
    void executeTransactionComparison_remote_tx_not_found_local_found_exception() {
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
    void executeTransactionComparison_tx_found_compareTransactions() throws TransactionsNotFoundException {
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
    		}
    	};
    	
    	// Method under test
    	transactionComparatorService.executeTransactionComparison();
    	
    	// Verifications
    	new Verifications() {
    		{
    			transactionComparatorService.compareTransactions(remoteTransactionMap, localTransactionMap);
    			times = 1;
    		}
    	};
    	
    }

}
