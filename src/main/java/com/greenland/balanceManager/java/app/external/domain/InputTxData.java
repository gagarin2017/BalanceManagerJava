package com.greenland.balanceManager.java.app.external.domain;

import java.math.BigDecimal;
import java.util.List;

import com.greenland.balanceManager.java.app.model.TxDataRow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The input data coming from the front end application (React for example)
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
public class InputTxData {
	
	private List<TxDataRow> remoteAccountTxsData;
	private List<TxDataRow> localAccountTxsData;
	
	private BigDecimal startingBalance;

}
