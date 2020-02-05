package com.greenland.balanceManager.java.app.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.greenland.balanceManager.java.app.model.TransactionsFileReader;
import com.greenland.balanceManager.java.app.model.TxDataRow;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;

public class TransactionsFileReaderTest {
	
	private static final String LOCAL_FILE_ABS_PATH = "\\C:\\Users\\jura_\\Documents\\ExcelFilesToCompare\\111.txt";
	private static final String REMOTE_FILE_ABS_PATH = "\\C:\\Users\\jura_\\Documents\\ExcelFilesToCompare\\222.csv";
	
	private static final String TEST_DATA_DIR = "build\\resources\\test";
	
	@Tested
	private TransactionsFileReader transactionsFileReader;
	
    @Test
    @DisplayName("Config file is incorrect. File doesnt exist - exception thrown")
    void getFileNamesFromPropertyFile_wrong_filename_exception() {
    	// Setup
    	final String wrongFileName = "blah.config";
    	
    	  new MockUp<TransactionsFileReader> () {
    		    @Mock public String getConfigPropertiesFileName() { return wrongFileName; }
    		  };
    	
    	final Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
    	final Map<LocalDate, List<TxDataRow>> localTransactionMap = new HashMap<>();
    	
    	// Method under test + Verifications
		assertThrows(NullPointerException.class, 
    			() -> transactionsFileReader.populateTxMapsFromSource(remoteTransactionMap, localTransactionMap));
    }
    
    @Test
    @DisplayName("Check if remote / file names are correct in config path")
    void getFileNamesFromPropertyFile_FileNames_Read_OK() {
    	// Setup
    	final String[] expectedFileNameArray = new String[] {REMOTE_FILE_ABS_PATH, LOCAL_FILE_ABS_PATH};
    	
    	// Method under test
    	final String[] resultFileNameArray = transactionsFileReader.getFileNamesFromPropertyFile();
    	
    	// Verifications
    	assertThat(resultFileNameArray, is(expectedFileNameArray));
    }
	
    @Test
    @DisplayName("File names from the config used to fetch remote and local transactions")
    void populateTxMapsFromSource_filePaths_usedToFetchTheTransactions() throws FileNotFoundException {
    	// Setup
    	final Map<LocalDate, List<TxDataRow>> remoteTransactionMap = new HashMap<>();
    	final Map<LocalDate, List<TxDataRow>> localTransactionMap = new HashMap<>();
    	
    	final String[] fileNameArray = new String[] {"remoteFilePath", "localFilePath"};
    	
    	new Expectations(transactionsFileReader) {
    		{
    			transactionsFileReader.getFileNamesFromPropertyFile();
    			result = fileNameArray;
    			
    			transactionsFileReader.readTransactionsFromTheFile(fileNameArray[0], true);
    			times = 1;
    			
    			transactionsFileReader.readTransactionsFromTheFile(fileNameArray[1], false);
    			times = 1;
    		}
    	};
    	
    	// Method under test
    	transactionsFileReader.populateTxMapsFromSource(remoteTransactionMap, localTransactionMap);
    }
    
    @Test
    @Tag("integrationTest")
    @DisplayName("Read Test file 01 (csv) and check the transactions (remote)")
    void readTransactionsFromTheFile_testFile_Remote_Valid_4_transactions_on_the_sameDay() throws IOException {
    	// Setup
    	final String testFileName = TEST_DATA_DIR + TransactionsFileReader.FS + "readTransactionsFromTheFile_01.csv";
    	final int noOfLinesInFile = 6;

    	new Expectations() {
    		{
				TxDataRow.parseRemoteFileTransaction(anyString);
				times = noOfLinesInFile;
				
				TxDataRow.parseLocalFileTransaction(anyString);
				times = 0;
    		}
    	};
    	
    	// Method under test
    	final Map<LocalDate, List<TxDataRow>> resultTransactionsMap = transactionsFileReader.readTransactionsFromTheFile(testFileName, true);
    	
    	assertThat("Only one record as all transactions occurred on the same date", resultTransactionsMap.size(), is(1));
    	assertThat("4 valid lines / transactions in the file", resultTransactionsMap.entrySet().iterator().next().getValue().size(), is(4));
    }
    
    @Test
    @Tag("integrationTest")
    @DisplayName("Read not existent file (remote). Exception thrown")
    void readTransactionsFromTheFile_wrong_remoteTx_fileName_exception() {
    	// Setup
    	final String testFileName = TEST_DATA_DIR + TransactionsFileReader.FS + "fileDontExist.csv";
    	
    	// Method under test
    	assertThrows(FileNotFoundException.class, 
    			() -> transactionsFileReader.readTransactionsFromTheFile(testFileName, true));
    }
    
    @Test
    @Tag("integrationTest")
    @DisplayName("Read not existent file (local). Exception thrown")
    void readTransactionsFromTheFile_wrong_localTx_fileName_exception() {
    	// Setup
    	final String testFileName = TEST_DATA_DIR + TransactionsFileReader.FS + "fileDontExist.csv";
    	
    	// Method under test
    	assertThrows(FileNotFoundException.class, 
    			() -> transactionsFileReader.readTransactionsFromTheFile(testFileName, false));
    }
    
    @Test
    @Tag("integrationTest")
    @DisplayName("Read Test file 01 (txt) and check the transactions (local)")
    void readTransactionsFromTheFile_testFile_Local_Valid_7_transactions_on_2_dates() throws IOException {
    	// Setup
    	final String testFileName = TEST_DATA_DIR + TransactionsFileReader.FS + "readTransactionsFromTheFile_01.txt";
    	final int noOfLinesInFile = 1;

    	final LocalDate expectedDate1 = LocalDate.of(2016, 11, 1);
    	final LocalDate expectedDate2 = LocalDate.of(2017, 1, 23);

    	new Expectations() {
    		{
//				TxDataRow.parseRemoteFileTransaction(anyString);
//				times = noOfLinesInFile;
//				
//				TxDataRow.parseLocalFileTransaction(anyString);
//				times = 0;
    		}
    	};
    	
    	// Method under test
    	final Map<LocalDate, List<TxDataRow>> resultTransactionsMap = transactionsFileReader.readTransactionsFromTheFile(testFileName, false);
    	
    	assertThat("Check for 2 entries as transactions occurred on two separate dates", resultTransactionsMap.size(), is(2));
		assertTrue(resultTransactionsMap.containsKey(expectedDate1));
		assertTrue(resultTransactionsMap.containsKey(expectedDate2));
    	
    	assertThat("1 valid lines / transactions in the file for the date "+expectedDate1, resultTransactionsMap.get(expectedDate1).size(), is(1));
    	assertThat("6 valid lines / transactions in the file for the date "+expectedDate2, resultTransactionsMap.get(expectedDate2).size(), is(6));
    }
}
