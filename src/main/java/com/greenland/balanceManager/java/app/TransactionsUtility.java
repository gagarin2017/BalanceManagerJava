package com.greenland.balanceManager.java.app;

import java.util.Comparator;

import com.greenland.balanceManager.java.app.model.TxDataRow;

/**
 * @author Jura
 *
 */
public class TransactionsUtility {
	
	public static class TransactionsByAmountComparator implements Comparator<TxDataRow> 
	{ 

		@Override
		public int compare(final TxDataRow txDataRow1, final TxDataRow txDataRow2) {
			return txDataRow1.getAmountAsNumber().compareTo(txDataRow2.getAmountAsNumber());
		} 
	} 

}
