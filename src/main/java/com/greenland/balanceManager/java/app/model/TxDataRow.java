package com.greenland.balanceManager.java.app.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.inject.Inject;
import com.greenland.balanceManager.java.app.services.TransactionDataRowService;

public class TxDataRow {

	private LocalDate txDate;
	private String accountName;
	private String categoryName;
	private float debitAmount;
	private float creditAmount;
	private boolean isReconsiled;
	
	@Inject
	private TransactionDataRowService transactionDataRowService;
	
	private enum AmountType {
		
		Local("Local"), Debit("Debit"), Credit("Credit");
		
		private String amountType;
		
		AmountType(final String type) {
			amountType = type;
		}
		
		public String getAmountType() {
			return amountType;
		}
	};
	
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
	 * @param transactionRowArray
	 */
	public void setTransactionAmount(final String[] txRowArray) {
		
		final Object[] isValid = transactionDataRowService.isValidTransactionRow(txRowArray);
		
		if(isValid[0] == Boolean.FALSE) {
			return;
		}
		
		final int remoteAmountTypeColumnNo = 6;
		final int remoteCreditAmtColumnNo = 4;
		final int remoteDebitAmtColumnNo = 3;
		final int localAmountColumnNo = 9;
		
		// TODO: need to be done during split with regex
		final String[] transactionRowArray = removeQuotesFromTheArray(txRowArray);
		
		final String transactionType = transactionRowArray[remoteAmountTypeColumnNo];
		final Map<AmountType, String> amountMap = new HashMap<>();
		
		if (transactionType.equalsIgnoreCase(AmountType.Credit.getAmountType())) {
			amountMap.put(AmountType.Credit, transactionRowArray[remoteCreditAmtColumnNo]);
		} else if (transactionType.equalsIgnoreCase(AmountType.Debit.getAmountType())) {
			amountMap.put(AmountType.Debit, transactionRowArray[remoteDebitAmtColumnNo]);
		} else {
			amountMap.put(AmountType.Local, transactionRowArray[localAmountColumnNo]);
		}

		setCreditDebitAmounts(amountMap);
	}
	
	private String[] removeQuotesFromTheArray(String[] txRowArray) {
		final List<String> arrayNoQuotes = new ArrayList<>();
		
		for(int i = 0 ; i < txRowArray.length; i++) {
			arrayNoQuotes.add(txRowArray[i].replaceAll("\"", "").trim());
		}
		
		return arrayNoQuotes.toArray(new String[] {});
	}
	
	/**
	 * @param amountMap
	 */
	private void setCreditDebitAmounts(final Map<AmountType, String> amountMap) {
		for (final Entry<AmountType, String> entry : amountMap.entrySet()) {

			final float amount = parseStringAmountToNumber(entry.getValue());

			if ((entry.getKey() == AmountType.Local || entry.getKey() == AmountType.Credit) && amount > 0) {
				this.setCreditAmount(amount);
			} else if ((entry.getKey() == AmountType.Local || entry.getKey() == AmountType.Debit)) {
				this.setDebitAmount(Math.abs(amount)); // return absolute value. Example, -2 becomes 2.
			}
		}
	}
	
	/**
	 * @param entry
	 * @return
	 */
	private float parseStringAmountToNumber(final String amountString) {
		final float amount;
		
		try {
			amount = Float.parseFloat(amountString);
		} catch (final NumberFormatException ex) {
//			ex.printStackTrace();
			return Float.MIN_VALUE;
		}
		return amount;
	}

}
