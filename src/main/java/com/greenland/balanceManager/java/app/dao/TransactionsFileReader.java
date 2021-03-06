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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.greenland.balanceManager.java.app.exceptions.TransactionsNotFoundAtSourceException;
import com.greenland.balanceManager.java.app.model.TxDataRow;
import com.greenland.balanceManager.java.app.services.TransactionDataRowService;

/**
 * {@link TransactionsSourceDao} dao implementation to read the transactions from the file.
 * 
 * @author Jura
 *
 */
public class TransactionsFileReader implements TransactionsSourceFileDao {
	
	private static Logger logger = LogManager.getLogger(TransactionsFileReader.class);

	private enum FileExtensions {CSV, TXT};
	
	private static final String CONFIG_PROPERTIES_FILE_NAME = "config.properties";
	public static final String FS = System.getProperty("file.separator");
	
	@Inject
	private TransactionDataRowService transactionDataRowService;
	
	@Override
	public void populateTxMapsFromSource(final Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			final Map<LocalDate, List<TxDataRow>> localTransactionMap, Object... sources) throws TransactionsNotFoundAtSourceException {
		
    	final String[] fileNames = getFileNamesFromPropertiesFile();
    	
    	logger.debug("Getting remote transactions from the file: {}", fileNames[0]);
		remoteTransactionMap.putAll(readTransactionsFromTheFile(fileNames[0]));
		
		logger.debug("Getting local transactions from the file: {}", fileNames[1]);
		localTransactionMap.putAll(readTransactionsFromTheFile(fileNames[1]));
    	
	}
	
	@Override
	public void populateTxMapsFromSource(final Map<LocalDate, List<TxDataRow>> remoteTransactionMap,
			final String remoteFile, final Map<LocalDate, List<TxDataRow>> localTransactionMap, final String localFile)
			throws TransactionsNotFoundAtSourceException {

		logger.debug("Getting remote transactions from the file: {}", remoteFile);
		remoteTransactionMap.putAll(readTransactionsFromTheFile(remoteFile));

		logger.debug("Getting local transactions from the file: {}", localFile);
		localTransactionMap.putAll(readTransactionsFromTheFile(localFile));
	}

	/**
	 * Read the config file for the file paths
	 * 
	 * @return array where first element is remote file name and second element - local file name
	 */
	public String[] getFileNamesFromPropertiesFile() {
		
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
	

	@Override
	public Map<LocalDate, List<TxDataRow>> readTransactionsFromTheFile(final String fileName) throws TransactionsNotFoundAtSourceException {
		String fileExt = "";
		boolean isRemote = false;
		final Pattern pattern = Pattern.compile("\\.{1}\\w{3}");
		final Matcher matcher = pattern.matcher(fileName);
		
		if(matcher.find()) {
			fileExt = matcher.group().substring(1);
			isRemote = fileExt.equalsIgnoreCase(FileExtensions.CSV.name()) ? true : false;
		}
		
		return readTransactionsFromTheFile(fileName, isRemote);
	}
	
	/**
	 * Read the transactions from the file.
	 * 
	 * @param fileName
	 * @param isRemote
	 * @return  Map<LocalDate, List<TxDataRow>> - map of transactions by date
	 * @throws FileNotFoundException 
	 */
	@Override
	@Deprecated
	public Map<LocalDate, List<TxDataRow>> readTransactionsFromTheFile(final String fileName, boolean isRemote)
			throws TransactionsNotFoundAtSourceException {

		final Map<LocalDate, List<TxDataRow>> transactionsMap = new HashMap<>();

		try {
			logger.debug("Reading file [{}]. Remote?: {}", fileName, isRemote);
			
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
		} catch (final FileNotFoundException ex) {
			throw new TransactionsNotFoundAtSourceException("necessary file(-s) was not present or error while reading the file", ex);
		}

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
