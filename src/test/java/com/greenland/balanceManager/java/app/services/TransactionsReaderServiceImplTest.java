package com.greenland.balanceManager.java.app.services;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.greenland.balanceManager.java.app.model.TransactionsSourceDao;
import com.greenland.balanceManager.java.app.model.TxDataRow;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;

public class TransactionsReaderServiceImplTest {
	
	@Tested
	private TransactionsReaderServiceImpl transactionsReaderServiceImpl;
	
	@Injectable
	private TransactionsSourceDao transactionsSourceDao;
	
    @Test
    void populateTxMapsFromSource_() throws FileNotFoundException {
    	// Setup
    	final Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
    	final Map<LocalDate, List<TxDataRow>> localTransactionMap = new HashMap<>();
    	
    	new Expectations() {
    		{
    			transactionsSourceDao.populateTxMapsFromSource(remoteTransactionMap, localTransactionMap);
    			times = 1;
    			
//    			transactionComparatorService.getRemoteTransactionMap();
//    			result = remoteTransactionMap;
//    			
//    			transactionComparatorService.getLocalTransactionMap();
//    			result = localTransactionMap;
//    			
//    			transactionsReaderService.populateTxMapsFromSource(remoteTransactionMap, localTransactionMap, withInstanceOf(TransactionsSourceDao.class));
//    			times = 1;
    		}
    	};
    	
    	// Method under test
    	transactionsReaderServiceImpl.populateTxMapsFromSource(remoteTransactionMap, localTransactionMap, transactionsSourceDao);

    	// Verifications
//        final String expectedErrorTxt = String.format(TransactionComparatorServiceImpl.TX_NOT_FOUND_ERROR, 0, 0);
//        assertEquals(expectedErrorTxt, exception.getMessage());
    }
    

}
