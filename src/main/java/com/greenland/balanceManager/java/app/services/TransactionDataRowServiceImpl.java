package com.greenland.balanceManager.java.app.services;

import java.time.LocalDate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.greenland.balanceManager.java.app.CommonUtils;
import com.greenland.balanceManager.java.app.model.TxDataRow;

@Singleton
public class TransactionDataRowServiceImpl implements TransactionDataRowService {
	private static Logger logger = LogManager.getLogger(TransactionDataRowServiceImpl.class);
	
	@Inject
	private TransactionsValidationService transactionsValidationService;
	
	/**
	 * Parsing local transactions into the {@link TxDataRow}
	 * 
	 * @param txString
	 * @return
	 */
	@Override
	public TxDataRow parseLocalFileTransaction(final String txString) {
		logger.debug("Trying to parse the line [{}] into transaction.", txString);
		
		TxDataRow txDataRow = null;
		final String[] txRowArray = txString.split("\t");
		final Object[] isValidDate = transactionsValidationService.isValidTransactionRow(txRowArray);

		logger.debug("Is it valid? {}. Transaction date: {}.", isValidDate[0], isValidDate[1]);

		if ((boolean) isValidDate[0]) {
			txDataRow = new TxDataRow();
			txDataRow.setTxDate((LocalDate) isValidDate[1]);
			txDataRow.setAccountName(txRowArray[2]);
			txDataRow.setCategoryName(txRowArray[6]);
			txDataRow.setReconsiled(txRowArray[8]);
			txDataRow.setTransactionAmount(txRowArray);
		}

		return txDataRow;
	}
	
	@Override
	public TxDataRow parseRemoteFileTransaction(final String txString) {
		logger.debug("Trying to parse the line [{}] into transaction.", txString);

		TxDataRow txDataRow = null;
		final String[] txRowArray = txString.split(CommonUtils.REMOTE_TX_REGEX);
		final Object[] isValidTransactionAndDate = transactionsValidationService.isValidTransactionRow(txRowArray);
		
		logger.debug("Is it valid? {}. Transaction date: {}.", isValidTransactionAndDate[0], isValidTransactionAndDate[1]);
		
		if ((boolean) isValidTransactionAndDate[0]) {
			txDataRow = new TxDataRow();
			txDataRow.setAccountName(txRowArray[0]);
			txDataRow.setTxDate((LocalDate) isValidTransactionAndDate[1]);
			txDataRow.setCategoryName(txRowArray[2]);
			txDataRow.setTransactionAmount(txRowArray);
		}

		return txDataRow;
	}


}
