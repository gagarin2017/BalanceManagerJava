package com.greenland.balanceManager.java.app.services;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.greenland.balanceManager.java.app.dao.TransactionsSourceDao;
import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
import com.greenland.balanceManager.java.app.model.TxDataRow;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;

public class TransactionsReaderServiceTest {
	
	@Tested
	private TransactionsReaderServiceImpl transactionsReaderService;
	
	private Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
	private Map<LocalDate,List<TxDataRow>> localTransactionMap = new HashMap<>();
	
	@Injectable
	private TransactionsSourceDao transactionsSourceDao;
	
	@Test
	@DisplayName("Making sure that the TransactionsSourceDao is called with the parameters")
	public void parseLocalFileTransaction_transaction_invalid_date_invalid() throws TransactionsNotFoundAtSourceException {
		// Setup
		new Expectations() {
			{
				transactionsSourceDao.populateTxMapsFromSource(remoteTransactionMap, localTransactionMap);
				times = 1;
			}
		};
		
		// Method under test
		transactionsReaderService.populateTxMapsFromSource(remoteTransactionMap, localTransactionMap, transactionsSourceDao);
	}

}
