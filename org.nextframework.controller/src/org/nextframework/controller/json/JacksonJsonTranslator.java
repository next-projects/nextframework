package org.nextframework.controller.json;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.TimeZone;

import org.nextframework.exception.NextException;
import org.nextframework.message.MessageResolver;
import org.nextframework.types.Cep;
import org.springframework.context.MessageSourceResolvable;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;

public class JacksonJsonTranslator implements JsonTranslator {

	public ObjectMapper createObjectMapper() {
		return createObjectMapper(null);
	}

	public ObjectMapper createObjectMapper(MessageResolver messageResolver) {

		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.setTimeZone(TimeZone.getDefault());
		//mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

		SimpleModule nextModule = new SimpleModule("NextModule", new Version(1, 0, 0, null, "org.nextframework", "next-controller"));
		nextModule.addSerializer(Cep.class, new CepSerializer());
		nextModule.addDeserializer(Cep.class, new CepDeserializer());
		nextModule.addSerializer(Throwable.class, new ThrowableSerializer());
		if (messageResolver != null) {
			nextModule.addSerializer(MessageSourceResolvable.class, new MessageSourceResolvableSerializer(messageResolver));
		}

		mapper.registerModule(nextModule);

		return mapper;
	}

	public String toJson(Object o) {
		ObjectMapper mapper = createObjectMapper();
		Writer strWriter = new StringWriter();
		try {
			mapper.writeValue(strWriter, o);
		} catch (Exception e) {
			throw new NextException("Error transforming object to json.", e);
		}
		return strWriter.toString();
	}

	public <E> E fromJson(String json, Class<E> type) {
		ObjectMapper mapper = createObjectMapper();
		try {
			return mapper.readValue(json, type);
		} catch (Exception e) {
			throw new NextException("Error transforming json to object.", e);
		}
	}

	public <E> List<E> fromJsonAsList(String json, Class<E> type) {
		ObjectMapper mapper = createObjectMapper();
		try {
			CollectionType ctype = mapper.getTypeFactory().constructCollectionType(List.class, type);
			return mapper.readValue(json, ctype);
		} catch (Exception e) {
			throw new NextException("Error transforming json to object.", e);
		}
	}

}