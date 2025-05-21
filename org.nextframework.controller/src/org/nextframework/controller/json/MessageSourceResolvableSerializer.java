package org.nextframework.controller.json;

import java.io.IOException;
import java.util.Locale;

import org.nextframework.core.standard.Next;
import org.springframework.context.MessageSourceResolvable;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

class MessageSourceResolvableSerializer extends JsonSerializer<MessageSourceResolvable> {

	private Locale locale;

	public MessageSourceResolvableSerializer(Locale locale) {
		this.locale = locale;
	}

	@Override
	public void serialize(MessageSourceResolvable value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		if (value == null) {
			jgen.writeNull();
		} else {
			String valueStr = Next.getMessageSource().getMessage(value, locale);
			jgen.writeString(valueStr);
		}
	}

}
