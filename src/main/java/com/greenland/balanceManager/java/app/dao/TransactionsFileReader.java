package com.greenland.balanceManager.java.app.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.greenland.balanceManager.java.app.model.TxDataRow;
import com.greenland.balanceManager.java.app.services.TransactionDataRowService;

/**
 * {@link TransactionsSourceDao} dao implementation to read the transactions from the file.
 * 
 * @author Jura
 *
 */
public class TransactionsFileReader implements TransactionsSourceDao {
	
	private static Logger logger = LogManager.getLogger(TransactionsFileReader.class);

	private static final String CONFIG_PROPERTIES_FILE_NAME = "config.properties";
	public static final String FS = System.getProperty("file.separator");
	
	@Inject
	private TransactionDataRowService transactionDataRowService;
	
	@Override
	public void populateTxMapsFromSource(final Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			final Map<LocalDate, List<TxDataRow>> localTransactionMap) throws FileNotFoundException {
		
    	final String[] fileNames = getFileNamesFromPropertyFile();
    	
    	logger.debug("Getting remote transactions from: {}", fileNames[0]);
    	remoteTransactionMap.putAll(readTransactionsFromTheFile(fileNames[0], true));
    	
    	logger.debug("Getting local transactions from: {}", fileNames[1]);
    	localTransactionMap.putAll(readTransactionsFromTheFile(fileNames[1], false));
	}

	/**
	 * Read the config file for the file paths
	 * 
	 * @return array where first element is remote file name and second element - local file name
	 */
	public String[] getFileNamesFromPropertyFile() {
		
		String remoteFile = "";
		String localFile = "";

		try (final InputStream input = TransactionsFileReader.class.getClassLoader().getResourceAsStream(getConfigPropertiesFileName())) {

			final Properties prop = new Properties();

			// load a properties file
			prop.load(input);

			final String txFilesDirName = prop.getProperty("directoryName");
			final String remoteFileNameFromProperties = prop.getProperty("remoteFileName");
			final String localFileNameFromProperties = prop.getProperty("localFileName");

			remoteFile = FS + txFilesDirName + FS + remoteFileNameFromProperties;
			localFile = FS + txFilesDirName + FS + localFileNameFromProperties;

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return new String[] {remoteFile, localFile};
	}
	
	/**
	 * @param fileName
	 * @param isRemote
	 * @return
	 * @throws FileNotFoundException 
	 */
	@Override
	public Map<LocalDate, List<TxDataRow>> readTransactionsFromTheFile(final String fileName, boolean isRemote)
			throws FileNotFoundException {

		logger.debug("Reading file [{}]. Remote?: {}", fileName, isRemote);
		final Map<LocalDate, List<TxDataRow>> transactionsMap = new HashMap<>();

		final File transactionFile = new File(fileName);
		final Scanner fileReader = new Scanner(transactionFile);

		while (fileReader.hasNextLine()) {
			final String data = fileReader.nextLine();
//			logger.debug("Reading file line [{}].", data);
			final TxDataRow txDataRow = isRemote ? transactionDataRowService.parseRemoteFileTransaction(data)
					: transactionDataRowService.parseLocalFileTransaction(data);
			if (txDataRow != null /* && !txDataRow.isReconsiled() */) {
//				logger.debug("Saving transaction [{}].", txDataRow);
				putTxDataRowToMap(transactionsMap, txDataRow);
			}
		}

		fileReader.close();

		return sortMapByTxDate(transactionsMap);
	}
	
	/**
	 * @param missingTxMap
	 * @param remoteTxDataRow
	 */
	private void putTxDataRowToMap(final Map<LocalDate, List<TxDataRow>> missingTxMap,
			final TxDataRow remoteTxDataRow) {
		final List<TxDataRow> missingTxList;
		
		if(!missingTxMap.isEmpty() && missingTxMap.containsKey(remoteTxDataRow.getTxDate())) {
			missingTxList = missingTxMap.get(remoteTxDataRow.getTxDate());
		} else {
			missingTxList = new ArrayList<>();
		}
		
		missingTxList.add(remoteTxDataRow);
		missingTxMap.put(remoteTxDataRow.getTxDate(), missingTxList);
	}
	

	/**
	 * @param unsortedMap
	 * @return
	 */
	private static TreeMap<LocalDate, List<TxDataRow>> sortMapByTxDate(
			final Map<LocalDate, List<TxDataRow>> unsortedMap) {
		
		return unsortedMap.entrySet().stream()
				.sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
						(oldValue, newValue) -> oldValue, TreeMap::new));
	}
	
	public String getConfigPropertiesFileName() {
		return CONFIG_PROPERTIES_FILE_NAME;
	}
}
