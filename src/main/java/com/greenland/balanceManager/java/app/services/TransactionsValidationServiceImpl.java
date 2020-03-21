package com.greenland.balanceManager.java.app.services;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.greenland.balanceManager.java.app.CommonUtils;

public class TransactionsValidationServiceImpl implements TransactionsValidationService {
	
	private static Logger logger = LogManager.getLogger(TransactionsValidationServiceImpl.class);

	@Override
	public Object[] isValidTransactionRow(final String[] txRowArray) {
		
		final Object[] isValidDate = new Object[2];
		isValidDate[0] = false;
		
		if (txRowArray.length > 1) {
			try {
				LocalDate txDate = LocalDate.parse(txRowArray[1], CommonUtils.DATE_TIME_FORMATTER);
				txDate = (txDate.isAfter(LocalDate.MIN) && txDate.isBefore(LocalDate.MAX)) 
						? txDate 
						: null;
				isValidDate[0] = true;
				isValidDate[1] = txDate;
			} catch (final DateTimeParseException ex) {
				logger.error("Is it valid date? {}", txRowArray[1]);
			}
		}
		
		return isValidDate;
	}
}
