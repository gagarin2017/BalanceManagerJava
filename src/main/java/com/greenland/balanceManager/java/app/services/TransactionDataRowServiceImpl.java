package com.greenland.balanceManager.java.app.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Singleton;
import com.greenland.balanceManager.java.app.CommonUtils;
import com.greenland.balanceManager.java.app.model.TxDataRow;

@Singleton
public class TransactionDataRowServiceImpl implements TransactionDataRowService {
	private static Logger logger = LogManager.getLogger(TransactionDataRowServiceImpl.class);

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
		final Object[] isValidDate = isValidTransactionRow(txRowArray);

		logger.debug("Is it valid? {}. Transaction date: {}.", isValidDate[0], isValidDate[1]);

		if ((boolean) isValidDate[0]) {
			txDataRow = new TxDataRow();
			txDataRow.setTxDate((LocalDate) isValidDate[1]);
			txDataRow.setAccountName(txRowArray[2]);
			txDataRow.setCategoryName(txRowArray[6]);
			txDataRow.setReconsiled(txRowArray[8].equalsIgnoreCase("R"));
			txDataRow.setTransactionAmount(txRowArray);
		}

		return txDataRow;
	}
	
	@Override
	public TxDataRow parseRemoteFileTransaction(final String txString) {
		logger.debug("Trying to parse the line [{}] into transaction.", txString);

		TxDataRow txDataRow = null;
		final String[] txRowArray = txString.split(CommonUtils.REMOTE_TX_REGEX);
		final Object[] isValidTransactionAndDate = isValidTransactionRow(txRowArray);
		
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

	
	/**
	 * Checks if passed transaction array is a valid transaction.
	 * Transaction string is only valid if first element of the passed array is a valid {@link LocalDate}
	 * 
	 * @param txRow
	 * @return
	 */
	@Override
	public Object[] isValidTransactionRow(final String[] txRowArray) {
		
		final Object[] isValidDate = new Object[2];
		isValidDate[0] = false;
		
		if (txRowArray.length > 1) {
			try {
				LocalDate txDate = LocalDate.parse(txRowArray[1], CommonUtils.DATE_TIME_FORMATTER);
				txDate = (txDate.isAfter(LocalDate.MIN) && txDate.isBefore(LocalDate.MAX)) 
						? txDate 
						: null;
				isValidDate[0] = true;
				isValidDate[1] = txDate;
			} catch (final DateTimeParseException ex) {
//				ex.printStackTrace();
			}
		}
		
		return isValidDate;
	}
}
