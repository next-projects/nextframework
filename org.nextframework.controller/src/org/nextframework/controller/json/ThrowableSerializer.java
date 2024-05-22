package org.nextframework.controller.json;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

class ThrowableSerializer extends JsonSerializer<Throwable> {

	@Override
	public void serialize(Throwable value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		if (value == null) {
			jgen.writeNull();
		} else {
			//jgen.writeString("error");
			//jgen.writeObjectFieldStart("error"); // "error" : {
			//jgen.writeFieldName("error"); // "error" :
			//jgen.writeStartArray(); // [
			//jgen.writeEndArray(); // ]
			jgen.writeStartObject(); // {
			jgen.writeStringField("error", getFullExceptionMessages(value)); // "error" : "Nonononon nononon"
			jgen.writeEndObject(); // }
		}
	}

	private String getFullExceptionMessages(Throwable t) {
		String txt = "";
		Set<Throwable> allCauses = new HashSet<Throwable>(); //para evitar recursividade
		Throwable cause = t;
		String lastMsg = null;
		do {
			String msg = cause.getMessage() != null ? cause.getMessage() : cause.toString();
			if (lastMsg == null || !msg.equals(lastMsg)) {
				txt += (txt.length() > 0 ? " --> " : "") + msg;
			}
			lastMsg = msg;
			cause = cause.getCause();
			if (allCauses.contains(cause)) {
				cause = null;
			} else {
				allCauses.add(cause);
			}
		} while (cause != null);
		return txt;
	}

}