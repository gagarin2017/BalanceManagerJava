package com.greenland.balanceManager.java.app;

import com.google.inject.AbstractModule;
import com.greenland.balanceManager.java.app.dao.TransactionsFileReader;
import com.greenland.balanceManager.java.app.dao.TransactionsSourceDao;
import com.greenland.balanceManager.java.app.services.TransactionComparatorService;
import com.greenland.balanceManager.java.app.services.TransactionComparatorServiceImpl;
import com.greenland.balanceManager.java.app.services.TransactionDataRowService;
import com.greenland.balanceManager.java.app.services.TransactionDataRowServiceImpl;
import com.greenland.balanceManager.java.app.services.TransactionsBalanceAnalyzer;
import com.greenland.balanceManager.java.app.services.TransactionsBalanceAnalyzerImpl;
import com.greenland.balanceManager.java.app.services.TransactionsReaderService;
import com.greenland.balanceManager.java.app.services.TransactionsReaderServiceImpl;
import com.greenland.balanceManager.java.app.services.TransactionsSizeComparator;
import com.greenland.balanceManager.java.app.services.TransactionsSizeComparatorImpl;

public class BalanceManagerJavaAppModule extends AbstractModule	 {
	
	@Override
	protected void configure() {
		
	    bind(TransactionComparatorService.class).to(TransactionComparatorServiceImpl.class);
	    bind(TransactionsReaderService.class).to(TransactionsReaderServiceImpl.class);
	    bind(TransactionsSourceDao.class).to(TransactionsFileReader.class);
	    bind(TransactionDataRowService.class).to(TransactionDataRowServiceImpl.class);
	    bind(TransactionsBalanceAnalyzer.class).to(TransactionsBalanceAnalyzerImpl.class);
	    bind(TransactionsSizeComparator.class).to(TransactionsSizeComparatorImpl.class);
	}

}
