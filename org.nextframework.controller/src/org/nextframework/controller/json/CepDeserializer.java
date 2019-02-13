package org.nextframework.controller.json;

import java.io.IOException;

import org.nextframework.types.Cep;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

class CepDeserializer extends JsonDeserializer<Cep> {

	@Override
	public Cep deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String value = jp.readValueAs(String.class);
		if (value == null) {
			return null;
		}
		return new Cep(value);
	}
}