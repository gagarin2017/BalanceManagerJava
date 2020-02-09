package com.greenland.balanceManager.java.app.model;

import java.time.LocalDate;
import java.util.Arrays;

public class TxDataRow {

	private LocalDate txDate;
	private String accountName;
	private String categoryName;
	private float debitAmount;
	private float creditAmount;
	private boolean isReconsiled;
	
	private static final String CREDIT = "CREDIT";
	private static final String DEBIT = "DEBIT";
	
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
	
	public String getAmountString() {
		return getCreditAmount() > 0 ? getCreditAmount()+"CR" : getDebitAmount() > 0 ? String.valueOf(getDebitAmount()) : "-";
	}
	
	public float getAmountNumber() {
		return getCreditAmount() > 0 ? getCreditAmount() : getDebitAmount() > 0 ? getDebitAmount() : 0;
	}
	
	/**
	 * @param txRowArray
	 */
	public void setTransactionAmount(final String[] txRowArray) {
		try {
			final String transactionType = txRowArray[6].replaceAll("\"", "").trim();
			if (transactionType.equalsIgnoreCase(DEBIT)) {
				// Debit amounts located at index 3
				setTheTransactionAmount(txRowArray, 3, true);
			} else if (transactionType.equalsIgnoreCase(CREDIT)) {
				// Credit amounts located at index 4
				setTheTransactionAmount(txRowArray, 4, false);
			}

		} catch (final NumberFormatException ex) {
			ex.printStackTrace();
		}		
	}
	
	/**
	 * @param txRowArray
	 */
	public void setTransactionAmountLocal(final String[] txRowArray) {
		
		final float amount;
		try {
			final String amountString = txRowArray[9].replaceAll(",", "");
			amount = Float.parseFloat(amountString);
		} catch (final NumberFormatException ex) {
			ex.printStackTrace();
			return;
		}
		
		if (amount > 0) {
			this.setCreditAmount(amount);
		} else {
			this.setDebitAmount(Math.abs(amount));
		}
	}

	/**
	 * @param txDataRow
	 * @param txRow
	 * @param columnIndex
	 * 				column index 0 based
	 * @param isDebitAmt 
	 */
	void setTheTransactionAmount(final String[] txRow, final int columnIndex, final boolean isDebitAmt) {
		if(columnIndex >= 0) {
			final String amtStr = txRow[columnIndex].replaceAll("\"", "").replaceAll(",", "").trim();
			final float amt;
			try {
				amt = Float.parseFloat(amtStr);
				if (isDebitAmt) {
					this.setDebitAmount(amt);
				} else {
					this.setCreditAmount(amt);
				}
			} catch (NumberFormatException ex) {
				System.out.println(String.format("Exception thrown for the row [%s] [%s] index: %d isDebitAmount: %s", this, Arrays.toString(txRow), columnIndex, isDebitAmt));
				ex.printStackTrace();
			}
		}
	}
	

}
