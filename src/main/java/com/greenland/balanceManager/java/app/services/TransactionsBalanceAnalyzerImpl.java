package com.greenland.balanceManager.java.app.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.greenland.balanceManager.java.app.CommonUtils;
import com.greenland.balanceManager.java.app.TransactionsUtility;
import com.greenland.balanceManager.java.app.model.TxDataRow;

import de.vandermeer.asciitable.AsciiTable;

/**
 * @author Jura
 *
 */
public class TransactionsBalanceAnalyzerImpl implements TransactionsBalanceAnalyzer {
	
	private static Logger logger = LogManager.getLogger(TransactionsBalanceAnalyzerImpl.class);

	@Override
	public boolean analyzeTransactionBalances(final Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			final Map<LocalDate, List<TxDataRow>> localTransactionMap) {
		
		BigDecimal startAmount = CommonUtils.START_AMOUNT;
		
		logger.info("Starting to compare balances.");
		logger.info("\n\n\n=============\n Starting amount: {}.\n=============\n\n", startAmount);
		
		logger.info("Starting to calculate balance for remote.");
		final Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> remoteTransactionBalances = buildTransactionsBalancesPerDateMap(remoteTransactionMap);
		
		logger.info("Starting to calculate balance for local.");
		final Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> localTransactionBalances = buildTransactionsBalancesPerDateMap(localTransactionMap);
		
		final boolean doRemoteVsLocalBalancesMatch = compareOverallTransactionBalances(remoteTransactionBalances, localTransactionBalances);
		
		final List<String[]> outputRows = buildOutputTableRows(remoteTransactionBalances, localTransactionBalances);
		drawAsciiTable(outputRows);
		
		return doRemoteVsLocalBalancesMatch;
	}
	
	/**
	 * @param transactionsMap
	 * @param startAmount
	 * @return
	 */
	private Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> buildTransactionsBalancesPerDateMap (
			final Map<LocalDate, List<TxDataRow>> transactionsMap) {
		
		final Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> transactionBalancesPerDate = new HashMap<>();
		
		for (final Entry<LocalDate, List<TxDataRow>> transactionsMapEntry : transactionsMap.entrySet()) {
			
//			logger.debug("Calculating remote transactions balance for the date: {}", transactionsMapEntry.getKey().format(CommonUtils.DATE_TIME_FORMATTER));
			final List<TxDataRow> transactionsForDate = transactionsMapEntry.getValue();
			
			// sorting transactions by amounts
	        Collections.sort(transactionsForDate, new TransactionsUtility.TransactionsByAmountComparator()); 
			final Pair<List<TxDataRow>, BigDecimal> txsBalancesForDatePair = Pair.of(transactionsForDate, calculateBalanceForTheDate(transactionsForDate));
			transactionBalancesPerDate.put(transactionsMapEntry.getKey(), txsBalancesForDatePair);
		}
		
		return transactionBalancesPerDate;
	}
	
	/**
	 * @param remoteTransactionBalances
	 * @param localTransactionBalances
	 * @return
	 */
	private boolean compareOverallTransactionBalances(
			final Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> remoteTransactionBalances,
			final Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> localTransactionBalances) {
		
		final boolean doRemoteVsLocalBalancesMatch;
		
		BigDecimal remoteTotal = new BigDecimal("0");
		BigDecimal localTotal = new BigDecimal("0");
		
		for (final Entry<LocalDate, Pair<List<TxDataRow>, BigDecimal>> remoteMapEntry : remoteTransactionBalances.entrySet()) {
			remoteTotal = remoteTotal.add(remoteMapEntry.getValue().getRight());
		}
		
		for (final Entry<LocalDate, Pair<List<TxDataRow>, BigDecimal>> localMapEntry : localTransactionBalances.entrySet()) {
			localTotal = localTotal.add(localMapEntry.getValue().getRight());
		}
		
		if (remoteLocalPositiveAndMatch(remoteTotal, localTotal)) {
			logger.info("Overall balances match! We are all good! Remote: {} euros vs Local: {} euros.", remoteTotal, localTotal);
			doRemoteVsLocalBalancesMatch = true;
		} else {
			logger.info("Overall balances 0 or  DID NOT match! Remote: {} euros vs Local: {} euros.", remoteTotal, localTotal);
//			handleBalancesDidNotMatch();
			doRemoteVsLocalBalancesMatch = false;
		}
		
		return doRemoteVsLocalBalancesMatch;
	}
	
