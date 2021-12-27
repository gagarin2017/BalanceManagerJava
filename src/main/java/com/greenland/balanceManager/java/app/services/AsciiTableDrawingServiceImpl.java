package com.greenland.balanceManager.java.app.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.greenland.balanceManager.java.app.CommonUtils;
import com.greenland.balanceManager.java.app.model.TxDataRow;

import de.vandermeer.asciitable.AsciiTable;

/**
 * Service to draw nice, good looking ASCII table into the logs/console
 * 
 * @author Jura
 * @deprecated - as its difficult to debug, when throws an error during the render:
 * 					 "problem creating a border character, did not find character for <null>" 
 */
@Deprecated
public class AsciiTableDrawingServiceImpl implements AsciiTableDrawingService {
	
	private static Logger logger = LogManager.getLogger(AsciiTableDrawingServiceImpl.class);
	
	@Override
	public void drawAsciiTable(Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> remoteTransactionBalances,
			Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> localTransactionBalances, BigDecimal startingBalance) {

		final AsciiTable outputTable = new AsciiTable();
		outputTable.addRule();
		outputTable.addRow(null, "REMOTE", null, "LOCAL");
		outputTable.addRule();
		outputTable.addRow("Description", "Amount", "Description", "Amount");

		final List<String[]> outputRows = buildOutputTableRows(remoteTransactionBalances, localTransactionBalances,
				startingBalance);

		for (final String[] rowArr : outputRows) {
			if (rowArr[3].equalsIgnoreCase(CommonUtils.TX_RULE)) {
				outputTable.addRule();
			} else {
				outputTable.addRow(rowArr[0], rowArr[1], rowArr[2], rowArr[3]);
			}
		}

		outputTable.addRule();
		outputTable.getContext().setWidth(150);
		logger.info("\n" + outputTable.render());
	}
	
	/**
	 * 
	 * 
	 * @param remoteTransactionBalances
	 * @param localTransactionBalances
	 * @param startingBalance
	 * @return
	 */
	private List<String[]> buildOutputTableRows(
			final Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> remoteTransactionBalances,
			final Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> localTransactionBalances, final BigDecimal startingBalance) {
		
		final List<String[]> outputRows = new LinkedList<>();
		
		logger.info("Starting to compare balances.");
		logger.info("\n\n\n=============\n Starting amount: {}.\n=============\n\n", startingBalance);
		
		final BigDecimal[] runningBalance = new BigDecimal[2];
		runningBalance[0] = startingBalance;
		runningBalance[1] = startingBalance;

		for (final Entry<LocalDate, Pair<List<TxDataRow>, BigDecimal>> remoteMapEntry : remoteTransactionBalances.entrySet()) {
			 final LocalDate currentDte = remoteMapEntry.getKey();
			 
			 final Pair<List<TxDataRow>, BigDecimal> remoteTransactionsForDate = remoteMapEntry.getValue();                                                                                                                                                                                              
			 final Pair<List<TxDataRow>, BigDecimal> localTransactionsForDate = localTransactionBalances.get(currentDte);
			 
			 if (localTransactionsForDate != null) {
				 addTransactionsWithBalanceForDateRowsToOutput(currentDte, remoteTransactionsForDate, localTransactionsForDate, outputRows, runningBalance);
			 } else {
				 logger.error(String.format("Local transactions not found for the date: %s", currentDte.format(CommonUtils.DATE_TIME_FORMATTER)));
				 logger.info("Remote transactions:");
				 
				 for (final TxDataRow txDataRow : remoteTransactionsForDate.getLeft()) {
					 logger.info(txDataRow.toString());
				}
			 }
		}
		
		outputRows.add(new String[] {null, null, null, CommonUtils.TX_RULE});
		outputRows.add(new String[] {null, null, null, CommonUtils.TX_RULE});
		outputRows.add(new String[] {null, runningBalance[0].toString(), null, runningBalance[1].toString()});
		
		return outputRows;
	}

