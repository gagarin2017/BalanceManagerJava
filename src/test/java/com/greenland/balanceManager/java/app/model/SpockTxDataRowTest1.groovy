package com.greenland.balanceManager.java.app.model

import static org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

class SpockTxDataRowTest1 {

	@Test
	void test() {
		given:
		int input1 = 10
		int input2 = 25

		when:
		int result = input1 + input2

		then:
		result == 35
	}

}