	/**
	 * 
	 * 
	 * @param remoteTransactionBalances
	 * @param localTransactionBalances
	 * @return
	 */
	private List<String[]> buildOutputTableRows(
			final Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> remoteTransactionBalances,
			final Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> localTransactionBalances) {
		
		final List<String[]> outputRows = new LinkedList<>();
		
		final BigDecimal remoteRunningBalance = CommonUtils.START_AMOUNT;
		final BigDecimal localRunningBalance = CommonUtils.START_AMOUNT;
		
		final BigDecimal[] runningBalance = new BigDecimal[2];
		runningBalance[0] = remoteRunningBalance;
		runningBalance[1] = localRunningBalance;

		for (final Entry<LocalDate, Pair<List<TxDataRow>, BigDecimal>> remoteMapEntry : remoteTransactionBalances.entrySet()) {
			 final LocalDate currentDte = remoteMapEntry.getKey();
			 
			 final Pair<List<TxDataRow>, BigDecimal> remoteTransactionsForDate = remoteMapEntry.getValue();                                                                                                                                                                                              
			 final Pair<List<TxDataRow>, BigDecimal> localTransactionsForDate = localTransactionBalances.get(currentDte);
			 
			 if (localTransactionsForDate != null) {
				 outputTransactionsWithBalancesForDate(currentDte, remoteTransactionsForDate, localTransactionsForDate, outputRows, runningBalance);
			 } else {
				 logger.error("Local transactions not found for the date: %s", currentDte.format(CommonUtils.DATE_TIME_FORMATTER));
				 logger.info("Remote transactions:");
				 
				 for (final TxDataRow txDataRow : remoteTransactionsForDate.getLeft()) {
					 logger.info(txDataRow.toStringFull());
				}
			 }
		}
		
		outputRows.add(new String[] {null, null, null, CommonUtils.TX_RULE});
		outputRows.add(new String[] {null, null, null, CommonUtils.TX_RULE});
		outputRows.add(new String[] {null, runningBalance[0].toString(), null, runningBalance[1].toString()});
		
		return outputRows;
	}
	
	/**
	 * Draw nice table using {@link AsciiTable}
	 * 
	 * @param outputRows
	 */
	private void drawAsciiTable(final List<String[]> outputRows) {
		final AsciiTable outputTable = new AsciiTable();
		outputTable.addRule();
		outputTable.addRow(null, "REMOTE", null, "LOCAL");
		outputTable.addRule();
		outputTable.addRow("Description", "Amount", "Description", "Amount");

		for (final String[] rowArr : outputRows) {
			if (rowArr[3].equalsIgnoreCase(CommonUtils.TX_RULE)) {
				outputTable.addRule();
			} else {
				outputTable.addRow(rowArr[0], rowArr[1], rowArr[2], rowArr[3]);
			}
		}
		
		outputTable.addRule();
		outputTable.getContext().setWidth(150);
		logger.debug("\n"+outputTable.render());
	}
	
	/**
	 * Calculate balance for the date by adding Credit and Debit amounts to the startAmount.
	 * 
	 * @param transactionsForDate
	 * @param startAmount
	 * @return
	 */
	private BigDecimal calculateBalanceForTheDate(final List<TxDataRow> transactionsForDate) {
		BigDecimal finalBalanceForDate = new BigDecimal("0");
		
		for (final TxDataRow txDataRow : transactionsForDate) {
			
			final BigDecimal creditAmt = txDataRow.getCreditAmount();
			final BigDecimal debitAmt = txDataRow.getDebitAmount();
			
			final BigDecimal signedAmt = creditAmt.compareTo(BigDecimal.ZERO) > 0 ? creditAmt : debitAmt.negate();
			
			finalBalanceForDate = finalBalanceForDate.add(signedAmt);
		}
		
		return finalBalanceForDate;
	}
	

	/**
	 * @param remoteTotal
	 * @param localTotal
	 * @return
	 */
	private boolean remoteLocalPositiveAndMatch(BigDecimal remoteTotal, BigDecimal localTotal) {
		return remoteTotal.compareTo(BigDecimal.ZERO) > 0 && localTotal.compareTo(BigDecimal.ZERO) > 0 && remoteTotal.compareTo(localTotal) == 0;
	}
	
	/**
	 * @param currentDte 
	 * @param remoteTransactionsForDate
	 * @param localTransactionsForDate
	 * @param outputRows 
	 * @param runningBalance 
	 */
	private void outputTransactionsWithBalancesForDate(final LocalDate currentDate, final Pair<List<TxDataRow>, BigDecimal> remoteTransactionsForDate,
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
