package org.nextframework.controller.json;

import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.nextframework.exception.ApplicationException;
import org.nextframework.types.Cep;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;

public class JacksonJsonTranslator implements JsonTranslator {

	public static DateFormat customDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	public ObjectMapper getObjectMapper() {

		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(customDateFormat);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.setSerializationInclusion(Include.NON_NULL);

		SimpleModule nextModule = new SimpleModule("NextModule", new Version(1, 0, 0, null, "org.nextframework", "next-controller"));
		nextModule.addSerializer(Date.class, new JsonJSDateSerializer());
		nextModule.addSerializer(Cep.class, new CepSerializer());
		nextModule.addDeserializer(Cep.class, new CepDeserializer());
		nextModule.addSerializer(Throwable.class, new ThrowableSerializer());
		mapper.registerModule(nextModule);

		return mapper;
	}

	public String toJson(Object o) {
		ObjectMapper mapper = getObjectMapper();
		Writer strWriter = new StringWriter();
		try {
			mapper.writeValue(strWriter, o);
		} catch (Exception e) {
			throw new ApplicationException("Error transforming object to json.", e);
		}
		return strWriter.toString();
	}

	public <E> E fromJson(String json, Class<E> type) {
		ObjectMapper mapper = getObjectMapper();
		try {
			return mapper.readValue(json, type);
		} catch (Exception e) {
			throw new ApplicationException("Error transforming json to object.", e);
		}
	}

	public <E> List<E> fromJsonAsList(String json, Class<E> type) {
		ObjectMapper mapper = getObjectMapper();
		try {
			CollectionType ctype = mapper.getTypeFactory().constructCollectionType(List.class, type);
			return mapper.readValue(json, ctype);
		} catch (Exception e) {
			throw new ApplicationException("Error transforming json to object.", e);
		}
	}

}