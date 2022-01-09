package com.greenland.balanceManager.java.app.model;

import static com.greenland.balanceManager.java.app.CommonUtils.DATE_TIME_FORMATTER;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

/**
 * @author Jura
 *
 */
@Data
@ToString
public class TxDataRow {

	@JsonProperty("date")
	@JsonFormat
    (shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private LocalDate txDate;
	
	@JsonProperty("account")
	private String accountName;
	private String description;
	private String memo;
	
	@JsonProperty("category")
	private String categoryName;
	
	private String clr;
	
	private boolean isReconsiled;
	
	private String tag;
	private BigDecimal debitAmount;
	private BigDecimal creditAmount;
	private boolean isRemote;
	
	public static final String RECONCILED_FLAG_STRING = "R";
	
	private static Logger logger = LogManager.getLogger(TxDataRow.class);
	
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
	
	/**
	 * @return
	 */
	public BigDecimal getAmountAsNumber() {
		final BigDecimal amount;
		
		if (isCreditAmountPositive()) {
			amount = getCreditAmount();
		} else if (isDebitAmountPositive()) {
			amount = getDebitAmount();
		} else {
			amount = BigDecimal.ZERO;
		}
		
		return amount;
	}

	private boolean isDebitAmountPositive() {
		return getDebitAmount().compareTo(BigDecimal.ZERO) > 0;
	}

	private boolean isCreditAmountPositive() {
		return getCreditAmount().compareTo(BigDecimal.ZERO) > 0;
	}
	
	/**
	 * @return Example, +23 for credit, -130 for debit amounts.
	 */
	public String getAmountNumberSignedString() {
		
		final String resultString;
		
		if (isCreditAmountPositive()) {
			resultString = "+"+getCreditAmount();
		} else if (isDebitAmountPositive()) {
			resultString = "-"+getDebitAmount();
		} else {
			resultString = "-";
		}
		
		return resultString;
	}
	
	/**
	 * @param transactionRowArray
	 */
	public void setTransactionAmount(final String[] txRowArray) {
		
		logger.debug("Setting amount for valid transaction [{}]", Arrays.toString(txRowArray));
		
		final int remoteAmountTypeColumnNo = 6;
		final int remoteCreditAmtColumnNo = 4;
		final int remoteDebitAmtColumnNo = 3;
		final int localAmountColumnNo = 9;
		
		// TODO: need to be done during split with regex
		final String[] transactionRowArray = removeQuotesFromTheArray(txRowArray);
		
		final String transactionType = transactionRowArray[remoteAmountTypeColumnNo];
		final Map<AmountType, String> amountMap = new HashMap<>();
		
		logger.debug("Transaction type: {}", transactionType);
		
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

			final String amountString = entry.getValue().replace(",", "");
			final BigDecimal amount = new BigDecimal(amountString);

			if ((entry.getKey() == AmountType.Local || entry.getKey() == AmountType.Credit) && amount.compareTo(BigDecimal.ZERO) > 0) {
				this.setCreditAmount(amount);
			} else if ((entry.getKey() == AmountType.Local || entry.getKey() == AmountType.Debit)) {
				this.setDebitAmount(amount.abs()); // return absolute value. Example, -2 becomes 2.
			}
		}
	}

	public BigDecimal getDebitAmount() {
		return debitAmount == null ? BigDecimal.ZERO : debitAmount;
	}

	public BigDecimal getCreditAmount() {
		return creditAmount == null ? BigDecimal.ZERO : creditAmount;
	}

	public void setReconsiled(final boolean isReconsiled) {
		this.isReconsiled = isReconsiled;
	}

	/**
	 * Set the transaction amount {@link #setCreditAmount(BigDecimal) if amount is positive, else method sets #setDebitAmount(BigDecimal)
	 * 
	 * @param amount
	 * 			{@link BigDecimal} of the amount
	 */
	public void setAmount(final BigDecimal amount) {
		if(amount.signum() < 0) {
			setDebitAmount(amount);
		} else {
			setCreditAmount(amount);
		}
	}

	/**
	 * Sets the boolean flag based on the passed string. If passed string is equal to the {@link #RECONCILED_FLAG_STRING},
	 * then {@link #TxDataRow()} is reconciled, else its not.
	 * 
	 * @param reconciledString
	 */
	public void setReconsiled(final String reconciledString) {
		setReconsiled(reconciledString!= null && reconciledString.equalsIgnoreCase(RECONCILED_FLAG_STRING) ? true : false);
	}

	public TxDataRow() { }
	
	
	public TxDataRow(LocalDate txDate, String accountName, String categoryName, boolean isReconsiled,
			boolean isRemote, BigDecimal amount) {
		this.txDate = txDate;
		this.accountName = accountName;
		this.categoryName = categoryName;
		this.isReconsiled = isReconsiled;
		this.isRemote = isRemote;
		setAmount(amount);
	}

	@Override
	public int hashCode() {
		return Objects.hash(creditAmount, debitAmount, txDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TxDataRow)) {
			return false;
		}
		TxDataRow other = (TxDataRow) obj;
		return Objects.equals(creditAmount, other.creditAmount) && Objects.equals(debitAmount, other.debitAmount)
				&& Objects.equals(txDate, other.txDate);
	}

	public void setDebitAmount(final BigDecimal debitAmount) {
		this.debitAmount = debitAmount != null ? debitAmount.setScale(2) : BigDecimal.valueOf(Double.MIN_VALUE);
	}
	
	public void setCreditAmount(final BigDecimal creditAmount) {
		this.creditAmount = creditAmount != null ? creditAmount.setScale(2) : BigDecimal.valueOf(Double.MIN_VALUE);
	}
	
	/**
	 * Set TxDate from the passed date string
	 * 
	 * @param date
	 * 			date String
	 */
	public void setTxDate(final String date) {
		this.txDate = LocalDate.parse(date, DATE_TIME_FORMATTER);
	}
	
	/**
	 * Set TxDate from the passed {@link LocalDate}
	 * 
	 * @param date
	 */
	public void setTxDate(final LocalDate date) {
		this.txDate = date;
	}

	/**
	 * Set the {@link TxDataRow} amount from the amount string.
	 * Method will return {Integer.MIN_VALUE} if parsing was unsuccessful
	 * 
	 * @param amount
	 * 			String
	 */
	public void setAmount(final String amount) {
		try {
			if (amount != null) {
				this.setAmount(new BigDecimal(amount.replace(",", "").trim()).setScale(2));
			}
		} catch (ArithmeticException | NumberFormatException ex) {
			this.setCreditAmount(new BigDecimal(Integer.MIN_VALUE).setScale(2, BigDecimal.ROUND_HALF_DOWN));
		}
		
	}
}
