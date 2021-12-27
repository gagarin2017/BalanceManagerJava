package com.greenland.balanceManager.java.app.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import com.greenland.balanceManager.java.app.model.TxDataRow;

@Deprecated
public class OutputTransactionsDeserializer extends StdDeserializer<Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>>> {

    public OutputTransactionsDeserializer() { 
        this(null); 
    } 

    public OutputTransactionsDeserializer(Class<?> vc) { 
        super(vc); 
    }

	@Override
	public Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JacksonException {

		final Map<LocalDate, Pair<List<TxDataRow>, BigDecimal>> result = new HashMap<>();
		
		JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        int id = (Integer) ((IntNode) node.get("id")).numberValue();
        String itemName = node.get("itemName").asText();
        int userId = (Integer) ((IntNode) node.get("createdBy")).numberValue();
		
		return result;
	}

}
