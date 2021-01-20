package com.greenland.balanceManager.java.app.services;

import org.junit.jupiter.api.BeforeEach;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.greenland.balanceManager.java.app.BalanceManagerJavaAppModule;

public abstract class TestBase {

	protected Injector injector = Guice.createInjector(new BalanceManagerJavaAppModule());

    @BeforeEach
    public void setup () {
        injector.injectMembers(this);
    }
}