	/**
	 * Calculate balance for the date.
	 * Add lines with transactions for the date to @param outputRows
	 * 
	 * @param currentDte 
	 * @param remoteTransactionsForDate
	 * @param localTransactionsForDate
	 * @param outputRows 
	 * @param runningBalance 
	 */
	private void addTransactionsWithBalanceForDateRowsToOutput(final LocalDate currentDate, final Pair<List<TxDataRow>, BigDecimal> remoteTransactionsForDate,
			final Pair<List<TxDataRow>, BigDecimal> localTransactionsForDate, final List<String[]> outputRows, final BigDecimal[] runningBalance) {
		
		final String currentDateString = currentDate.format(CommonUtils.DATE_TIME_FORMATTER);
		
		if (remoteTransactionsForDate.getLeft() != null && localTransactionsForDate.getLeft() != null) {
			
			final TxDataRow[] remoteTransactionsArray = remoteTransactionsForDate.getLeft().toArray(new TxDataRow[] {});
			final TxDataRow[] localTransactionsArray = localTransactionsForDate.getLeft().toArray(new TxDataRow[] {});
			
			final BigDecimal remoteBalance = remoteTransactionsForDate.getRight().setScale(2, RoundingMode.HALF_UP);
			final BigDecimal localBalance = localTransactionsForDate.getRight().setScale(2, RoundingMode.HALF_UP);
			
			if(!remoteTxsBalancesOrSizeMatch(remoteTransactionsArray, localTransactionsArray, remoteBalance, localBalance)) {
				outputRows.add(new String[] {null, null, null, CommonUtils.TX_RULE});
			}
			
			outputRows.add(new String[] {null, null, null, CommonUtils.TX_RULE});
			outputRows.add(new String[] {null, null, null, currentDateString});
			outputRows.add(new String[] {null, null, null, CommonUtils.TX_RULE});
			
			
			if(!remoteTxsBalancesOrSizeMatch(remoteTransactionsArray, localTransactionsArray, remoteBalance, localBalance)) {
				outputRows.add(new String[] {null, null, null, CommonUtils.TX_RULE});
			}

			buildTableRowsForTransactionsPerDay(outputRows, remoteTransactionsArray, localTransactionsArray);
			
			final BigDecimal runningBalanceRemote = runningBalance[0].add(remoteBalance);
			final BigDecimal runningBalanceLocal = runningBalance[1].add(localBalance);
			
			outputRows.add(new String[] {null, null, null, CommonUtils.TX_RULE});
			outputRows.add(new String[] {
					String.format("%d transactions ", remoteTransactionsArray.length), 
					String.valueOf(remoteBalance) + " eur [" + runningBalanceRemote.toString() + "]", 
					String.format("%d transactions ", remoteTransactionsArray.length), 
					String.valueOf(localBalance) + " eur [" + runningBalanceLocal.toString() + "]"
					});

			runningBalance[0] = runningBalanceRemote;
			runningBalance[1] = runningBalanceLocal;

		} else {
			final String errorMessage = String.format(
					"\n\nTransaction number per date %s do not match Remote %d vs Local %d transactions.\n\n", 
					currentDateString,
					remoteTransactionsForDate.getLeft().size(),
					localTransactionsForDate.getLeft().size());
			logger.error(errorMessage);
		}
		
	}
	
	/**
	 * @param remoteTransactionsArray
	 * @param localTransactionsArray
	 * @param remoteBalance
	 * @param localBalance
	 * @return
	 */
	private boolean remoteTxsBalancesOrSizeMatch(final TxDataRow[] remoteTransactionsArray,
			final TxDataRow[] localTransactionsArray, final BigDecimal remoteBalance, final BigDecimal localBalance) {
		return remoteBalance.compareTo(localBalance) == 0 && remoteTransactionsArray.length == localTransactionsArray.length;
	}
	
	/**
	 * @param outputRows
	 * @param remoteTransactionsArray
	 * @param localTransactionsArray
	 */
	private void buildTableRowsForTransactionsPerDay(final List<String[]> outputRows,
			final TxDataRow[] remoteTransactionsArray, final TxDataRow[] localTransactionsArray) {
		
		if (remoteTransactionsArray.length == localTransactionsArray.length) {
			
			numberOfTransactionsMatchTable(outputRows, remoteTransactionsArray, localTransactionsArray);
			
		} else if (remoteTransactionsArray.length > localTransactionsArray.length) {
			
			final LinkedList<TxDataRow> locatTxsList = new LinkedList<>(Arrays.asList(localTransactionsArray));
			final ListIterator<TxDataRow> localListIterator = locatTxsList.listIterator();
			
			for(int i=0; i < remoteTransactionsArray.length; i++) {
				
				final TxDataRow remoteTx = remoteTransactionsArray[i];
				final TxDataRow localTx = localListIterator.hasNext() ? localListIterator.next() : null;
				
				outputRows.add(new String[] {
						remoteTx.getCategoryName(), remoteTx.getAmountNumberSignedString(), 
						localTx == null ? "-" : localTx.getCategoryName(), localTx == null ? "-" : localTx.getAmountNumberSignedString()});
			}
		} else {
			
			final LinkedList<TxDataRow> remoteTxsList = new LinkedList<>(Arrays.asList(remoteTransactionsArray));
			final ListIterator<TxDataRow> remoteListIterator = remoteTxsList.listIterator();
			
			for(int i=0; i < localTransactionsArray.length; i++) {
				
				final TxDataRow localTx = localTransactionsArray[i];
				final TxDataRow remoteTx = remoteListIterator.hasNext() ? remoteListIterator.next() : null;
				
				outputRows.add(new String[] {
						remoteTx == null ? "-" : remoteTx.getCategoryName(), remoteTx == null ? "-" : remoteTx.getAmountNumberSignedString(), 
						localTx == null ? "-" : localTx.getCategoryName(), localTx == null ? "-" : localTx.getAmountNumberSignedString()});
			}
		}
 		
	}
	
	/**
	 * Output table rows where number of transactions Remote vs Local match up
	 * 
	 * @param outputRows
	 * @param remoteTransactionsArray
	 * @param localTransactionsArray
	 */
	private void numberOfTransactionsMatchTable(final List<String[]> outputRows,
			final TxDataRow[] remoteTransactionsArray, final TxDataRow[] localTransactionsArray) {
		
		for(int i=0; i < remoteTransactionsArray.length; i++) {
			final TxDataRow remoteTx = remoteTransactionsArray[i];
			final TxDataRow localTx = localTransactionsArray[i];
			
			outputRows.add(new String[] {
					remoteTx.getCategoryName(), remoteTx.getAmountNumberSignedString(), 
					localTx.getCategoryName(), localTx.getAmountNumberSignedString()});
		}
	}

}
