package com.greenland.balanceManager.java.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
import com.greenland.balanceManager.java.app.external.BalanceManagerExternal;

public class TestApp {
    public static void main(String[] args) throws TransactionsNotFoundAtSourceException {
        Injector injector = Guice.createInjector((Module[])new Module[]{new BalanceManagerJavaAppModule()});
        BalanceManagerExternal balanceManagerExternal = (BalanceManagerExternal)injector.getInstance(BalanceManagerExternal.class);
        System.out.println(balanceManagerExternal.getAllTransactions());
    }
}