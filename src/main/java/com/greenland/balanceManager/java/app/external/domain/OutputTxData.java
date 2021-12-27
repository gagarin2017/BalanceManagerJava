package com.greenland.balanceManager.java.app.external.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The output data to be sent to the front end application (React for example)
 * 
 * @author Yura
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class OutputTxData {
	
	private List<DailyTransactions> remoteTransactions;
	private List<DailyTransactions> localTransactions;
	private Map<String, List<DailyTransactions>> missingTransactions; // group name and transactions for the date
	
	@Builder.Default
	private List<String> errors = new ArrayList<>();
	 
	@Builder.Default
	private List<String> infoMessages = new ArrayList<>();
}
