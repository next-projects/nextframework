package org.nextframework.controller.json;

import java.io.IOException;

import org.nextframework.message.MessageResolver;
import org.springframework.context.MessageSourceResolvable;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

class MessageSourceResolvableSerializer extends JsonSerializer<MessageSourceResolvable> {

	private MessageResolver messageResolver;

	public MessageSourceResolvableSerializer(MessageResolver messageResolver) {
		this.messageResolver = messageResolver;
	}

	@Override
	public void serialize(MessageSourceResolvable value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		if (value == null) {
			jgen.writeNull();
		} else {
			String valueStr = messageResolver.message(value);
			jgen.writeString(valueStr);
		}
	}

}