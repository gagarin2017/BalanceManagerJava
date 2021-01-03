package com.greenland.balanceManager.java.app.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.greenland.balanceManager.java.app.dao.TransactionsSourceDao;
import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
import com.greenland.balanceManager.java.app.model.TxDataRow;

public class TransactionsReaderServiceImpl implements TransactionsReaderService {
	
	@Override
	public void populateTxMapsFromSource(final Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			final Map<LocalDate, List<TxDataRow>> localTransactionMap, final TransactionsSourceDao transactionsSourceDao, Object... sources) throws TransactionsNotFoundAtSourceException {
		
		transactionsSourceDao.populateTxMapsFromSource(remoteTransactionMap, localTransactionMap, sources);
	}

}
