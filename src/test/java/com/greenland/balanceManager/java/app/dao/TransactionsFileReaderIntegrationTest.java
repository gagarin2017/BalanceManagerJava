package com.greenland.balanceManager.java.app.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.greenland.balanceManager.java.app.BalanceManagerJavaAppModule;
import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
import com.greenland.balanceManager.java.app.model.TxDataRow;

public class TransactionsFileReaderIntegrationTest {
	
	private static final String TEST_DATA_DIR = "build\\resources\\test";
	
	private static TransactionsSourceDao transactionsFileReader;

	@BeforeAll
	public static void setup() {
		final Injector injector = Guice.createInjector(new BalanceManagerJavaAppModule());
        transactionsFileReader = injector.getInstance(TransactionsSourceDao.class);
	}
	
    @Test
    @DisplayName("Read Test file 01 (csv) and see if all lines are read (remote).")
    void readTransactionsFromTheFile_testFile_Remote_six_lines_in_file_duplicates() throws TransactionsNotFoundAtSourceException {
    	// Setup
    	final String testFileName = TEST_DATA_DIR + TransactionsFileReader.FS + "readTransactionsFromTheFile_02.csv";
    	final LocalDate day1 = LocalDate.of(2017, 12, 21);
    	final LocalDate day2 = LocalDate.of(2017, 12, 22);
    	final LocalDate day3 = LocalDate.of(2017, 12, 28);
    	final LocalDate day4 = LocalDate.of(2018, 1, 8);
    	
    	// Method under test
    	final Map<LocalDate, List<TxDataRow>> resultTransactionsMap = transactionsFileReader.readTransactionsFromTheFile(testFileName, true);
    	
    	assertThat(resultTransactionsMap.size(), not(0));
    	
    	assertThat("Transactions for four days, hence 4 entries ", resultTransactionsMap.size(), is(4));
    	assertThat("Check if number of transactions match day #1", resultTransactionsMap.get(day1).size(), is(2));
    	assertThat("Check if number of transactions match day #2", resultTransactionsMap.get(day2).size(), is(1));
    	assertThat("Check if number of transactions match day #3", resultTransactionsMap.get(day3).size(), is(1));
    	assertThat("Check if number of transactions match day #4", resultTransactionsMap.get(day4).size(), is(4));
    }
}
