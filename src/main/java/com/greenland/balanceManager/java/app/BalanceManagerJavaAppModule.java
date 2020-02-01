package com.greenland.balanceManager.java.app;

import com.google.inject.AbstractModule;
import com.greenland.balanceManager.java.app.model.TransactionsFileReader;
import com.greenland.balanceManager.java.app.model.TransactionsSourceDao;
import com.greenland.balanceManager.java.app.services.TransactionComparatorService;
import com.greenland.balanceManager.java.app.services.TransactionComparatorServiceImpl;
import com.greenland.balanceManager.java.app.services.TransactionsReaderService;
import com.greenland.balanceManager.java.app.services.TransactionsReaderServiceImpl;

public class BalanceManagerJavaAppModule extends AbstractModule	 {
	
	@Override
	protected void configure() {
		
	    bind(TransactionComparatorService.class).to(TransactionComparatorServiceImpl.class);
	    bind(TransactionsReaderService.class).to(TransactionsReaderServiceImpl.class);
	    bind(TransactionsSourceDao.class).to(TransactionsFileReader.class);
	    
	}

}
