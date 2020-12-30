package com.greenland.balanceManager.java.app.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
import com.greenland.balanceManager.java.app.model.TxDataRow;
import com.greenland.balanceManager.java.app.services.TransactionDataRowService;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;

@Disabled
public class TransactionsFileReaderTest {
	
	private static final String LOCAL_FILE_ABS_PATH = "\\C:\\ExcelFilesToCompare\\111.txt";
	private static final String REMOTE_FILE_ABS_PATH = "\\C:\\ExcelFilesToCompare\\222.csv";
	
	private static final String TEST_DATA_DIR = "build\\resources\\test";
	
	@Tested
	private TransactionsFileReader transactionsFileReader;
	
	@Injectable
	private TransactionDataRowService transactionDataRowService;
	
	@Mocked
	private TxDataRow txDataRow;
	
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
    void populateTxMapsFromSource_filePaths_usedToFetchTheTransactions() throws TransactionsNotFoundAtSourceException, FileNotFoundException {
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
    @DisplayName("Read Test file 01 (csv) and see if all lines are read (remote)")
    void readTransactionsFromTheFile_testFile_Remote_six_lines_in_file() throws TransactionsNotFoundAtSourceException {
    	// Setup
    	final String testFileName = TEST_DATA_DIR + TransactionsFileReader.FS + "readTransactionsFromTheFile_01.csv";
    	final int numberOfLinesInFile = 10;
    	
    	new Expectations() {
    		{
    			transactionDataRowService.parseRemoteFileTransaction(anyString);
    			result = txDataRow;
    			times = numberOfLinesInFile;
    			
    			transactionDataRowService.parseLocalFileTransaction(anyString);
    			result = txDataRow;
    			times = 0;
    		}
    	};
    	
    	// Method under test
    	final Map<LocalDate, List<TxDataRow>> resultTransactionsMap = transactionsFileReader.readTransactionsFromTheFile(testFileName, true);
    	
    	assertThat("All lines are valid \"transactions\" - one Entry in map ", resultTransactionsMap.size(), is(1));
    	assertThat("Check if all \"transactions\" are read from the file", resultTransactionsMap.entrySet().iterator().next().getValue().size(), is(numberOfLinesInFile));
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
    @DisplayName("Read Test file 01 (txt) and see if all lines are read (local)")
    void readTransactionsFromTheFile_testFile_Local_fourteen_lines_in_file() throws TransactionsNotFoundAtSourceException {
    	// Setup
    	final String testFileName = TEST_DATA_DIR + TransactionsFileReader.FS + "readTransactionsFromTheFile_01.txt";
    	final int numberOfLinesInFile = 14;
    	
    	new Expectations() {
    		{
    			transactionDataRowService.parseRemoteFileTransaction(anyString);
    			result = txDataRow;
    			times = 0;
    			
    			transactionDataRowService.parseLocalFileTransaction(anyString);
    			result = txDataRow;
    			times = numberOfLinesInFile;
    		}
    	};
    	
    	// Method under test
    	final Map<LocalDate, List<TxDataRow>> resultTransactionsMap = transactionsFileReader.readTransactionsFromTheFile(testFileName, false);

       	assertThat("All lines are valid \"transactions\" - one Entry in map ", resultTransactionsMap.size(), is(1));
    	assertThat("Check if all \"transactions\" are read from the file", resultTransactionsMap.entrySet().iterator().next().getValue().size(), is(numberOfLinesInFile));

   }
}
