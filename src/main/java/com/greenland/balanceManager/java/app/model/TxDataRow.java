package com.greenland.balanceManager.java.app.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

public class TxDataRow {
	
	private static final int VALID_TX_SIZE_IN_CELLS = 9;

	private final static String FIRST_CELL_TXT_IGNORE_ROW="Masked Card Number";
	
	private LocalDate txDate;
	private String accountName;
	private String categoryName;
	private float debitAmount;
	private float creditAmount;
	private boolean isReconsiled;
	
	public LocalDate getTxDate() {
		return txDate;
	}
	public void setTxDate(LocalDate txDate) {
		this.txDate = txDate;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public float getDebitAmount() {
		return debitAmount;
	}
	public void setDebitAmount(float debitAmount) {
		this.debitAmount = debitAmount;
	}
	public float getCreditAmount() {
		return creditAmount;
	}
	public void setCreditAmount(float creditAmount) {
		this.creditAmount = creditAmount;
	}
	public boolean isReconsiled() {
		return isReconsiled;
	}
	public void setReconsiled(boolean isReconsiled) {
		this.isReconsiled = isReconsiled;
	}

	public String toStringFull() {
		return "TxDataRow [txDate=" + txDate + ", accountName=" + accountName + ", categoryName=" + categoryName
				+ ", debitAmount=" + debitAmount + ", creaditAmount=" + creditAmount + ", isReconsiled=" + isReconsiled
				+ "]";
	}
	
	/**
	 * Parsing local transactions into the {@link TxDataRow}
	 * 
	 * @param txString
	 * @return
	 */
	public static TxDataRow parseLocalFileTransaction(final String txString) {
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
	
	/**
	 * @param txRow
	 * @return
	 */
	private static boolean isValidTransactionRow(final String[] row) {
		final String firstCellText = row.length > 0 ? row[0] : "";
		return row.length >= VALID_TX_SIZE_IN_CELLS && !firstCellText.equalsIgnoreCase(FIRST_CELL_TXT_IGNORE_ROW);
	}
	
	
	public static TxDataRow parseRemoteFileTransaction(String txString) {

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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(creditAmount);
		result = prime * result + Float.floatToIntBits(debitAmount);
		result = prime * result + ((txDate == null) ? 0 : txDate.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TxDataRow other = (TxDataRow) obj;
		if (Float.floatToIntBits(creditAmount) != Float.floatToIntBits(other.creditAmount))
			return false;
		if (Float.floatToIntBits(debitAmount) != Float.floatToIntBits(other.debitAmount))
			return false;
		if (txDate == null) {
			if (other.txDate != null)
				return false;
		} else if (!txDate.equals(other.txDate))
			return false;
		return true;
	}
	@Override
	public String toString() {
		final String SP = "\t";
		return "\t" + txDate + SP + categoryName + SP + "\t\t" +
				(debitAmount != 0 ? debitAmount : "") +
				(creditAmount != 0 ? creditAmount + " CR" : "");
	}
	
	public String getAmount() {
		return creditAmount > 0 ? creditAmount+"CR" : debitAmount > 0 ? String.valueOf(debitAmount) : "-";
	}
	
	public float getAmountNumber() {
		return creditAmount > 0 ? creditAmount : debitAmount > 0 ? debitAmount : 0;
	}
	
	
	

}
