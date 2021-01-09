package com.greenland.balanceManager.java.app.services

import spock.lang.Specification

import java.time.LocalDate

import org.apache.commons.lang3.tuple.Pair
import org.json.JSONObject

import com.greenland.balanceManager.java.app.model.TxDataRow

class SpockTransactionsBalanceAnalyzerTest extends Specification  {

	TransactionsBalanceAnalyzerImpl app

	AsciiTableDrawingService service = Mock()
	
	LocalDate transactionsDate = LocalDate.of(2020,5,2)

	def setup() {
		app = new TransactionsBalanceAnalyzerImpl(service)
	}

	void "Testing if the draw Ascii table is executed with the right parameters"() {
		given:
		// Setting up the remote transactions
		List<TxDataRow> remoteTransactions = new ArrayList<>()
		remoteTransactions.add(new TxDataRow(transactionsDate, "accountName", "categoryName", true, true, new BigDecimal("20.11")))
		remoteTransactions.add(new TxDataRow(transactionsDate, "accountName1", "categoryName1", true, true, new BigDecimal("120.11")))
		
		Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>()
		remoteTransactionMap.put(transactionsDate, remoteTransactions)
		
		// Setting up the local transactions
		List<TxDataRow> localTransactions = new ArrayList<>()
		localTransactions.add(new TxDataRow(transactionsDate, "accountName2", "categoryName2", true, false, new BigDecimal("-6.01")))
		localTransactions.add(new TxDataRow(transactionsDate, "accountName3", "categoryName3", true, false, new BigDecimal("-6.01")))
		
		Map<LocalDate, List<TxDataRow>> localTransactionMap = new HashMap<>()
		localTransactionMap.put(transactionsDate, localTransactions)
		
		BigDecimal startingBalance = new BigDecimal("25.55")

		Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> expectedRemote = new HashMap<>()
		expectedRemote.put(transactionsDate, Pair.of(remoteTransactions, 140.22))
		
		Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> expectedLocal = new HashMap<>()
		expectedLocal.put(transactionsDate, Pair.of(localTransactions, 12.02))
		
		when:
		app.analyzeTransactionBalances(remoteTransactionMap, localTransactionMap, startingBalance)

		then:
//		1 * service.drawAsciiTable(*_)		// any argument list (including the empty argument list)
		1 * service.drawAsciiTable(expectedRemote, expectedLocal, startingBalance)
//		1 * service.drawAsciiTable({it.contains(startingBalance)})		// any argument list (including the empty argument list)
	}
}