package com.greenland.balanceManager.java.app.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import com.greenland.balanceManager.java.app.model.TxDataRow;

public class TransactionDataRowServiceImpl implements TransactionDataRowService {
	
	private static final int VALID_TX_SIZE_IN_CELLS = 9;
	private final static String FIRST_CELL_TXT_IGNORE_ROW="Masked Card Number";

	/**
	 * Parsing local transactions into the {@link TxDataRow}
	 * 
	 * @param txString
	 * @return
	 */
	@Override
	public TxDataRow parseLocalFileTransaction(final String txString) {
		TxDataRow txDataRow = null;
		final String[] row = txString.split("\t");
		
		if(isValidTransactionRow(row)) {
			LocalDate txDate = LocalDate.MIN;
			try {
				txDate = LocalDate.parse(row[1], DateTimeFormatter.ofPattern("d/MM/yyyy"));
			} catch (final DateTimeParseException ex) {
				//ex.printStackTrace();
			}
			
			if (!txDate.isEqual(LocalDate.MIN)) {
			txDataRow = new TxDataRow();
			
			txDataRow.setTxDate(txDate);
			txDataRow.setAccountName(row[2]);
			txDataRow.setCategoryName(row[6]);
			txDataRow.setReconsiled(row[8].equalsIgnoreCase("R"));
			
			float amt = Float.MIN_VALUE;
			try {
				final String amount = row[9].replaceAll(",", "");
				amt = Float.parseFloat(amount);
				if(amt != Float.MIN_VALUE && amt > 0) {
					txDataRow.setCreditAmount(amt);
				} else {
					txDataRow.setDebitAmount(Math.abs(amt));
				}
			} catch (final NumberFormatException ex) {
				ex.printStackTrace();
			}
			
			}
		}
		
		return txDataRow;
	}
	@Override
	public TxDataRow parseRemoteFileTransaction(final String txString) {

		TxDataRow txDataRow = null;
		final String[] row = txString.split("(,)(?=(?:[^\"]|\"[^\"]*\")*$)");

		if (isValidTransactionRow(row)) {
			LocalDate txDate = LocalDate.MIN;
			try {
				txDate = LocalDate.parse(row[1], DateTimeFormatter.ofPattern("d/MM/yyyy"));
			} catch (final DateTimeParseException ex) {
				System.out.println("Failed to parse the date @ TxRow : ["+row[1]+"]");
				return null;
//				ex.printStackTrace();
			}

			if (!txDate.isEqual(LocalDate.MIN)) {
				txDataRow = new TxDataRow();

				txDataRow.setTxDate(txDate);
				txDataRow.setAccountName(row[0]);
				txDataRow.setCategoryName(row[2]);

				try {

					if (row[6].replaceAll("\"", "").trim().equalsIgnoreCase("DEBIT")) {
						// Debit amounts located at index 3
						updateTheTransactionAmount(txDataRow, row, 3, true);
					} else if (row[6].replaceAll("\"", "").trim().equalsIgnoreCase("CREDIT")) {
						// Credit amounts located at index 4
						updateTheTransactionAmount(txDataRow, row, 4, false);
					}

				} catch (final NumberFormatException ex) {
					 ex.printStackTrace();
				}

			} else {
				throw new RuntimeException("Remote file parsing failed. Transaction date is incorrect: "+row[1]);
			}
		}

		return txDataRow;
	}

	/**
	 * @param txDataRow
	 * @param txRow
	 * @param columnIndex
	 * 				column index 0 based
	 * @param isDebitAmt 
	 */
	private static void updateTheTransactionAmount(TxDataRow txDataRow, final String[] txRow, final int columnIndex, final boolean isDebitAmt) {
		if(columnIndex >= 0) {
			final String amtStr = txRow[columnIndex].replaceAll("\"", "").replaceAll(",", "").trim();
			final float amt;
			try {
				amt = Float.parseFloat(amtStr);
				if (isDebitAmt) {
					txDataRow.setDebitAmount(amt);
				} else {
					txDataRow.setCreditAmount(amt);
				}
			} catch (NumberFormatException ex) {
				System.out.println(String.format("Exception thrown for the row [%s] [%s] index: %d isDebitAmount: %s", txDataRow, Arrays.toString(txRow), columnIndex, isDebitAmt));
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * @param txRow
	 * @return
	 */
	private static boolean isValidTransactionRow(final String[] row) {
		final String firstCellText = row.length > 0 ? row[0] : "";
		return row.length >= VALID_TX_SIZE_IN_CELLS && !firstCellText.equalsIgnoreCase(FIRST_CELL_TXT_IGNORE_ROW);
	}
}
