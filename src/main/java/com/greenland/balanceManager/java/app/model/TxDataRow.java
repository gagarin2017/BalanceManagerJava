package com.greenland.balanceManager.java.app.model;

import java.time.LocalDate;

public class TxDataRow {

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
	
	
	

}
