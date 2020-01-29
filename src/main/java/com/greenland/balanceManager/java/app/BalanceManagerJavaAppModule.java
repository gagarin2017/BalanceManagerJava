package com.greenland.balanceManager.java.app;

import com.google.inject.AbstractModule;
import com.greenland.balanceManager.java.app.services.TransactionComparatorService;
import com.greenland.balanceManager.java.app.services.TransactionComparatorServiceImpl;

public class BalanceManagerJavaAppModule extends AbstractModule	 {
	
	@Override
	protected void configure() {
		
	    bind(TransactionComparatorService.class).to(TransactionComparatorServiceImpl.class);

	}

}
