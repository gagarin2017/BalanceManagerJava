package com.greenland.balanceManager.java.app.model
import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.Shared

class SpockTxDataRowTest  extends Specification {
	
//	@Shared
//	def RECONCILED_FLAG_STRING = TxDataRow.RECONCILED_FLAG_STRING
	
	def "check if Reconciled is set properly for the TxDataRow, when valid value passed"() {
		given: "TxDataRow and reconciled valid string"
		final String reconciledValidString = "R"
		TxDataRow txDataRow = new TxDataRow();
	
		when: "setting the reconciled flag for the row"
		txDataRow.setReconsiled(reconciledValidString)
		
		then: "TxDataRow flag should be set to true"
		txDataRow.isReconsiled() == true
	}
	
	@Unroll
	def "if passed reconciled string is \"#reconciledString\", then TxDataRow.isReconciled() should return [#isReconciled]"() {
		given:
		def TxDataRow txDataRow = new TxDataRow();
	
		when: "setting the reconciled flag for the row"
		txDataRow.setReconsiled(reconciledString)
		
		then: "TxDataRow flag should be set"
		isReconciled == txDataRow.isReconsiled()
		
		where:
		reconciledString					||isReconciled
		TxDataRow.RECONCILED_FLAG_STRING	||true
		"Ro"								||false
		""									||false
		null								||false
	}
}
