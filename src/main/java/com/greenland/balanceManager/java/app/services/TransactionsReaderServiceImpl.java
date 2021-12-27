package com.greenland.balanceManager.java.app.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.greenland.balanceManager.java.app.dao.TransactionsSourceDao;
import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
import com.greenland.balanceManager.java.app.external.domain.OutputTxData;
import com.greenland.balanceManager.java.app.model.TxDataRow;

public class TransactionsReaderServiceImpl implements TransactionsReaderService {
	
	@Deprecated
	@Override
	public void populateTxMapsFromSource(final Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			final Map<LocalDate, List<TxDataRow>> localTransactionMap, final TransactionsSourceDao transactionsSourceDao, Object... sources) throws TransactionsNotFoundAtSourceException {
		
		transactionsSourceDao.populateTxMapsFromSource(remoteTransactionMap, localTransactionMap, sources);
	}

	@Override
	public void populateTxMapsFromSource(Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			Map<LocalDate, List<TxDataRow>> localTransactionMap, TransactionsSourceDao transactionsSourceDao,
			Object inputTxData, OutputTxData outputTxData) {
		
		transactionsSourceDao.populateTxMapsFromRemoteApp(remoteTransactionMap, localTransactionMap, inputTxData, outputTxData);
		
	}

}
