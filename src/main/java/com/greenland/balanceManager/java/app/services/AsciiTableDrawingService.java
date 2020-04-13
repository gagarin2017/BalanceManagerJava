package com.greenland.balanceManager.java.app.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.greenland.balanceManager.java.app.model.TxDataRow;

import de.vandermeer.asciitable.AsciiTable;

public interface AsciiTableDrawingService {

	/**
	 * Draw nice table using {@link AsciiTable}
	 * 
	 * @param outputRows
	 */
	void drawAsciiTable(Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> remoteTransactionBalances,
			Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> localTransactionBalances, BigDecimal startingBalance);

}
