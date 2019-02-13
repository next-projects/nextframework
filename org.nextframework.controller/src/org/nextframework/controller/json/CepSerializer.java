package org.nextframework.controller.json;

import java.io.IOException;

import org.nextframework.types.Cep;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

class CepSerializer extends JsonSerializer<Cep> {
	
	@Override
	public void serialize(Cep value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		if (value == null) {
			jgen.writeNull();
		} else {
			jgen.writeString(value.toString());
		}
	}
	
}