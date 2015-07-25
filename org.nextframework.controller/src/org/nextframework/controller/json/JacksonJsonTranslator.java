package org.nextframework.controller.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.module.SimpleModule;

public class JacksonJsonTranslator implements JsonTranslator {
	public static boolean CREATE_DATES_WITH_CUSTOMPATTERN = true;
	
//	public static DateFormat customDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public static DateFormat customDateFormat = new SimpleDateFormat("'new Date('yyyy','MM','dd','HH','mm','ss','SSS')'");
	
	private static final class DateFormater extends JsonSerializer<Date> {
		public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
			if(value == null){
				jgen.writeNull();
			} else {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(value);
				calendar.add(Calendar.MONTH, -1); //the month is zero based
				jgen.writeRawValue(customDateFormat.format(calendar.getTime()));
			}
		}
	}


	@Override
	public String toJson(Object o) {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule nextModule = new SimpleModule("NextModule", new Version(1, 0, 0, null));
		if(CREATE_DATES_WITH_CUSTOMPATTERN){
			nextModule.addSerializer(Date.class, new DateFormater());
//			mapper.setDateFormat(customDateFormat);
//			mapper.disable(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS);
		}
		
		mapper.registerModule(nextModule);
		Writer strWriter = new StringWriter();
		try {
			mapper.writeValue(strWriter, o);
		} catch (Exception e) {
			throw new RuntimeException("Error transforming object to json.", e);
		}
		return strWriter.toString();
	}


	@Override
	public <E> E fromJson(String json, Class<E> type) {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule nextModule = new SimpleModule("NextModule", new Version(1, 0, 0, null));
		if(CREATE_DATES_WITH_CUSTOMPATTERN){
			nextModule.addSerializer(Date.class, new DateFormater());
//			mapper.setDateFormat(customDateFormat);
//			mapper.disable(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS);
		}
		
		mapper.registerModule(nextModule);
		
		try {
			return mapper.readValue(json, type);
		} catch (JsonParseException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
