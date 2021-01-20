/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.inject.Guice
 *  com.google.inject.Injector
 *  com.google.inject.Module
 *  com.greenland.balanceManager.java.app.BalanceManagerJavaAppModule
 *  com.greenland.balanceManager.java.app.model.TxDataRow
 *  com.greenland.balanceManager.java.app.services.TransactionComparatorService
 */
package com.greenland.balanceManager.java.app.external;

import java.math.BigDecimal;
import java.util.List;

import org.json.JSONObject;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.greenland.balanceManager.java.app.BalanceManagerJavaAppModule;
import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
import com.greenland.balanceManager.java.app.model.TxDataRow;
import com.greenland.balanceManager.java.app.services.TransactionComparatorService;

public class BalanceManagerExternalImpl implements BalanceManagerExternal {
   
	private TransactionComparatorService transactionComparatorService;

    public BalanceManagerExternalImpl() {
        Injector injector = Guice.createInjector((Module[])new Module[]{new BalanceManagerJavaAppModule()});
        this.transactionComparatorService = (TransactionComparatorService)injector.getInstance(TransactionComparatorService.class);
    }

    @Override
    public List<TxDataRow> getAllTransactions() throws TransactionsNotFoundAtSourceException {
        return this.transactionComparatorService.getAllTransactions();
    }

	@Override
	public JSONObject getBalanceComparison(final JSONObject remoteTransactions, final JSONObject localTransactions, final BigDecimal startingBalance) throws TransactionsNotFoundAtSourceException {
		return transactionComparatorService.executeTransactionComparison(remoteTransactions, localTransactions, startingBalance);
	}
}
