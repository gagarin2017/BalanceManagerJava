package com.greenland.balanceManager.java.app;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.greenland.balanceManager.java.app.dao.TransactionsFileReader;
import com.greenland.balanceManager.java.app.dao.TransactionsJsonReader;
import com.greenland.balanceManager.java.app.dao.TransactionsSourceDao;
import com.greenland.balanceManager.java.app.services.AsciiTableDrawingService;
import com.greenland.balanceManager.java.app.services.AsciiTableDrawingServiceImpl;
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
import com.greenland.balanceManager.java.app.services.TransactionsValidationService;
import com.greenland.balanceManager.java.app.services.TransactionsValidationServiceImpl;

public class BalanceManagerJavaAppModule extends AbstractModule	 {
	
	@Override
	protected void configure() {
		
	    bind(TransactionComparatorService.class).to(TransactionComparatorServiceImpl.class);
	    bind(TransactionsReaderService.class).to(TransactionsReaderServiceImpl.class);
	    
	    // Reading the transactions from the files. Bind different implementation of the TransactionsSourceDao to use different transaction reader
	    bind(TransactionsSourceDao.class).annotatedWith(Names.named("TransactionsFileReader")).to(TransactionsFileReader.class);
	    bind(TransactionsSourceDao.class).annotatedWith(Names.named("TransactionsJsonReader")).to(TransactionsJsonReader.class);
	    
	    bind(TransactionDataRowService.class).to(TransactionDataRowServiceImpl.class);
	    bind(TransactionsBalanceAnalyzer.class).to(TransactionsBalanceAnalyzerImpl.class);
	    bind(TransactionsSizeComparator.class).to(TransactionsSizeComparatorImpl.class);
	    bind(TransactionsValidationService.class).to(TransactionsValidationServiceImpl.class);
	    bind(AsciiTableDrawingService.class).to(AsciiTableDrawingServiceImpl.class);
	}

}
