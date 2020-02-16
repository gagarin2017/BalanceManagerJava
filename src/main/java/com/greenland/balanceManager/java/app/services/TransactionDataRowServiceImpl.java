package com.greenland.balanceManager.java.app.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.greenland.balanceManager.java.app.model.TxDataRow;

public class TransactionDataRowServiceImpl implements TransactionDataRowService {
	
	public static final String REMOTE_TX_REGEX = "(,)(?=(?:[^\"]|\"[^\"]*\")*$)";
	private static final String DATE_FORMAT = "d/MM/yyyy";

	/**
	 * Parsing local transactions into the {@link TxDataRow}
	 * 
	 * @param txString
	 * @return
	 */
	@Override
	public TxDataRow parseLocalFileTransaction(final String txString) {
		TxDataRow txDataRow = null;
		final String[] txRowArray = txString.split("\t");
		final Object[] isValidDate = isValidTransactionRow(txRowArray);

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
		TxDataRow txDataRow = null;
		final String[] txRowArray = txString.split(REMOTE_TX_REGEX);
		final Object[] validDate = isValidTransactionRow(txRowArray);
		
		if ((boolean) validDate[0]) {
			txDataRow = new TxDataRow();
			txDataRow.setAccountName(txRowArray[0]);
			txDataRow.setTxDate((LocalDate) validDate[1]);
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
				LocalDate txDate = LocalDate.parse(txRowArray[1], DateTimeFormatter.ofPattern(DATE_FORMAT));
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
